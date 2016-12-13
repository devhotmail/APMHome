package com.ge.apm.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.poi.hssf.record.formula.functions.T;
import com.ge.apm.view.sysutil.UrlEncryptController;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class DemoUrlEncodeController extends JpaCRUDController<T>{

	private static final long serialVersionUID = 1L;
	private Integer woId;
	private String propertyStr;

	@Override
	protected GenericRepository<T> getDAO() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	 protected void init() {
		propertyStr = (String) UrlEncryptController.getMap("propertyStr");
		woId = Integer.parseInt((String) UrlEncryptController.getMap("woId"));
		logger.info("propertyStr value is "+propertyStr );
	}
	public Integer getWoId() {
		return woId;
	}

	public void setWoId(Integer woId) {
		this.woId = woId;
	}

	public String getPropertyStr() {
		return propertyStr;
	}

	public void setPropertyStr(String propertyStr) {
		this.propertyStr = propertyStr;
	}
	
}
