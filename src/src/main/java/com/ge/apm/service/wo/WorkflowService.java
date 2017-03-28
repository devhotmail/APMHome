package com.ge.apm.service.wo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.WorkOrderMapper;
import com.ge.apm.domain.Constans;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkFlow;
import com.ge.apm.domain.WorkOrder;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.utils.WeiXinUtils;
import com.ge.apm.service.wechat.CoreService;

@Service
public class WorkflowService {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String TIMEOUT_TEMPLATE_ID = "1";
	public static final String TIMEOUT_TITLE = "超时提醒";
	public static final String TIMEOUT_BRIEF = "超时提醒";
	public static final String TIMEOUT_DETAILS = "超时提醒";
	@Autowired
	WorkOrderMapper workOrderMapper;
	
	@Autowired
	UserAccountMapper userAccountMapper;
	
	@Autowired
	CoreService coreService;
	
	private Map<String,WorkflowConfig> configs = new ConcurrentHashMap<String,WorkflowConfig>();

	public void execute() {
		List<WorkOrder> orders = workOrderMapper.fetchUnFinishedWorkList();
		if(CollectionUtils.isEmpty(orders)){
			return;
		}
		for (WorkOrder workOrder : orders) {
			isTimeout(workOrder);
			isReopen(workOrder);
		}
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
			users = new ArrayList<Integer>(5);
			users.add(workOrder.getRequestorId());
			users.add(workOrder.getCurrentPersonId());
			users.add(parent.getRequestorId());
			push2WX(users,"reopen");
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
		//如果第一个超时，则给当前步骤之前的所有人发送通知
		WorkFlow first = workFlows.get(0);
		if(need2Notice(first,woc)){
			for(WorkFlow workFlow : workFlows){
				users.add(workFlow.getCurrentPersonId());
			}
		}
		if(CollectionUtils.isEmpty(users)){
			return;
		}
		push2WX(users,"timeout");
	}
	
	public void push2WX(List<Integer> users,String modifier){
		WeiXinUtils.removeDuplicateId(users);
		List<UserAccount> accounts = userAccountMapper.getUserWechatId(users);
		//TODO push by batch
		for(UserAccount account : accounts ){
			if(modifier.equals("timeout")){
				buildTimeoutTemplateMsg(account);
			}else if(modifier.equals("reopen")){
				buildReopenTemplateMsg(account);
			}
		}
	}

	private void buildTimeoutTemplateMsg(UserAccount account) {
		logger.info("begin to push timeout msg");
		//coreService.sendWxTemplateMessage(account.getWeChatId(), TIMEOUT_TEMPLATE_ID, TIMEOUT_TITLE, TIMEOUT_BRIEF, TIMEOUT_DETAILS, "time", "url");
	}
	
	private void buildReopenTemplateMsg(UserAccount account) {
		logger.info("begin to push reopen msg");
		//coreService.sendWxTemplateMessage(account.getWeChatId(), TIMEOUT_TEMPLATE_ID, TIMEOUT_TITLE, TIMEOUT_BRIEF, TIMEOUT_DETAILS, "time", "url");
	}

	public boolean need2Notice(WorkFlow workFlow,WorkflowConfig woc){
		DateTime now = new DateTime(new Date());
		DateTime startTime = new DateTime(workFlow.getStartTime());
		Integer timeout = 0;
		if(workFlow.getCurrentStepId() == Constans.WO_STEP_ASSIGN){
			timeout =woc.getTimeoutDispatch();
		}else if(workFlow.getCurrentStepId() == Constans.WO_STEP_ACCEPT){
			timeout =woc.getTimeoutAccept();
		}else if(workFlow.getCurrentStepId() == Constans.WO_STEP_REPAIR){
			timeout =woc.getTimeoutRepair();
		}else if(workFlow.getCurrentStepId() == Constans.WO_STEP_CLOSED){
			timeout =woc.getTimeoutClose();
		}
		return startTime.plusMinutes(timeout).isAfter(now);
	}
	
}
