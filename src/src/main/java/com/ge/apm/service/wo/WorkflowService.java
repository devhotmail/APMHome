package com.ge.apm.service.wo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.dao.mapper.WechatMessageLogMapper;
import com.ge.apm.dao.mapper.WorkOrderMapper;
import com.ge.apm.domain.AssetInfo;
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
public class WorkflowService {
	Logger logger = LoggerFactory.getLogger(getClass());
	public static final int SPECIAL_DISPATCHER = 1;// 专人派工 work_order_dispather角色
	public static final int GRAB_DISPATCHER = 2;// 抢单 asset_staff 角色
	public static final int AUTO_DISPATCHER = 3;// 资产owner 1 2

	@Autowired
	WorkOrderMapper workOrderMapper;

	@Autowired
	UserAccountMapper userAccountMapper;

	@Autowired
	AssetInfoMapper assetInfoMapper;

	@Autowired
	WechatMessageLogMapper wechatMessageLogMapper;

	@Autowired
	CoreService coreService;

	@Autowired
	ConfigUtils configUtils;

	@Transactional
	public void execute() {
		if (logger.isDebugEnabled()) {
			logger.debug("begin check timeout and reopen……");
		}
		List<WorkOrder> orders = workOrderMapper.fetchUnFinishedWorkList();
		if (CollectionUtils.isEmpty(orders)) {
			if (logger.isInfoEnabled()) {
				logger.info("no open workOrder");
			}
			return;
		}
		for (WorkOrder workOrder : orders) {
			WorkflowConfig woc = workOrderMapper.fetchWorkflowConfig(workOrder.getSiteId(), workOrder.getHospitalId());
			if (woc == null) {
				logger.error("fetch WorkflowConfig error,siteId is {},hospitalId is {}", workOrder.getSiteId(),workOrder.getHospitalId());
				continue;
			}
			isTimeout(workOrder, woc);
			// isReopen(workOrder,woc); // 暂不开放 2017.3.30
		}
		if (logger.isDebugEnabled()) {
			logger.debug("end timeout and reopen……");
		}
	}

	@SuppressWarnings("unused")
	private void isReopen(WorkOrder workOrder, WorkflowConfig woc) {
		WorkOrder parent = workOrderMapper.fetchParentWorkOrder(workOrder.getParentWoId());
		if (parent == null) {
			return;
		}
		DateTime parentStartTime = new DateTime(parent.getRequestTime());
		DateTime childStartTime = new DateTime(workOrder.getRequestTime());
		List<Integer> users = null;
		if (Minutes.minutesBetween(childStartTime, parentStartTime).getMinutes() > woc.getOrderReopenTimeframe()) {
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
			buildReopenTemplateMsg(model, users);
		}
	}

	private void buildReopenTemplateMsg(ReopenPushModel model, List<Integer> users) {
		List<UserAccount> accounts = userAccountMapper.getUserWechatId(users);
		for (UserAccount ua : accounts) {
			coreService.sendWxTemplateMessage(ua.getWeChatId(), configUtils.fetchProperties("reopen_template_id"),
					WeiXinUtils.object2Map(model));
		}
	}

	private void isTimeout(WorkOrder workOrder, WorkflowConfig woc) {
		List<WorkFlow> workFlows = workOrderMapper.fetchWorkFlowList(workOrder);
		if (CollectionUtils.isEmpty(workFlows)) {
			logger.error("cannot find work steps , workOrder id is ", workOrder.getId());
			return;
		}
		List<Integer> users = new ArrayList<Integer>(workFlows.size() * 10);
		TimeoutPushModel model = null;
		WorkFlow lastOne = workFlows.get(0);
		String stepName = lastOne.getStepName();
		Integer woId = workOrder.getId();
		Integer currentStepId = lastOne.getCurrentStepId();
		Integer times = woc.getMaxMessageCount();
		if (isTimeout(lastOne, woc)) {
			model = new TimeoutPushModel();
			model.setFirst(stepName + "超时");
			model.setKeyword1(workOrder.getAssetName());
			model.setKeyword2(TimeUtils.getStrDate(workOrder.getRequestTime(), "yyyy-MM-dd HH:mm:ss"));
			model.setKeyword3(CasePriorityNum.getName(workOrder.getCasePriority()));
			model.setKeyword4(stepName);// Constans.getName(workOrder.getCurrentStepId())
			model.setKeyword5(workOrder.getCurrentPersonName());
			model.setLinkUrl(coreService.getWoDetailUrl(workOrder.getId()));
			for (WorkFlow workFlow : workFlows) {
				users.add(workFlow.getCurrentPersonId());
			}
			users.add(workOrder.getRequestorId());//报修人

			// sysRole 
			if (woc.getDispatchMode() == SPECIAL_DISPATCHER) {
				List<Integer> dispatchers = userAccountMapper.fetchDispaterUser(workOrder.getRequestorId(), 8);
				users.addAll(dispatchers);
			} else if (woc.getDispatchMode() == GRAB_DISPATCHER) {
				List<Integer> dispatchers = userAccountMapper.fetchDispaterUser(workOrder.getRequestorId(), 3);
				users.addAll(dispatchers);
			} else if (woc.getDispatchMode() == AUTO_DISPATCHER) {
				AssetInfo asset = assetInfoMapper.fetchAssetInfoById(workOrder.getAssetId());
				if (asset != null) {
					users.add(asset.getAssetOwnerId());
					users.add(asset.getAssetOwnerId2());
				}
			}
			List<Integer> subscribers = userAccountMapper.getAssetSubscriber(workOrder.getAssetId());
			users.addAll(subscribers);
			logger.info("before filter ,users is {}",users);
			List<Integer> filterUser =users.stream().filter(id -> id != null).filter(id -> id >0).distinct().collect(Collectors.toList());
			logger.info("after filter ,users is {}",users);
			if(CollectionUtils.isEmpty(filterUser)){
				logger.error("1workOrder timeout but no users find,orderId is {}",workOrder.getId());
				return;
			}
			Iterator<Integer> it = filterUser.iterator();  
			while(it.hasNext()) {
				Integer needPushUser = it.next();
				 if(!isMatchRule(needPushUser, woId, currentStepId, times)){
					 logger.info("userId {} is removed because of rule!",needPushUser);
					 it.remove();
				 }
			}
			if(CollectionUtils.isEmpty(filterUser)){
				logger.warn("2workOrder timeout but no users find,orderId is {}",workOrder.getId());
				return;
			}
			buildTimeoutTemplateMsg(model, filterUser);
		} else {
			logger.info(" no workorder is timeout ....");
		}

	}

	private void buildTimeoutTemplateMsg(TimeoutPushModel model, List<Integer> users) {
		logger.info("begin to push timeout msg");
		List<UserAccount> accounts = userAccountMapper.getUserWechatId(users);
		for (UserAccount ua : accounts) {
			coreService.sendWxTemplateMessage(ua.getWeChatId(), configUtils.fetchProperties("timeout_template_id"),
					WeiXinUtils.object2Map(model));
		}
		logger.info("push timeout over,to users is {}", users);
	}
	
	private boolean isMatchRule(Integer userId,Integer woId,Integer currentStepId,Integer times) {
		//-1 stands for no limit
		if(times == -1){
			return true;
		}
		WechatMessageLog wml = new WechatMessageLog();
		UserAccount user = userAccountMapper.getUserById(userId);
		String weChatId = user.getWeChatId();
		if (StringUtils.isEmpty(weChatId)) {
			logger.error("user {} does not bind wx!", user.getLoginName());
			return false;
		}
		wml.setWechatid(weChatId);
		wml.setWoId(woId);
		wml.setWoStepId(currentStepId);
		WechatMessageLog wmlFromDB = wechatMessageLogMapper.fetchByProperties(wml);
		if (wmlFromDB == null) {
			wechatMessageLogMapper.insertMessageLogMapper(wml);
			return true;
		}
		if (wmlFromDB.getMessageCount() >= times) {
			return false;
		}
		wechatMessageLogMapper.updateMessageLogMapper(wmlFromDB);
		return true;
	}

	public boolean isTimeout(WorkFlow workFlow, WorkflowConfig woc) {
		DateTime now = new DateTime(new Date());
		DateTime startTime = new DateTime(workFlow.getStartTime());
		Integer timeout = 0;
		if (workFlow.getCurrentStepId() == Constans.DISPATCH.getIndex()) {
			timeout = woc.getTimeoutDispatch();
		} else if (workFlow.getCurrentStepId() == Constans.ACCEPT.getIndex()) {
			timeout = woc.getTimeoutAccept();
		} else if (workFlow.getCurrentStepId() == Constans.CLOSED.getIndex()) {
			timeout = woc.getTimeoutClose();

		}
		if (timeout > 0) {
			return Minutes.minutesBetween(startTime, now).getMinutes() > timeout;
		}
		// else if(workFlow.getCurrentStepId() == Constans.REPAIR.getIndex()){
		// //repair need not to notice
		// timeout =woc.getTimeoutRepair();
		// }
		return false;
	}

}
