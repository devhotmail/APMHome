package com.ge.apm.view.wechat;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.MessageSubscriber;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.MessageSubscriberService;
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
    private MessageSubscriberService msService;
    private AssetFileAttachmentRepository attachDao = null;
    private UserContextService userContextService;
    UserAccount currentUser;
    private AssetInfo assetInfo;
    private String qrCode;

    private String searchStr;
    private String searchGroup;
    private Boolean searchStatRun = true;
    private Boolean searchStatStop = true;
    private Boolean searchStatPartial = true;

    private StreamedContent picture;

    private Boolean bindResult = false;
    
    private MessageSubscriber messageSubscriber;

    @Override
    protected void init() {
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        msService = WebUtil.getBean(MessageSubscriberService.class);
        currentUser = userContextService.getLoginUser();
        qrCode = WebUtil.getRequestParameter("qrCode");
        String assetId = WebUtil.getRequestParameter("assetId");
        if (null != assetId && assetId.length() > 0) {
            assetInfo = assetDao.findById(Integer.parseInt(assetId));
        } else if (qrCode != null && qrCode.length() > 0) {
            if (qrCode.length() > 36) {
                qrCode = qrCode.substring(qrCode.length() - 36);
            }
            assetInfo = findAsset(qrCode);
        }
        if(null!= assetInfo){
            if(assetInfo.getSiteId()!=currentUser.getSiteId() ){
                assetInfo = null;
            }
            if(!assetInfo.getHospitalId().equals(currentUser.getHospitalId()) && !userContextService.hasRole("MultiHospital")){
                 assetInfo = null;
            }
        }
        if(null!= assetInfo){
            messageSubscriber = msService.getMessageSubscriber(assetInfo.getId(),currentUser.getId());
            if(null == messageSubscriber){
                messageSubscriber = new MessageSubscriber();
                messageSubscriber.setAssetId(assetInfo.getId());
                messageSubscriber.setSiteId(assetInfo.getSiteId());
                messageSubscriber.setHospitalId(assetInfo.getHospitalId());
                messageSubscriber.setSubscribeUserId(currentUser.getId());
            }
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

    public void onSearch() {
    }

    public List<AssetInfo> getAssetList() {

        List<SearchFilter> sfs = new ArrayList();
        sfs.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getSiteId()));
        if (!userContextService.hasRole("MultiHospital")) {
            sfs.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getHospitalId()));
        }
        if (null != searchStr) {
            sfs.add(new SearchFilter("name", SearchFilter.Operator.LIKE, searchStr));
        }
        if (null != searchGroup) {
            sfs.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, searchGroup));
        }
        if (!searchStatRun) {
            sfs.add(new SearchFilter("status", SearchFilter.Operator.NE, 1));
        }
        if (!searchStatStop) {
            sfs.add(new SearchFilter("status", SearchFilter.Operator.NE, 2));
        }
        if (!searchStatPartial) {
            sfs.add(new SearchFilter("status", SearchFilter.Operator.NE, 3));
        }
        return assetDao.findBySearchFilter(sfs);
    }

    public void settingMsgSubscriber(){
        int receibeMode = Integer.parseInt(WebUtil.getRequestParameter("subscribrSetting:subsMsgRadio"));
        messageSubscriber.setReceiveMsgMode(receibeMode);
        if(msService.saveOrUpdate(messageSubscriber)){
            WebUtil.addSuccessMessage("保存成功");
        }else{
            WebUtil.addErrorMessage("保存失败");
        }
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

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public String getSearchGroup() {
        return searchGroup;
    }

    public void setSearchGroup(String searchGroup) {
        this.searchGroup = searchGroup;
    }

    public Boolean getSearchStatRun() {
        return searchStatRun;
    }

    public void setSearchStatRun(Boolean searchStatRun) {
        this.searchStatRun = searchStatRun;
    }

    public Boolean getSearchStatStop() {
        return searchStatStop;
    }

    public void setSearchStatStop(Boolean searchStatStop) {
        this.searchStatStop = searchStatStop;
    }

    public Boolean getSearchStatPartial() {
        return searchStatPartial;
    }

    public void setSearchStatPartial(Boolean searchStatPartial) {
        this.searchStatPartial = searchStatPartial;
    }

    public MessageSubscriber getMessageSubscriber() {
        return messageSubscriber;
    }

    public void setMessageSubscriber(MessageSubscriber messageSubscriber) {
        this.messageSubscriber = messageSubscriber;
    }
    

}
