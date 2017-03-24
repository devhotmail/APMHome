package com.ge.apm.service.wo;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author 212547631
 */
@Service
public class WorkOrderService {
    private static final Logger logger = Logger.getLogger(WorkOrderService.class);

    @Autowired
    private SiteInfoRepository siteDao;
    @Autowired
    private WorkOrderStepRepository stepDao;
    @Autowired
    private WxMpService wxService;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    private WorkOrderRepository workOrderRepository;
    @Autowired
    WorkOrderStepRepository workOrderStepRepository;
    @Autowired
    I18nMessageRepository i18nMessageRepository;

    public List<WorkOrder> findWorkOrderByStatus(int status)throws Exception{
        List<WorkOrder> byStatus = workOrderRepository.findByStatus(status);
        return byStatus;
    }
    public  List<WorkOrder>  findWorkOrderByCon(HttpServletRequest request){
        List<SearchFilter> searchFilters = new ArrayList<>();


        UserAccount ua = UserContext.getCurrentLoginUser(request);
        System.out.println(request.getParameter("requestorId"));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, request.getParameter("status")));
        searchFilters.add(new SearchFilter("requestorId", SearchFilter.Operator.EQ, request.getParameter("requestorId")));
        return workOrderRepository.findBySearchFilter(searchFilters);
    }
    //public List<WorkOrder>

    public  List<WorkOrder>  findWorkOrderByContest(Integer requestorId){
        return workOrderRepository.findByRequestorId(requestorId);
    }

    public WorkOrderStep initWorkOrderCurrentStep(WorkOrder wo){
        WorkOrderStep woStep = new WorkOrderStep();

        woStep.setWorkOrderId(wo.getId());
        woStep.setSiteId(wo.getSiteId());

        if(wo.getCurrentPersonId()!=null) woStep.setOwnerId(wo.getCurrentPersonId());
        if(wo.getCurrentPersonName()!=null) woStep.setOwnerName(wo.getCurrentPersonName());

        woStep.setStepId(wo.getCurrentStepId());
        woStep.setStepName(WebUtil.getMessage("woSteps"+"-"+wo.getCurrentStepId()));

        woStep.setStartTime(TimeUtil.now());

        return woStep;
    }


    @Transactional
    public void workWorderCreate(Integer assetId)throws RuntimeException{
        /*  判断是否二次开单  --> 判断派工模式
        * 1（拿siteid和assetid还有hospitalid来作为该设备是否是唯一的）
        * */
        //select * from work_order where  (close_time::timestamp)::date > (select now() - interval '7 day')::date  and  asset_id =68 and hospital_id=2 and site_id=2
//gl:requestor 就是申请保修的,curent_person_d: 自动派单时为-1，手动派单时为设备科科长.
        //gl: select from user_account ua , sys_role  sr where sr.role_id =2 and ua.user_id = ?
         UserAccount currentUsr= UserContext.getCurrentLoginUser();
        WorkOrder neWorkOrder=null;
        List<WorkOrder> reopenWorkOrder = workOrderRepository.isReopenWorkOrder(2, currentUsr.getHospitalId(), currentUsr.getSiteId());

        //gl: assetHead当前资产负责人
        List<UserAccount> assetHead = userDao.getAssetHead(currentUsr.getSiteId(), currentUsr.getHospitalId());
//workOrder不为空 表示是二次开单了
        if(reopenWorkOrder.size() >0 && assetHead.size()>0){//true is reopen
            System.out.println("是二次开单的工单创建");
            //relating to parent parent_wo_id(work_order)
            neWorkOrder=initWorkOrder(assetId,assetHead.get(0),reopenWorkOrder.get(0),true);
        }else{ //非二次开单
            System.out.println("不是二次开单的工单创建");
            //非二次开单第三个参数就为空 即不需要父工单
            neWorkOrder=initWorkOrder(assetId,assetHead.get(0),null,false);

        }
        workOrderRepository.save(neWorkOrder);
    }
    @Transactional
    public  void assignWorkOrder(WorkOrderPoJo wopo) throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        Date date =new SimpleDateFormat("yyyy-MM-dd").parse(wopo.getStrDate());
        String desc = wopo.getDesc();
        int assigneeId = Integer.parseInt(wopo.getAssigneeId());
        WorkOrder wo = workOrderRepository.getById(woId);
        int currentStepId = wo.getCurrentStepId();

        //1 update work order table
        UserAccount user = userDao.getById(assigneeId);
        wo.setCurrentPersonId(user.getId());
        wo.setCurrentPersonName(user.getName());
        wo.setCurrentStepId(currentStepId+1);
        String stepName = i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps", String.valueOf(currentStepId+1)).getValueZh();
        wo.setCurrentStepName(stepName);
        workOrderRepository.save(wo);
        //2  update endtime in wos for last step workorder
        WorkOrderStep currentStep = workOrderStepRepository.getByWorkOrderIdAndStepId(woId,currentStepId).get(0);
        currentStep.setEndTime(new Date());
        workOrderStepRepository.save(currentStep);
        //3 create next work order step
        WorkOrderStep wds =  initWorkOrderStep(wo);//new WorkOrderStep();
        wds.setOwnerId(user.getId());
        wds.setOwnerName(user.getName());
        wds.setDescription(desc);
        workOrderStepRepository.save(wds);
    }


    @Transactional
    public void takeWorkOrder(WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        Date estimatedCloseTime =new SimpleDateFormat("yyyy-MM-dd").parse(wopo.getEstimatedCloseTime());
        wo.setEstimatedCloseTime(estimatedCloseTime);
        String desc = wopo.getDesc();

        //1 update work order
        workOrderUpdate(wo);
        //2 create new work-order-step
        WorkOrderStep wds = initWorkOrderStep(wo);
        wds.setDescription(desc);
        workOrderStepRepository.save(wds);
        // update end time for last work step
        updateEndTime(wo);

    }


    @Transactional
    public void signWorkOrder(WorkOrderPoJo wopo)throws  Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        workOrderUpdate(wo);
        WorkOrderStep wds = initWorkOrderStep(wo);
        workOrderStepRepository.save(wds);
        updateEndTime(wo);
    }
    @Transactional
    public void repair(WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        workOrderUpdate(wo);
        WorkOrderStep wds = initWorkOrderStep(wo);
        workOrderStepRepository.save(wds);
        updateEndTime(wo);
    }
    @Transactional
    public void closeWorkOrder(WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        wo.setStatus(2);
        workOrderRepository.save(wo);
        updateEndTime(wo);
    }
    @Transactional
    private void workOrderUpdate(WorkOrder wo)throws  Exception{
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        wo.setCurrentPersonId(currentUserAccount.getId());
        wo.setCurrentPersonName(currentUserAccount.getName());
        int currentStepId = wo.getCurrentStepId();
        wo.setCurrentStepId(currentStepId+1);
        String stepName = i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps", String.valueOf(currentStepId+1)).getValueZh();
        wo.setCurrentStepName(stepName);
        workOrderRepository.save(wo);
    }

    private WorkOrderStep initWorkOrderStep(WorkOrder wo ) throws Exception{
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        WorkOrderStep wds = new WorkOrderStep();
        wds.setOwnerId(currentUserAccount.getId());
        wds.setOwnerName(currentUserAccount.getName());
        wds.setStartTime(new Date());
        wds.setWorkOrderId(wo.getId());
        wds.setSiteId(wo.getSiteId());
        int currentStepId = wo.getCurrentStepId();
        wo.setCurrentStepId(currentStepId+1);
        String stepName = i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps", String.valueOf(currentStepId+1)).getValueZh();
        wo.setCurrentStepName(stepName);
        return wds;
    }
    @Transactional
    private void updateEndTime(WorkOrder wo)throws Exception{
        //2.1  update endtime in wos for last step workorder
        WorkOrderStep workOrderStep = workOrderStepRepository.getByWorkOrderIdAndStepId(wo.getId(),wo.getCurrentStepId()).get(0);
        List<WorkOrderStep> wosList =workOrderStepRepository.getByWorkOrderIdAndStepId(wo.getId(),wo.getCurrentStepId());
        if(wosList.size()>0){
            WorkOrderStep wos = wosList.get(0);
            wos.setEndTime(new Date());
            workOrderStepRepository.save(wos);
        }else{
            throw new Exception("work order step missing");
        }

    }

    private WorkOrder initWorkOrder(Integer assetId,UserAccount headerAccount,WorkOrder reopenWorkOrder,boolean isReopen){
        WorkOrder neWorkOrder = new WorkOrder();
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        neWorkOrder.setSiteId(currentUserAccount.getSiteId());
        neWorkOrder.setAssetName("assetnasssme");
        neWorkOrder.setRequestorId(currentUserAccount.getId());
        neWorkOrder.setRequestorName(currentUserAccount.getName());
        neWorkOrder.setRequestTime(new Date());
        neWorkOrder.setRequestReason("request reason");
        neWorkOrder.setCaseType(1);//---------from fonrt
        neWorkOrder.setCaseSubType(1);//-----
        neWorkOrder.setCasePriority(1);//---------
        neWorkOrder.setHospitalId(currentUserAccount.getHospitalId());
        if(isReopen){
            neWorkOrder.setParentWoId(reopenWorkOrder.getId());
        }
        neWorkOrder.setCurrentPersonId(headerAccount.getId());
        neWorkOrder.setCurrentPersonName(headerAccount.getName());
        neWorkOrder.setCurrentStepId(2);// gl: 表示步骤是开单
        neWorkOrder.setCurrentStepName("审核");
        neWorkOrder.setTotalManHour(0);//----?
        neWorkOrder.setTotalPrice(0.0);//----?


        neWorkOrder.setRequestTime(new Date());
        neWorkOrder.setRequestReason("reason");
        neWorkOrder.setCasePriority(1);
        neWorkOrder.setAssetId(assetId);
        neWorkOrder.setStatus(1);
        return neWorkOrder;
    }
    @Transactional
    public void saveWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep, WorkOrderStep nextWoStep) throws RuntimeException{
        WorkOrderRepository woDao = WebUtil.getBean(WorkOrderRepository.class);
        WorkOrderStepRepository woStepDao = WebUtil.getBean(WorkOrderStepRepository.class);
        if(wo!=null){
            try{
                wo.setCurrentStepName(WebUtil.getMessage("woSteps"+"-"+wo.getCurrentStepId()));
                woDao.save(wo);
            }
            catch(Exception ex){
                throw new RuntimeException("Failed to save WorkOrder:"+ex.getMessage());
            }
        }
        if(currentWoStep!=null){
            if(wo!=null){
                currentWoStep.setWorkOrderId(wo.getId());
            }

            try{
                currentWoStep.removeEmptyStepDetailRecord();
                woStepDao.save(currentWoStep);
                //保存step后会连带保存detail，再计算order上面的工时和总价
                saveTotalTimeAndPrice(woDao, wo, currentWoStep);
            }
            catch(Exception ex){
                throw new RuntimeException("Failed to save current WorkOrderStep:"+ex.getMessage());
            }
        }
        if(nextWoStep!=null){
            if(wo!=null){
                nextWoStep.setWorkOrderId(wo.getId());
            }

            try{
                nextWoStep.removeEmptyStepDetailRecord();
                woStepDao.save(nextWoStep);
            }
            catch(Exception ex){
                throw new RuntimeException("Failed to save next WorkOrderStep:"+ex.getMessage());
            }
        }
    }

    protected void checkWorkOrderPersonNames(WorkOrder wo){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        UserAccount user;

        user = uaaService.getUserById(wo.getRequestorId());
        if(user!=null)
            wo.setRequestorName(user.getName());
        else
            wo.setRequestorName("N/A");

        user = uaaService.getUserById(wo.getCurrentPersonId());
        if(user!=null)
            wo.setCurrentPersonName(user.getName());
        else
            wo.setCurrentPersonName("N/A");
    }

    public void createWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        finishWorkOrderStep(wo, currentWoStep);
    }

    public void finishWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        // update current Work Order Step
        currentWoStep.setEndTime(TimeUtil.now());

        // initiate next Work Order Step
        wo.setCurrentStepId(currentWoStep.getStepId()+1);
        checkWorkOrderPersonNames(wo);

        WorkOrderStep nextWoStep = initWorkOrderCurrentStep(wo);

        try{
            saveWorkOrderStep(wo, currentWoStep, nextWoStep);
        }
        catch(Exception ex){
            // rollback WorkOrder changes
            wo.setCurrentStepId(currentWoStep.getStepId());
            wo.setCurrentPersonId(currentWoStep.getOwnerId());
            wo.setCurrentPersonName(currentWoStep.getOwnerName());

            throw ex;
        }
        //do wechat push
        sendTemplateMsg(wo, currentWoStep, nextWoStep);

        //judge whether continue, if auto_stepX is true then continue
        int currentStep = wo.getCurrentStepId();
        if ( wo.getSiteId() == null) return;
        SiteInfo site = siteDao.findById(wo.getSiteId());
        if (site == null) return;
        List<WorkOrderStep> list = stepDao.getByWorkOrderIdAndStepId(wo.getId(), currentStep);
        if (list.size() == 0) return;
        for(WorkOrderStep step : list) {
            if (step.getEndTime() == null)
                currentWoStep = step;
        }
        switch (currentStep){
            case 2:if(site.getWfAutoStep2()){finishWorkOrderStep(wo, currentWoStep);}break;
            case 3:if(site.getWfAutoStep3()){finishWorkOrderStep(wo, currentWoStep);}break;
            case 4:if(site.getWfAutoStep4()){finishWorkOrderStep(wo, currentWoStep);}break;
            case 5:if(site.getWfAutoStep5()){finishWorkOrderStep(wo, currentWoStep);}break;
            case 6:if(site.getWfAutoStep6()){closeWorkOrder(wo, currentWoStep);}break;
            default:;
        }
    }

    public void closeWorkOrder(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        wo.setCurrentStepId(6);
        currentWoStep.setEndTime(TimeUtil.now());

        WorkOrderRepository woDao = WebUtil.getBean(WorkOrderRepository.class);
        WorkOrderStepRepository woStepDao = WebUtil.getBean(WorkOrderStepRepository.class);

        try{
            if (currentWoStep.getStepId() == 6){
                saveWorkOrderStep(wo, currentWoStep, null);
            } else {
                WorkOrderStep nextWoStep = initWorkOrderCurrentStep(wo);
                nextWoStep.setEndTime(TimeUtil.now());
                saveWorkOrderStep(wo, currentWoStep, nextWoStep);
            }
        }
        catch(Exception ex){
            throw ex;
        }
    }

    @Transactional
    public void transferWorkOrder(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        checkWorkOrderPersonNames(wo);
        WorkOrderStep nextWoStep = initWorkOrderCurrentStep(wo);

        try{
            saveWorkOrderStep(wo, currentWoStep, nextWoStep);
        }
        catch(Exception ex){
            throw ex;
        }
        //do wechat push
        sendTemplateMsg(wo, currentWoStep, null);
    }

    private void saveTotalTimeAndPrice(WorkOrderRepository woDao, WorkOrder wo, WorkOrderStep wos) {
        int totalManHour = wo.getTotalManHour();
        double totalPrice = wo.getTotalPrice();
        if ( wo.getSiteId() == null) return;
        SiteInfo site = siteDao.findById(wo.getSiteId());
        if (site == null) return;
        double hourPrice = site.getManhourPrice()==null?0.0:site.getManhourPrice();
        List<WorkOrderStepDetail> details = wos.getStepDetails();
        if (details == null || details.isEmpty()) return;
        for (WorkOrderStepDetail detail : details) {
            totalManHour += detail.getManHours()==null?0:detail.getManHours();
            totalPrice = totalPrice + (detail.getManHours()==null?0:detail.getManHours())*hourPrice
                    + (detail.getAccessoryQuantity()==null?0:detail.getAccessoryQuantity())*(detail.getAccessoryPrice()==null?0.0:detail.getAccessoryPrice())
                    + (detail.getOtherExpense()==null?0.0:detail.getOtherExpense());
        }
        wo.setTotalManHour(totalManHour);
        wo.setTotalPrice(totalPrice);
        woDao.save(wo);
    }

    //do wechat push
    @Autowired
    private  HttpServletRequest request;
    private void sendTemplateMsg(WorkOrder wo, WorkOrderStep currentWoStep, WorkOrderStep nextWoStep) {
        if (currentWoStep.getStepId() != 1) {
            //工单报修者 requestorId
            UserAccount user = userDao.findById(wo.getRequestorId());
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                    .toUser(user.getWeChatId()).templateId("4N0nfZ0fXstReD-FcBu-d6tUsTcwBEIND-0wmOh0cO8").build();
            if (currentWoStep.getStepId() == 6){
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("first", "工单已关单","#FF00FF"));
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("performance", "工单已关单","#FF00FF"));
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("time", "2017-3-13 17:23:34","#FF00FF"));
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("remark", currentWoStep.getStepName()+"已完成，工单已关单","#FF00FF"));
            } else {
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("first", "工单已关单","#FF00FF"));
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("performance", "工单已关单","#FF00FF"));
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("time", "2017-3-13 17:23:34","#FF00FF"));
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("remark", currentWoStep.getStepName()+"已完成，当前状态为"+nextWoStep.getStepName(),"#FF00FF"));
            }
            String serverName = request.getRequestURL().toString().replace("/web/finishwo", "");
            templateMessage.setUrl(wxService.oauth2buildAuthorizationUrl(serverName+"/web/menu/31", WxConsts.OAUTH2_SCOPE_USER_INFO, ""));
            try{
                wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            } catch (Exception e) {
                logger.error("消息发送失败");
            }
        }
    }

}
