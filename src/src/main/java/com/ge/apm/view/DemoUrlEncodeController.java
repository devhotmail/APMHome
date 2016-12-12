package com.ge.apm.view;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.record.formula.functions.T;
import com.ge.apm.view.sysutil.UrlEncryptController;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class DemoUrlEncodeController extends JpaCRUDController<T>{

	private static final long serialVersionUID = 1L;
	private Map<String,Object> map;
	private String encodeStr;
	private String propertyStr;

	@Override
	protected GenericRepository<T> getDAO() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	 protected void init() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        //encodeStr = request.getQueryString();
        encodeStr = request.getParameter("str");
		map = (Map<String, Object>) UrlEncryptController.urlParamDecode(encodeStr);
		if(map != null && !map.isEmpty()){
			propertyStr = (String) map.get("propertyStr");
		}
	}

	public String getEncodeStr() {
		return encodeStr;
	}

	public void setEncodeStr(String encodeStr) {
		this.encodeStr = encodeStr;
	}

	public String getPropertyStr() {
//		if(map != null && !map.isEmpty()){
//			return (String) map.get("propertyStr");
//		}
		return propertyStr;
	}

	public void setPropertyStr(String propertyStr) {
		this.propertyStr = propertyStr;
	}
	
}
