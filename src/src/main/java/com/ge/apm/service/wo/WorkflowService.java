package com.ge.apm.service.wo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.WechatMessageLogMapper;
import com.ge.apm.dao.mapper.WorkOrderMapper;
import com.ge.apm.domain.CasePriorityNum;
import com.ge.apm.domain.Constans;
import com.ge.apm.domain.ReopenPushModel;
import com.ge.apm.domain.TimeoutPushModel;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WechatMessageLog;
import com.ge.apm.domain.WorkFlow;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.utils.ConfigUtils;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.service.utils.WeiXinUtils;
import com.ge.apm.service.wechat.CoreService;

@Service
@Scope("prototype")
public class WorkflowService {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String TIMEOUT_TEMPLATE_ID = "rM1hPuHQDoXccoyUkUdapuxMinKxPqmcKhJdU7E6w1o";
	public static final String REOPEN_TEMPLATED_ID= "6zKD9kSJq1SEaym7HIfIFO8H136CTUcAfcIGTOtfBtk";
	@Autowired
	WorkOrderMapper workOrderMapper;
	
	@Autowired
	UserAccountMapper userAccountMapper;
	
	@Autowired
	WechatMessageLogMapper wechatMessageLogMapper;
	
	@Autowired
	CoreService coreService;
	
	@Autowired
	ConfigUtils configUtils;
	
	//private Map<String,WorkflowConfig> configs = new ConcurrentHashMap<String,WorkflowConfig>();

	@Transactional
	public void execute() {
		if(logger.isDebugEnabled()){
			logger.debug("begin check timeout and reopen……");
		}
		List<WorkOrder> orders = workOrderMapper.fetchUnFinishedWorkList();
		if(CollectionUtils.isEmpty(orders)){
			if(logger.isInfoEnabled()){
				logger.info("no open workOrder");
			}
			return;
		}
		for (WorkOrder workOrder : orders) {
			WorkflowConfig woc = workOrderMapper.fetchWorkflowConfig(workOrder.getSiteId(),workOrder.getHospitalId());
			if(woc == null){
				logger.error("fetch WorkflowConfig error,siteId is {},hospitalId is {}",workOrder.getSiteId(),workOrder.getHospitalId());
				continue;
			}
			isTimeout(workOrder,woc);
			//isReopen(workOrder,woc); // 暂不开放 2017.3.30
		}
		if(logger.isDebugEnabled()){
			logger.debug("end timeout and reopen……");
		}
	}
	
	@SuppressWarnings("unused")
	private void isReopen(WorkOrder workOrder, WorkflowConfig woc) {
		WorkOrder parent = workOrderMapper.fetchParentWorkOrder(workOrder.getParentWoId());
		if(parent == null){
			return;
		}
		DateTime parentStartTime = new DateTime(parent.getRequestTime());
		DateTime childStartTime = new DateTime(workOrder.getRequestTime());
		/*			StringBuilder builder = new StringBuilder();
		String key = builder.append(workOrder.getSiteId()).append(":").append(workOrder.getHospitalId()).toString();
		WorkflowConfig woc = configs.get(key);
		if(woc == null){
			woc = workOrderMapper.fetchWorkflowConfig(workOrder.getSiteId(),workOrder.getHospitalId());
			configs.put(key, woc);
		}*/
		List<Integer> users = null;
		if(Minutes.minutesBetween(childStartTime, parentStartTime).getMinutes() > woc.getOrderReopenTimeframe()){
			ReopenPushModel model = new ReopenPushModel();
			model.set_assetName(workOrder.getAssetName());
			model.set_parentRequestPerson(parent.getRequestorName());
			model.set_parentRequestTime(TimeUtils.getStrDate(parent.getRequestTime(), "yyyy-MM-dd hh:mm:ss"));
			model.set_requestPerson(workOrder.getRequestorName());
			model.set_requestTime(TimeUtils.getStrDate(workOrder.getRequestTime(), "yyyy-MM-dd hh:mm:ss"));
			model.set_linkUrl(coreService.getWoDetailUrl(workOrder.getId()));
			users = new ArrayList<Integer>(5);
			users.add(workOrder.getRequestorId());
			users.add(workOrder.getCurrentPersonId());
			users.add(parent.getRequestorId());
			users.add(parent.getCurrentPersonId());
			buildReopenTemplateMsg(model,users);
		}
	}
	private void buildReopenTemplateMsg(ReopenPushModel model, List<Integer> users) {
		List<UserAccount> accounts = userAccountMapper.getUserWechatId(users);
		for(UserAccount ua:accounts){
			coreService.sendWxTemplateMessage(ua.getWeChatId(),configUtils.fetchProperties("reopen_template_id"),WeiXinUtils.object2Map(model));
		}
		
	}
	
	
	private void isTimeout(WorkOrder workOrder, WorkflowConfig woc) {
		/*		StringBuilder builder = new StringBuilder();
		String key = builder.append(workOrder.getSiteId()).append(":").append(workOrder.getHospitalId()).toString();
		WorkflowConfig woc = configs.get(key);
		if(woc == null){
			woc = workOrderMapper.fetchWorkflowConfig(workOrder.getSiteId(),workOrder.getHospitalId());
			configs.put(key, woc);
		}*/
		List<WorkFlow> workFlows = workOrderMapper.fetchWorkFlowList(workOrder);
		if(CollectionUtils.isEmpty(workFlows)){
			logger.error("cannot find work steps , workOrder id is ",workOrder.getId());
			return;
		}
		List<Integer> users = new ArrayList<Integer>(workFlows.size() * 10);
		List<TimeoutPushModel> models = new ArrayList<TimeoutPushModel>(workFlows.size() * 10);
		//如果第一个超时，则给当前步骤之前的所有人发送通知
		WorkFlow lastOne = workFlows.get(0);
		String stepName = lastOne.getStepName();
		if (isTimeout(lastOne, woc)) {
			for (WorkFlow workFlow : workFlows) {
				if(!matchRule(workFlow,woc)){
					continue;
				}
				if (users.contains(workFlow.getCurrentPersonId())) {
					continue;
				}
				users.add(workFlow.getCurrentPersonId());
				TimeoutPushModel model = new TimeoutPushModel();
				model.set_assetName(workOrder.getAssetName());
				model.set_requestTime(TimeUtils.getStrDate(workOrder.getRequestTime(), "yyyy-MM-dd hh:mm:ss"));
				model.set_currentPerson(workOrder.getCurrentPersonName());
				model.set_status(stepName);// Constans.getName(workOrder.getCurrentStepId())
				model.setFirst(stepName + "超时");
				model.set_urgency(CasePriorityNum.getName(workOrder.getCasePriority()));
				model.set_linkUrl(coreService.getWoDetailUrl(workOrder.getId()));
				model.setCurrentPersonId(workFlow.getCurrentPersonId());
				models.add(model);
			}
		}
		if(CollectionUtils.isEmpty(models)){
			return;
		}
		if(!users.contains(workOrder.getRequestorId())){
			users.add(workOrder.getRequestorId());
		}
		if(!users.contains(woc.getDispatchUserId())){
			users.add(woc.getDispatchUserId());
		}
		buildTimeoutTemplateMsg(models,users);
	}
	
	private boolean matchRule(WorkFlow workFlow, WorkflowConfig woc) {
		WechatMessageLog wml = new WechatMessageLog();
		UserAccount user = userAccountMapper.getUserById(workFlow.getCurrentPersonId());
		String weChatId =user.getWeChatId();
		if(StringUtils.isEmpty(weChatId)){
			logger.error("user {} does not bind wx!",user.getLoginName());
			return false;
		}
		wml.setWechatid(weChatId);
		wml.setWoId(workFlow.getWorkOrderId());
		wml.setWoStepId(workFlow.getCurrentStepId());
		WechatMessageLog wmlFromDB = wechatMessageLogMapper.fetchByProperties(wml);
		if(wmlFromDB == null){
			wechatMessageLogMapper.insertMessageLogMapper(wml);
			return true;
		}
		if(wmlFromDB.getMessageCount() >= woc.getMaxMessageCount()){
			return false;
		}
		wechatMessageLogMapper.updateMessageLogMapper(wmlFromDB);
		return true;
	}

	private void buildTimeoutTemplateMsg(List<TimeoutPushModel> models, List<Integer> users) {
		logger.info("begin to push timeout msg");
		List<UserAccount> accounts = userAccountMapper.getUserWechatId(users);
		Map<Integer,UserAccount> uas = new HashMap<Integer,UserAccount>();
		for(UserAccount ua:accounts){
			uas.put(ua.getId(), ua);
		}
		for (TimeoutPushModel timeoutPushModel : models) {
			if(uas.get(timeoutPushModel.getCurrentPersonId()) != null){
				String openId = uas.get(timeoutPushModel.getCurrentPersonId()).getWeChatId();
				coreService.sendWxTemplateMessage(openId, configUtils.fetchProperties("timeout_template_id"),WeiXinUtils.object2Map(timeoutPushModel));
			}
		}
		logger.info("push timeout over,to users size is {}",users.size());
	}
	
	public boolean isTimeout(WorkFlow workFlow,WorkflowConfig woc){
		DateTime now = new DateTime(new Date());
		DateTime startTime = new DateTime(workFlow.getStartTime());
		Integer timeout = 0;
		if(workFlow.getCurrentStepId() ==Constans.DISPATCH.getIndex()){
			timeout =woc.getTimeoutDispatch();
		}else if(workFlow.getCurrentStepId() == Constans.ACCEPT.getIndex()){
			timeout =woc.getTimeoutAccept();
		}else if(workFlow.getCurrentStepId() == Constans.CLOSED.getIndex()){
			timeout =woc.getTimeoutClose();
		}
		if(timeout > 0){
			return Minutes.minutesBetween(startTime, now).getMinutes() > timeout;
		}
//		else if(workFlow.getCurrentStepId() == Constans.REPAIR.getIndex()){ //repair need not to notice
//			timeout =woc.getTimeoutRepair();
//		}
		return false;
	}
	
}
