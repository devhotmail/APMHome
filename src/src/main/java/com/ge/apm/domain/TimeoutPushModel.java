package com.ge.apm.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class TimeoutPushModel extends PushModel{

	private String _requestTime;
	private String _urgency;
	private String _currentStepId;
	private String _currentPerson;
	private Integer currentPersonId;
	public Integer getCurrentPersonId() {
		return currentPersonId;
	}
	public void setCurrentPersonId(Integer currentPersonId) {
		this.currentPersonId = currentPersonId;
	}
	public String get_requestTime() {
		return _requestTime;
	}
	public void set_requestTime(String _requestTime) {
		this._requestTime = _requestTime;
	}
	public String get_urgency() {
		return _urgency;
	}
	public void set_urgency(String _urgency) {
		this._urgency = _urgency;
	}
	public String get_currentStepId() {
		return _currentStepId;
	}
	public void set_currentStepId(String _currentStepId) {
		this._currentStepId = _currentStepId;
	}
	public String get_currentPerson() {
		return _currentPerson;
	}
	public void set_currentPerson(String _currentPerson) {
		this._currentPerson = _currentPerson;
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeoutPushModel [_requestTime=");
		builder.append(_requestTime);
		builder.append(", _urgency=");
		builder.append(_urgency);
		builder.append(", _currentStepId=");
		builder.append(_currentStepId);
		builder.append(", _currentPerson=");
		builder.append(_currentPerson);
		builder.append(", currentPersonId=");
		builder.append(currentPersonId);
		builder.append("]");
		return builder.toString();
	}
	public Map<String,Object> toMap(){
		Map<String,Object> map = new HashMap<String,Object>();
		 try {
			BeanUtils.populate(this, map);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return map;
	}

}
