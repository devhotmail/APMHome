package com.ge.apm.service.wo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.WorkOrderMapper;
import com.ge.apm.domain.CasePriorityNum;
import com.ge.apm.domain.Constans;
import com.ge.apm.domain.ReopenPushModel;
import com.ge.apm.domain.TimeoutPushModel;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkFlow;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.utils.TimeUtils;
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
	CoreService coreService;
	
	private Map<String,WorkflowConfig> configs = new ConcurrentHashMap<String,WorkflowConfig>();

	public void execute() {
		logger.info("begin check timeout and reopen……");
		List<WorkOrder> orders = workOrderMapper.fetchUnFinishedWorkList();
		if(CollectionUtils.isEmpty(orders)){
			if(logger.isInfoEnabled()){
				logger.info("no open workOrder");
			}
			return;
		}
		for (WorkOrder workOrder : orders) {
			isTimeout(workOrder);
			isReopen(workOrder);
		}
		logger.info("end timeout and reopen……");
	}
	
	private void isReopen(WorkOrder workOrder) {
		WorkOrder parent = workOrderMapper.fetchParentWorkOrder(workOrder.getParentWoId());
		if(parent == null){
			return;
		}
		StringBuilder builder = new StringBuilder();
		String key = builder.append(workOrder.getSiteId()).append(":").append(workOrder.getHospitalId()).toString();
		DateTime parentStartTime = new DateTime(parent.getRequestTime());
		DateTime childStartTime = new DateTime(workOrder.getRequestTime());
		WorkflowConfig woc = configs.get(key);
		if(woc == null){
			woc = workOrderMapper.fetchWorkflowConfig(workOrder.getSiteId(),workOrder.getHospitalId());
			configs.put(key, woc);
		}
		List<Integer> users = null;
		if(Minutes.minutesBetween(childStartTime, parentStartTime).getMinutes() > woc.getOrderReopenTimeframe()){
			ReopenPushModel model = new ReopenPushModel();
			model.set_assetName(workOrder.getAssetName());
			model.set_parentRequestPerson(parent.getRequestorName());
			TimeUtils.getStrDate(workOrder.getRequestTime(), "yyyy-MM-dd hh:mm");
			model.set_parentRequestTime(TimeUtils.getStrDate(parent.getRequestTime(), "yyyy-MM-dd hh:mm"));
			model.set_requestPerson(workOrder.getRequestorName());
			model.set_requestTime(TimeUtils.getStrDate(workOrder.getRequestTime(), "yyyy-MM-dd hh:mm"));
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
			coreService.sendWxTemplateMessage(ua.getWeChatId(), REOPEN_TEMPLATED_ID,model.toMap());
		}
		
	}

	private void isTimeout(WorkOrder workOrder) {
		StringBuilder builder = new StringBuilder();
		String key = builder.append(workOrder.getSiteId()).append(":").append(workOrder.getHospitalId()).toString();
		WorkflowConfig woc = configs.get(key);
		if(woc == null){
			woc = workOrderMapper.fetchWorkflowConfig(workOrder.getSiteId(),workOrder.getHospitalId());
			configs.put(key, woc);
		}
		List<WorkFlow> workFlows = workOrderMapper.fetchWorkFlowList(workOrder);
		if(CollectionUtils.isEmpty(workFlows)){
			logger.error("cannot find work steps , workOrder id is ",workOrder.getId());
			return;
		}
		List<Integer> users = new ArrayList<Integer>(workFlows.size() * 10);
		List<TimeoutPushModel> models = new ArrayList<TimeoutPushModel>(workFlows.size() * 10);
		//如果第一个超时，则给当前步骤之前的所有人发送通知
		WorkFlow lastOne = workFlows.get(0);
		if(need2Notice(lastOne,woc)){
			for(WorkFlow workFlow : workFlows){
				if(users.contains(workFlow.getCurrentPersonId())){
					continue;
				}
				users.add(workFlow.getCurrentPersonId());
				TimeoutPushModel model = new TimeoutPushModel();
				model.set_assetName(workOrder.getAssetName());
				model.set_requestTime(TimeUtils.getStrDate(workOrder.getRequestTime(), "yyyy-MM-dd hh:mm"));
				model.set_currentPerson(workOrder.getCurrentPersonName());
				model.set_currentStepId(Constans.getName(workFlow.getCurrentStepId()));
				model.set_urgency(CasePriorityNum.getName(workOrder.getCasePriority()));
				model.setCurrentPersonId(workFlow.getCurrentPersonId());
				models.add(model);
			}
		}
		if(CollectionUtils.isEmpty(models)){
			return;
		}
		//push2WX(models,"timeout");
		buildTimeoutTemplateMsg(models,users);
	}
	

	private void buildTimeoutTemplateMsg(List<TimeoutPushModel> models, List<Integer> users) {
		logger.info("begin to push timeout msg");
		List<UserAccount> accounts = userAccountMapper.getUserWechatId(users);
		Map<Integer,UserAccount> uas = new HashMap<Integer,UserAccount>();
		for(UserAccount ua:accounts){
			uas.put(ua.getId(), ua);
		}
		for (TimeoutPushModel timeoutPushModel : models) {
			String openId = uas.get(timeoutPushModel.getCurrentPersonId()).getWeChatId();
			coreService.sendWxTemplateMessage(openId, TIMEOUT_TEMPLATE_ID,timeoutPushModel.toMap());
		}
		//coreService.sendWxTemplateMessage(account.getWeChatId(), TIMEOUT_TEMPLATE_ID, TIMEOUT_TITLE, TIMEOUT_BRIEF, TIMEOUT_DETAILS, "time", "url");
	}
	

	public boolean need2Notice(WorkFlow workFlow,WorkflowConfig woc){
		DateTime now = new DateTime(new Date());
		DateTime startTime = new DateTime(workFlow.getStartTime());
		Integer timeout = 0;
		if(workFlow.getCurrentStepId() ==Constans.CREATE.getIndex()){
			timeout =woc.getTimeoutDispatch();
		}else if(workFlow.getCurrentStepId() == Constans.ACCEPT.getIndex()){
			timeout =woc.getTimeoutAccept();
		}else if(workFlow.getCurrentStepId() == Constans.REPAIR.getIndex()){
			timeout =woc.getTimeoutRepair();
		}else if(workFlow.getCurrentStepId() == Constans.CLOSED.getIndex()){
			timeout =woc.getTimeoutClose();
		}
		return startTime.plusMinutes(timeout).isAfter(now);
	}
	
}
