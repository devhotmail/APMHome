package com.ge.apm.service.wo;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ConfigUtils;
import com.ge.apm.service.wechat.CoreService;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

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
    private WorkOrderStepRepository workOrderStepRepository;
    @Autowired
    private WorkOrderStepDetailRepository woDetailDao;
    @Autowired
    private I18nMessageRepository i18nMessageRepository;
    @Autowired
    private WorkflowConfigRepository woConDao;
    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Autowired
    private CoreService cService;
    @Autowired
    private MessageSubscriberRepository subscribDao;
    @Autowired
    private ConfigUtils configUtils;

    public List<WorkOrder> findWorkOrderByStatus(int status)throws Exception{
        List<WorkOrder> byStatus = workOrderRepository.findByStatus(status);
        return byStatus;
    }
    public  List<WorkOrder>  findWorkOrderByCon(HttpServletRequest request){
        UserAccount ua = UserContext.getCurrentLoginUser(request);
        return workOrderRepository.findByRequestorIdAndStatusOrderById(ua.getId(), Integer.parseInt(request.getParameter("status")));
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
    public String workWorderCreate(HttpServletRequest request, WorkOrderPoJo wop)throws Exception{
        /*  判断是否二次开单  --> 判断派工模式
        * 1（拿siteid和assetid还有hospitalid来作为该设备是否是唯一的）
        * */
        //select * from work_order where  (close_time::timestamp)::date > (select now() - interval '7 day')::date  and  asset_id =68 and hospital_id=2 and site_id=2
        //gl:requestor 就是申请保修的,curent_person_d: 自动派单时为-1，手动派单时为设备科科长.
        //gl: select from user_account ua , sys_role  sr where sr.role_id =2 and ua.user_id = ?
        UserAccount currentUsr= UserContext.getCurrentLoginUser(request);
        WorkflowConfig woc = woConDao.getBySiteIdAndHospitalId(currentUsr.getSiteId(), currentUsr.getHospitalId());
        if (woc == null){
            logger.error("没有找到对应机构的流程配置信息......");
            return "error";
        }
        if (woc.getDispatchUserId() == null) {
            logger.error("流程派单人未配置......");
            return "error";
        }
        List<WorkOrder> reopenWorkOrder = null;
        if (woc.getOrderReopenTimeframe() == null) {
            logger.error("流程二次开单信息未配置......");
        } else {
            reopenWorkOrder = workOrderRepository.isReopenWorkOrder(wop.getAssetId(),
                currentUsr.getHospitalId(), currentUsr.getSiteId(), woc.getOrderReopenTimeframe()+ " day");
        }
        
        WorkOrder neWorkOrder = initWorkOrder(wop, currentUsr, reopenWorkOrder);
        neWorkOrder.setCurrentPersonId(woc.getDispatchUserId());
        neWorkOrder.setCurrentPersonName(woc.getDispatchUserName());
        //voice
        if (wop.getVoiceId() != null){
            cService.uploadVoice(neWorkOrder, wop.getVoiceId());
        } 
        workOrderRepository.save(neWorkOrder);
        //images
        if (wop.getImgIds() != null) {
            String[] imgIds = wop.getImgIds().split(",");
            if (imgIds.length > 0) {
                for (String str : imgIds) {
                    cService.uploadImage(neWorkOrder, str);
                }
            }
        }
        //step
        WorkOrderStep wds = initWorkOrderStep(request, neWorkOrder);
        wds.setOwnerId(woc.getDispatchUserId());
        wds.setOwnerName(woc.getDispatchUserName());
        workOrderStepRepository.save(wds);
        //msg
        sendWoMsgs(neWorkOrder, null, "requestor");
        return "success";
    }
    @Transactional
    public  void assignWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo) throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
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
        WorkOrderStep wds =  initWorkOrderStep(request, wo);//new WorkOrderStep();
        wds.setOwnerId(user.getId());
        wds.setOwnerName(user.getName());
        workOrderStepRepository.save(wds);
        sendWoMsgs(wo, null, null);
    }


    @Transactional
    public void takeWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        Date estimatedCloseTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(wopo.getEstimatedCloseTime());
        wo.setEstimatedCloseTime(estimatedCloseTime);

        // update end time for last work step
        updateEndTime(wo, wopo.getDesc());
        //1 update work order
        workOrderUpdate(request, wo);
        //2 create new work-order-step
        WorkOrderStep wds = initWorkOrderStep(request, wo);
        workOrderStepRepository.save(wds);
        sendWoMsgs(wo, null, null);
    }

    @Transactional
    public void signWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws  Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        updateEndTime(wo, "签到");
        WorkOrderStep wds = initWorkOrderStep(request, wo);
        workOrderStepRepository.save(wds);
//        sendWoMsgs(wo, );
    }
    @Transactional
    public void repairWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        updateEndTime(wo, wopo.getDesc());
        workOrderUpdate(request, wo);
        WorkOrderStep wds = initWorkOrderStep(request, wo);
        workOrderStepRepository.save(wds);
        sendWoMsgs(wo, null, null);
    }
    @Transactional
    public void closeWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        WorkOrderStep wos = updateEndTime(wo, wopo.getDesc());
        wo.setStatus(2);
        wo.setCloseTime(new Date());
        wo.setCaseType(wopo.getCaseType()==null?0:Integer.parseInt(wopo.getCaseType()));
        wo.setCaseSubType(wopo.getCaseSubType()==null?0:Integer.parseInt(wopo.getCaseSubType()));
        wo.setPatActions(wopo.getPatActions());
        wo.setPatProblems(wopo.getPatProblems());
        wo.setPatTests(wopo.getPatTests());
        workOrderUpdate(request, wo);
        
        JSONArray array = JSONArray.fromObject(wopo.getStepDetail());
        List<WorkOrderStepDetail> list = JSONArray.toList(array, new WorkOrderStepDetail(), new JsonConfig());
        if (list != null && !list.isEmpty()) {
            for (WorkOrderStepDetail sd : list) {
                sd.setWorkOrderStepId(wos.getId());
            }
            woDetailDao.save(list);
        }
        
        sendWoMsgs(wo, null, null);
    }
    @Transactional
    public void returnWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        updateEndTime(wo, wopo.getDesc());
        UserAccount currentUsr = UserContext.getCurrentLoginUser(request);
        wo.setCurrentStepId(2);
        //step
        WorkOrderStep wds = initWorkOrderStep(request, wo);
        WorkflowConfig woc = woConDao.getBySiteIdAndHospitalId(currentUsr.getSiteId(), currentUsr.getHospitalId());
        if (woc != null) {
            wo.setCurrentPersonId(woc.getDispatchUserId());
            wo.setCurrentPersonName(woc.getDispatchUserName());
            wds.setOwnerId(woc.getDispatchUserId());
            wds.setOwnerName(woc.getDispatchUserName());
        }
        workOrderStepRepository.save(wds);
        wo.setCurrentStepName(wds.getStepName());
        workOrderRepository.save(wo);
        sendWoMsgs(wo, "工单回退", null);
    }
    
    @Transactional
    public void fatchWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        Date estimatedCloseTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(wopo.getEstimatedCloseTime());
        wo.setEstimatedCloseTime(estimatedCloseTime);

        // update end time for last work step
        updateEndTime(wo, wopo.getDesc());
        //1 update work order
        wo.setCurrentStepId(4);
        //2 create new work-order-step
        WorkOrderStep wds = initWorkOrderStep(request, wo);
        wo.setCurrentPersonId(wds.getOwnerId());
        wo.setCurrentPersonName(wds.getOwnerName());
        wo.setCurrentStepName(wds.getStepName());
        workOrderRepository.save(wo);
        workOrderStepRepository.save(wds);
        sendWoMsgs(wo, null, null);
    }
    
    
    @Transactional
    public void cancelWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        updateEndTime(wo, wopo.getDesc());
        wo.setStatus(3);
        wo.setCloseTime(new Date());
        workOrderRepository.save(wo);
        sendWoMsgs(wo, "工单取消", "requestor");
    }
    
    @Transactional
    public void feedbackWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        wo.setFeedbackComment(wopo.getDesc());
        wo.setFeedbackRating(Integer.parseInt(wopo.getFeedbackRating()));
        workOrderRepository.save(wo);
    }
    
    @Transactional
    private void workOrderUpdate(HttpServletRequest request, WorkOrder wo)throws  Exception{
        UserAccount currentUserAccount = UserContext.getCurrentLoginUser(request);
        wo.setCurrentPersonId(currentUserAccount.getId());
        wo.setCurrentPersonName(currentUserAccount.getName());
        int currentStepId = wo.getCurrentStepId();
        wo.setCurrentStepId(currentStepId+1);
        String stepName = i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps", String.valueOf(currentStepId+1)).getValueZh();
        wo.setCurrentStepName(stepName);
        workOrderRepository.save(wo);
    }

    private WorkOrderStep initWorkOrderStep(HttpServletRequest request, WorkOrder wo ) throws Exception{
        UserAccount currentUserAccount = UserContext.getCurrentLoginUser(request);
        WorkOrderStep wds = new WorkOrderStep();
        wds.setOwnerId(currentUserAccount.getId());
        wds.setOwnerName(currentUserAccount.getName());
        wds.setStartTime(new Date());
        wds.setWorkOrderId(wo.getId());
        wds.setSiteId(wo.getSiteId());
        int currentStepId = wo.getCurrentStepId();
        wds.setStepId(currentStepId);
        String stepName = i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps", String.valueOf(currentStepId)).getValueZh();
        wds.setStepName(stepName);
        return wds;
    }
    @Transactional
    private WorkOrderStep updateEndTime(WorkOrder wo, String desc)throws Exception{
        //2.1  update endtime in wos for last step workorder
        List<WorkOrderStep> wosList =workOrderStepRepository.getByWorkOrderIdAndStepId(wo.getId(),wo.getCurrentStepId());
        if(wosList.size()>0){
            WorkOrderStep wos = wosList.get(0);
            wos.setEndTime(new Date());
            wos.setDescription(desc);
            return workOrderStepRepository.save(wos);
        }else{
            throw new Exception("work order step missing");
        }

    }
    
    private WorkOrder initWorkOrder(WorkOrderPoJo wop,UserAccount usr,List<WorkOrder> reopenWorkOrder) throws Exception{

        WorkOrder neWorkOrder = new WorkOrder();
        neWorkOrder.setSiteId(usr.getSiteId());
        neWorkOrder.setHospitalId(usr.getHospitalId());
        AssetInfo ai = assetInfoRepository.getById(wop.getAssetId());
        if(ai == null){
            throw new Exception("该资产在数据中找不到");
        }
        neWorkOrder.setAssetId(wop.getAssetId());
        neWorkOrder.setAssetName(ai.getName());
        neWorkOrder.setRequestorId(usr.getId());
        neWorkOrder.setRequestorName(usr.getName());
        neWorkOrder.setRequestTime(new Date());
        neWorkOrder.setRequestReason("".equals(wop.getReason())?"参见语音":wop.getReason());
        neWorkOrder.setCasePriority(Integer.parseInt(wop.getPriority()));
        
        if(reopenWorkOrder != null && reopenWorkOrder.size()>0){
            neWorkOrder.setParentWoId(reopenWorkOrder.get(0).getId());
        }
        neWorkOrder.setCurrentStepId(2);// gl: 表示步骤是开单
        neWorkOrder.setCurrentStepName(i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps",Integer.toString(2)).getValueZh()) ;
        neWorkOrder.setTotalManHour(0);//gl:----?
        neWorkOrder.setTotalPrice(0.0);//gl:----?

        neWorkOrder.setStatus(1);  //gl: -- 1-在修 / 2-完成 / 3-取消
        neWorkOrder.setFeedbackRating(0);
        
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
    
    public void sendWoMsgs(WorkOrder wo, String msgTitle, String msgType) {
        //String wxTemplateId = "4N0nfZ0fXstReD-FcBu-d6tUsTcwBEIND-0wmOh0cO8";
        String wxTemplateId = configUtils.fetchProperties("workorder_change_template_id");
        msgTitle = msgTitle==null?i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps",wo.getCurrentStepId()-1+"").getValueZh():msgTitle;
        String linkUrl = cService.getWoDetailUrl(wo.getId());
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("first", msgTitle);
        
        params.put("_assetName", wo.getAssetName());
        params.put("_requestTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(wo.getRequestTime()));
        params.put("_requestPerson", wo.getRequestorName());
        params.put("_urgency", i18nMessageRepository.getByMsgTypeAndMsgKey("casePriority",wo.getCasePriority()+"").getValueZh());
        params.put("_currentPerson", wo.getCurrentPersonName());
        
        String msgBrief = "资产名称："+wo.getAssetName() +"\n"
                + "报修时间："+ (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(wo.getRequestTime())) +"\n"
                + "报修人："+ wo.getRequestorName() +"\n"
                + "紧急程度："+ i18nMessageRepository.getByMsgTypeAndMsgKey("casePriority",wo.getCasePriority()+"").getValueZh() +"\n"
                + "当前责任人："+ wo.getCurrentPersonName();
        params.put("remark", msgBrief);
        params.put("_linkUrl", linkUrl);
        
        // subscriber
        List<MessageSubscriber> sber = null;
        switch(wo.getCurrentStepId()-1) {
            case 1: sber = subscribDao.getByAssetIdAndReceiveMsgModeIn(wo.getAssetId(), Arrays.asList(1,2)); break;
            case 5: sber = subscribDao.getByAssetIdAndReceiveMsgModeIn(wo.getAssetId(), Arrays.asList(1,2)); break;
            default: sber = subscribDao.getByAssetIdAndReceiveMsgMode(wo.getAssetId(), 2);break;
        }
        if (sber != null) {
            for (MessageSubscriber sb :sber) {
                int userId = sb.getSubscribeUserId();
                UserAccount ua = userDao.getById(userId);
                cService.sendWxTemplateMessage(ua.getWeChatId(), wxTemplateId, params);
            }
        }
        //currentPerson
        if (wo.getCurrentStepId() == 2 || wo.getCurrentStepId() == 3) {
            UserAccount ua = userDao.getById(wo.getCurrentPersonId());
            cService.sendWxTemplateMessage(ua.getWeChatId(), wxTemplateId, params);
        }
        //requestor
        if (!"requestor".equals(msgType)) {
            UserAccount ua = userDao.getById(wo.getRequestorId());
            cService.sendWxTemplateMessage(ua.getWeChatId(), wxTemplateId, params);
        }
        //assigner
        if (wo.getCurrentStepId() == 4) {
            List<WorkOrderStep> list = stepDao.findByWorkOrderIdAndStepId(wo.getId(), 2);
            if (list != null && !list.isEmpty()){
                UserAccount ua = userDao.getById(list.get(0).getOwnerId());
                cService.sendWxTemplateMessage(ua.getWeChatId(), wxTemplateId, params);
            }
        }
    }
    
    @Transactional
    public void estimatedTime(WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        Date estimatedCloseTime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(wopo.getEstimatedCloseTime());
        wo.setEstimatedCloseTime(estimatedCloseTime);
        workOrderRepository.save(wo);
    }

}
