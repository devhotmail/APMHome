package com.ge.apm.domain;

public class Constans {
	//工单状态
	public static final int WO_STEP_CREATE = 1;//保修
	public static final int WO_STEP_APPROVE = 2;//审核
	public static final int WO_STEP_ASSIGN = 3;//派工
	public static final int WO_STEP_ACCEPT = 4;//领工
	public static final int WO_STEP_REPAIR = 5;//维修
	public static final int WO_STEP_CLOSED = 6;//关单
	
	//微信推送的templateId
	public static final String TIME_OUT_PUSH = "";
	public static final String REOPEN_PUSH = "";
}
