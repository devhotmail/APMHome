package com.ge.apm.view.wechat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.record.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class WxScanController  extends JpaCRUDController<T>{

	private static final long serialVersionUID = -1L;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private WxJsapiSignature wxJsapiSignature ;
    private WxMpService wxMpService;
    
    @Override
    protected void init() {
    	wxMpService = WebUtil.getBean(WxMpService.class);
    	try {
    		HttpServletRequest request =((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
			wxJsapiSignature = wxMpService.createJsapiSignature(request.getRequestURL().toString()+"?"+request.getQueryString());
	    	logger.info("appid is {},noncestr is {},timestamp is {},url is {},signature is {}",
	    	wxJsapiSignature.getAppid(),wxJsapiSignature.getNoncestr(),wxJsapiSignature.getTimestamp(),wxJsapiSignature.getUrl(),
	    	wxJsapiSignature.getSignature());
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
    }
    
	@Override
	protected GenericRepository<T> getDAO() {
		return null;
	}

	public WxJsapiSignature getWxJsapiSignature() {
		return wxJsapiSignature;
	}

	public void setWxJsapiSignature(WxJsapiSignature wxJsapiSignature) {
		this.wxJsapiSignature = wxJsapiSignature;
	}
}
