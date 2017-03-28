package com.ge.apm.domain;

public class PushModel {
	private String _openId;
	private String _templateId;
	private String _assetName;//资产名称
	public String get_openId() {
		return _openId;
	}
	public void set_openId(String _openId) {
		this._openId = _openId;
	}
	public String get_templateId() {
		return _templateId;
	}
	public void set_templateId(String _templateId) {
		this._templateId = _templateId;
	}
	public String get_assetName() {
		return _assetName;
	}
	public void set_assetName(String _assetName) {
		this._assetName = _assetName;
	}
	
}
