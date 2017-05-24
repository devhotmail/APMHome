package com.ge.apm.view.wechat;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.MessageSubscriber;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.MessageSubscriberService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.QRCodeUtil;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
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
    private QrCodeLibRepository qrLibDao;
    private MessageSubscriberService msService;
    private AssetFileAttachmentRepository attachDao;
    private AssetCreateService acService;
    private UserContextService userContextService;
    private V2_WorkOrderService v2WOService;
    private UaaService uaaService;
    private UserAccount currentUser;
    private AssetInfo assetInfo;
    private String qrCode;

    private String searchStr;
    private String searchGroup;
    private Integer searchDept;
    private Boolean searchStatRun = true;
    private Boolean searchStatStop = true;
    private Boolean searchStatPartial = true;

    private StreamedContent picture;

    private Boolean bindResult = false;

    private MessageSubscriber messageSubscriber;

    private List<V2_ServiceRequest> srList;

    //设备查询 0:无效编码;1:未创建档案;2:建档中;3:无权限查看
    private Integer assetFlag = 0;

    private Boolean[] isModifying = {false, false, false};

    private String removedPicStr;

    private String addedPicStr;

    @Override
    protected void init() {
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        qrLibDao = WebUtil.getBean(QrCodeLibRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        v2WOService = WebUtil.getBean(V2_WorkOrderService.class);
        msService = WebUtil.getBean(MessageSubscriberService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        currentUser = WebUtil.getUserAccountFromRequest();
//        userContextService.getLoginUser();
//        System.out.println("========================current user is "+currentUser.getLoginName());

        if (userContextService.hasRole("Guest")) {
            try {
                FacesContext face = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) face.getExternalContext().getResponse();
                response.sendRedirect("../noPremission.jsp");
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(WxAssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

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
        if (null != assetInfo) {
            if (assetInfo.getSiteId() != currentUser.getSiteId()) {
                assetInfo = null;
                assetFlag = 3;
            } else if (!assetInfo.getHospitalId().equals(currentUser.getHospitalId()) && !userContextService.hasRole("MultiHospital")) {
                assetInfo = null;
                assetFlag = 3;
            }
        }
        if (null != assetInfo) {
            messageSubscriber = msService.getMessageSubscriber(assetInfo.getId(), currentUser.getId());
            if (null == messageSubscriber) {
                messageSubscriber = new MessageSubscriber();
                messageSubscriber.setAssetId(assetInfo.getId());
                messageSubscriber.setSiteId(assetInfo.getSiteId());
                messageSubscriber.setHospitalId(assetInfo.getHospitalId());
                messageSubscriber.setSubscribeUserId(currentUser.getId());
            }
            srList = findSrList();
        }

    }

    public List<Object[]> getSearchDeptList() {
        List<Object[]> res = new ArrayList();
        if (userContextService.hasRole("MultiHospital")) {
            List<OrgInfo> hospitals = uaaService.getHospitalListBySiteId(UserContextService.getCurrentUserAccount().getSiteId());

            for (OrgInfo item : hospitals) {
                List<OrgInfo> oneHospital = acService.getOrgListByHospital(item.getHospitalId());
                for (OrgInfo dept : oneHospital) {
                    Object[] o = {item.getName().concat("-").concat(dept.getName()), dept.getId()};
                    res.add(o);
                }
            }
        } else {
            List<OrgInfo> depts = acService.getOrgListByHospital(currentUser.getHospitalId());
            for (OrgInfo item : depts) {
                Object[] o = {item.getName(), item.getId()};
                res.add(o);
            }
        }
        return res;
    }

    private List<V2_ServiceRequest> findSrList() {

        return v2WOService.getServiceRequestByAssetId(assetInfo.getId());
    }

    public List<V2_WorkOrder> getWorkOrderList(String srId) {
        return v2WOService.getWorkOrdersBySR(srId);
    }

    public String getUserOpenId() {
        return userContextService.getLoginUser().getWeChatId();
    }

    public List<AssetFileAttachment> getPictureList(Integer assetid) {

        List<SearchFilter> sfs = new ArrayList();
        sfs.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetid));
        sfs.add(new SearchFilter("fileType", SearchFilter.Operator.EQ, "1"));
        List<AssetFileAttachment> pictures = attachDao.findBySearchFilter(sfs);

        return pictures;
    }

    private AssetInfo findAsset(String qrCode) {
        if (null == qrCode || qrCode.length() == 0) {
            assetFlag = 0;
            return null;
        }
        List<AssetInfo> resList = assetDao.getByQrCode(qrCode);
        if (resList.isEmpty()) {
            QrCodeLib qrLib = qrLibDao.findByQrCode(qrCode);
            if (null == qrLib) {
                assetFlag = 0;
                return null;
            } else {
                assetFlag = qrLib.getStatus();
                return null;
            }
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
        if (null != searchDept) {
            sfs.add(new SearchFilter("clinicalDeptId", SearchFilter.Operator.EQ, searchDept));
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

    public void settingMsgSubscriber() {
        int receibeMode = Integer.parseInt(WebUtil.getRequestParameter("subscribrSetting:subsMsgRadio"));
        messageSubscriber.setReceiveMsgMode(receibeMode);
        if (msService.saveOrUpdate(messageSubscriber)) {
            WebUtil.addSuccessMessage("保存成功");
        } else {
            WebUtil.addErrorMessage("保存失败");
        }
    }

    public String getQrCodeImageBase64() {
        return QRCodeUtil.getQRCodeImageBase64(assetInfo.getQrCode());
    }

    public void updateAttachmentPics() {

        if (!removedPicStr.isEmpty()) {
            String[] removeList = removedPicStr.split(",");

            for (String item : removeList) {
                acService.removeAttachment(assetInfo.getId(), Integer.parseInt(item));
            }
            removedPicStr = "";
        }

        if (!addedPicStr.isEmpty()) {
            String[] addedPicList = addedPicStr.split(",");
            for (String item : addedPicList) {
                acService.addWechatPicAttachment(assetInfo, item);
            }
            addedPicStr = "";
        }
    }

    public void updateFields(Integer index) {
        if (isModifying[index]) {
            assetDao.save(assetInfo);
        }
        isModifying[index] = !isModifying[index];
    }

    public List<OrgInfo> getClinicalDeptList() {
        return acService.getClinicalDeptList(assetInfo.getHospitalId());
    }

    public void onClinicalDeptChange() {
        OrgInfo clinicalDept = acService.getOrgInfo(assetInfo.getClinicalDeptId());
        assetInfo.setClinicalDeptName(clinicalDept.getName());
        assetInfo.setClinicalOwnerId(null);
        assetInfo.setClinicalOwnerName(null);
        assetInfo.setClinicalOwnerTel(null);
    }

    public void onClinicalOwnerChange() {
        UserAccount owner = acService.getUserInfo(assetInfo.getClinicalOwnerId());
        assetInfo.setClinicalOwnerName(owner.getName());
        assetInfo.setClinicalOwnerTel(owner.getTelephone());
    }

    public void onAssetOwnerChange() {
        UserAccount owner = acService.getUserInfo(assetInfo.getAssetOwnerId());
        assetInfo.setAssetOwnerName(owner.getName());
        assetInfo.setAssetOwnerTel(owner.getTelephone());
    }

    public void onAssetOwner2Change() {
        UserAccount owner = acService.getUserInfo(assetInfo.getAssetOwnerId2());
        assetInfo.setAssetOwnerName2(owner.getName());
        assetInfo.setAssetOwnerTel2(owner.getTelephone());
    }

    public List<UserAccount> getAssetOwnerList() {
        return acService.getAssetOnwers(assetInfo.getSiteId(), assetInfo.getHospitalId());
    }

    public List<UserAccount> getClinicalOwnerList() {
        return acService.getClinicalOwnerList(assetInfo.getClinicalDeptId());
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

    public List<V2_ServiceRequest> getSrList() {
        return srList;
    }

    public Integer getSearchDept() {
        return searchDept;
    }

    public void setSearchDept(Integer searchDept) {
        this.searchDept = searchDept;
    }

    public Integer getAssetFlag() {
        return assetFlag;
    }

    public Boolean[] getIsModifying() {
        return isModifying;
    }

    public void setIsModifying(Boolean[] isModifying) {
        this.isModifying = isModifying;
    }

    public String getRemovedPicStr() {
        return removedPicStr;
    }

    public void setRemovedPicStr(String removedPicStr) {
        this.removedPicStr = removedPicStr;
    }

    public String getAddedPicStr() {
        return addedPicStr;
    }

    public void setAddedPicStr(String addedPicStr) {
        this.addedPicStr = addedPicStr;
    }

}
