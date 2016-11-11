package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.web.WebUtil;
import webapp.framework.dao.SearchFilter;

@ManagedBean
@ViewScoped
public class AssetInfoController extends JpaCRUDController<AssetInfo> {

    AssetInfoRepository dao = null;

    AssetFileAttachmentRepository attachDao = null;

    private boolean resultStatus;
    private UserAccount currentUser;

    private UserAccount owner;
    private List<UserAccount> ownerList;

    private List<AssetFileAttachment> attachements;

    @ManagedProperty("#{attachmentFileService}")
    private AttachmentFileService fileService;

    private UaaService uuaService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        uuaService = (UaaService) WebUtil.getBean(UaaService.class);
        this.filterBySite = true;
        this.setSiteFilter();

        String actionName = WebUtil.getRequestParameter("actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("View".equalsIgnoreCase(actionName)) {
            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            owner = userDao.findById(selected.getAssetOwnerId());
            prepareView();
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            owner = userDao.findById(selected.getAssetOwnerId());
            prepareEdit();
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            owner = userDao.findById(selected.getAssetOwnerId());
            prepareDelete();
        }

        ownerList = uuaService.getUserList(currentUser.getHospitalId());
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount currentUser) {
        this.currentUser = currentUser;
    }

    public void setFileService(AttachmentFileService fileService) {
        this.fileService = fileService;
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetInfo> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    public String getViewPage(String pageName, String actionName) {
        return pageName + "?faces-redirect=true&actionName=" + actionName + "&selectedid=" + selected.getId();
    }

    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if (isOK) {
            saveAttachements(object.getId());
        }
    }

    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if (isOK) {
            saveAttachements(object.getId());
        }
    }

    @Override
    public void onAfterDeleteObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    public String removeOne() {
        this.delete();
        if (resultStatus) {
            return "List";
        } else {
            return "";
        }
    }

    public String applyChange() {
        if ("Create".equalsIgnoreCase(this.crudActionName)) {
            selected.setSiteId(currentUser.getSiteId());
            selected.setHospitalId(currentUser.getHospitalId());
        }
        this.save();
        if (resultStatus) {
            return "List";
        } else {
            return "";
        }
    }

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public List<UserAccount> getOwnerList() {
        return ownerList;
    }

    public void setOwnerList(List<UserAccount> ownerList) {
        this.ownerList = ownerList;
    }

    public void onOwnerChange() {
        if (null != owner) {
            selected.setAssetOwnerId(owner.getId());
            selected.setAssetOwnerName(owner.getName());
            selected.setAssetOwnerTel(owner.getTelephone());
        }
    }

    public void handleFileUpload(FileUploadEvent event) {

        String type = event.getComponent().getId();
        type = type.substring(type.indexOf("type") + 4);
        AssetFileAttachment attach = new AssetFileAttachment();
        String fileName = fileService.getFileName(event.getFile());
        attach.setName(fileName);
        attach.setFileUrl(fileService.getFileUrl() + fileName);
        attach.setFileType(type);
        attach.setSiteId(currentUser.getSiteId());
        if (fileService.uploadFile(event.getFile(), attach.getFileUrl())) {
            WebUtil.addSuccessMessage("Succesful", fileName + " is uploaded.");
            attachements.add(attach);
        }
    }

    public List<AssetFileAttachment> getAttachList(Integer assetid, String type) {
        if (attachements == null || attachements.isEmpty()) {
            List<SearchFilter> sfs = new ArrayList();
            sfs.addAll(this.searchFilters);
            sfs.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetid));
            attachements = attachDao.findBySearchFilter(sfs);
        }

        List<AssetFileAttachment> result = new ArrayList();

        for (AssetFileAttachment item : attachements) {
            if (item.getFileType().equals(type)) {
                result.add(item);
            }
        }
        return result;
    }

    private void saveAttachements(int assetId) {
        for (AssetFileAttachment item : attachements) {
            if (null == item.getId()) {
                item.setAssetId(assetId);
                fileService.addAttachment(item, currentUser.getHospitalId());
            }
        }
        attachements.clear();
    }

    public void removeAttachment(String attachUrl) {
        for (AssetFileAttachment item : attachements) {
            if (attachUrl.equals(item.getFileUrl())) {
                attachements.remove(item);
                fileService.removeAttachment(item);
                break;
            }
        }
    }
}
