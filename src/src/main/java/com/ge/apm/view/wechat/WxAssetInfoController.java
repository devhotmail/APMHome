package com.ge.apm.view.wechat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

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
    @Override
    protected void init() {
    	dao = WebUtil.getBean(AssetInfoRepository.class);
    	assetInfo = dao.findById(1);
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

	
}
