package com.ge.apm.view.wechat;

import java.util.Enumeration;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class WxAssetInfoController  extends JpaCRUDController<AssetInfo>{

	private static final long serialVersionUID = -1L;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private AssetInfoRepository dao = null;
    private AssetInfo assetInfo;
    private Integer assetId;
    
    @Override
    protected void init() {
    	dao = WebUtil.getBean(AssetInfoRepository.class);
    	HttpServletRequest request =((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
		Enumeration em = request.getParameterNames();
    	 while (em.hasMoreElements()) {
    	    String name = (String) em.nextElement();
    	    logger.info(" current name is {}",name);
    	    if(name.equals("assetId")){
    	    	String value = request.getParameter(name);
    	    	try{
    	    		assetId = Integer.parseInt(value);
    	    	}catch(NumberFormatException ex){
    	    		assetId = 0;
    	    		logger.error("assetId format error,msg is {}",ex.getMessage());;
    	    	}
    	    }
    	}
//    	assetInfo =  dao.findById(1);
    	assetInfo =  dao.findById(assetId);
    }
    
	@Override
	protected GenericRepository<AssetInfo> getDAO() {
		return dao;
	}

	public AssetInfo getAssetInfo() {
		return assetInfo;
	}

	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}

	public Integer getAssetId() {
		return assetId;
	}

	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}
}
