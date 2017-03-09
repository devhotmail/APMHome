package com.ge.apm.view.wechat;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.StreamedContent;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class WxAssetInfoController extends JpaCRUDController<AssetInfo> {

    private static final long serialVersionUID = -1L;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private AssetInfoRepository assetDao;
    private AssetFileAttachmentRepository attachDao = null;
    private UserContextService userContextService;
    private AssetInfo assetInfo;
    private String qrCode;

    private String action;
    private String searchStr;

    private StreamedContent picture;

    private Boolean bindResult = false;

    @Override
    protected void init() {
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        qrCode = WebUtil.getRequestParameter("qrCode");
        action = WebUtil.getRequestParameter("action");
        String assetId = WebUtil.getRequestParameter("assetId");
        if (null != assetId && assetId.length() > 0) {
            assetInfo = assetDao.findById(Integer.parseInt(assetId));
        } else if (qrCode != null && qrCode.length() > 0) {
            assetInfo = findAsset(qrCode);
        }

    }

    public List<AssetFileAttachment> getPictureList(Integer assetid) {

        List<SearchFilter> sfs = new ArrayList();
        sfs.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetid));
        sfs.add(new SearchFilter("fileType", SearchFilter.Operator.EQ, "1"));
        List<AssetFileAttachment> pictures = attachDao.findBySearchFilter(sfs);

        if (!pictures.isEmpty() && pictures.size() > 3) {
            return pictures.subList(0, 2);
        }
        return pictures;
    }


    private AssetInfo findAsset(String qrCode) {
        if (null == qrCode || qrCode.length() == 0) {
            return null;
        }
        List<AssetInfo> resList = assetDao.getByQrCode(qrCode);
        if (resList.isEmpty()) {
            return null;
        } else if (resList.size() == 1) {
            return resList.get(0);
        } else {
            return null;
        }
    }

    public void bindingAsset() {
        if (qrCode != null && qrCode.length() > 0 && null != assetInfo) {
            assetInfo.setQrCode(qrCode);
            assetDao.save(assetInfo);
            bindResult = true;
        }
    }

    public void onSearch(){
    }
    
    public List<AssetInfo> getAssetList() {
        
        List<SearchFilter> sfs = new ArrayList();
        sfs.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getSiteId()));
        if(!userContextService.hasRole("MultiHospital")){
            sfs.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getHospitalId()));
        }
        if(null!=searchStr ){
            sfs.add(new SearchFilter("name", SearchFilter.Operator.LIKE, searchStr));
        }
        return assetDao.findBySearchFilter(sfs);
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return assetDao;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getQrCode() {
        return qrCode;
    }

    public Boolean getBindResult() {
        return bindResult;
    }

    public StreamedContent getPicture() {
        return picture;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    
}
