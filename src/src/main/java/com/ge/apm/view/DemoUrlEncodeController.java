package com.ge.apm.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ge.apm.view.sysutil.UrlEncryptController;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class DemoUrlEncodeController<T> extends JpaCRUDController<T>{

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
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
		propertyStr = (String) UrlEncryptController.getValueFromMap(encodeStr,"propertyStr");
		String woIdStr = (String) UrlEncryptController.getValueFromMap(encodeStr,"woId");
		if(woIdStr !=null){
			woId = Integer.parseInt(woIdStr);
		}
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
