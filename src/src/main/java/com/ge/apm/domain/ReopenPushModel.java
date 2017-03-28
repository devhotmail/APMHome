package com.ge.apm.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class ReopenPushModel extends PushModel{
	private String _parentRequestTime;
	private String _parentRequestPerson;
	private String _requestTime;
	private String _requestPerson;
	public String get_parentRequestTime() {
		return _parentRequestTime;
	}
	public void set_parentRequestTime(String _parentRequestTime) {
		this._parentRequestTime = _parentRequestTime;
	}
	public String get_parentRequestPerson() {
		return _parentRequestPerson;
	}
	public void set_parentRequestPerson(String _parentRequestPerson) {
		this._parentRequestPerson = _parentRequestPerson;
	}
	public String get_requestTime() {
		return _requestTime;
	}
	public void set_requestTime(String _requestTime) {
		this._requestTime = _requestTime;
	}
	public String get_requestPerson() {
		return _requestPerson;
	}
	public void set_requestPerson(String _requestPerson) {
		this._requestPerson = _requestPerson;
	}
	public Map<String, Object> toMap() {
		Map<String,Object> map = new HashMap<String,Object>();
		 try {
			BeanUtils.populate(this, map);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
}
