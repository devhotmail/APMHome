package com.ge.apm.view.asset;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetFileAttachmentController extends JpaCRUDController<AssetFileAttachment> {

    AssetFileAttachmentRepository dao = null;

    BlobObjectRepository blobDao;

    BlobFileService blobFileService;

    UserAccount currentUser;

    List<AssetInfo> assetList;

    V2_BlobObject blobObject;

    AssetInfo selectedAsset;

    private AssetInfoService assetService;
    private BlobFileService blobService;

    MicroServiceUtil microService = WebUtil.getBean(MicroServiceUtil.class);

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        microService = WebUtil.getBean(MicroServiceUtil.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        assetService = WebUtil.getBean(AssetInfoService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        this.filterBySite = true;
        if (!userContextService.hasRole("MultiHospital")) {
            this.filterByHospital = true;
        }
        this.setSiteFilter();

        AssetInfoService service = (AssetInfoService) WebUtil.getBean(AssetInfoService.class);
        assetList = service.getAssetList();
    }

    @Override
    protected AssetFileAttachmentRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetFileAttachment> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetFileAttachment> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    @Override
    public void onBeforeNewObject(AssetFileAttachment object) {
        object.setSiteId(currentUser.getSiteId());
        this.selectedAsset = null;

    }

    public List<AssetInfo> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<AssetInfo> assetList) {
        this.assetList = assetList;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    @Override
    public void save() {
        if (null == selectedAsset) {
            WebUtil.addErrorMessage(WebUtil.getMessage("assetName") + WebUtil.getMessage("ValidationRequire"));
        } else if (null == selected.getFileType() || selected.getFileType().isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("fileType") + WebUtil.getMessage("ValidationRequire"));
        } else if (null == selected.getName() || selected.getName().isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("SelectUploadFile"));
        } else if (selected.getFileType().equals("1") && !selected.getName().matches("^.*?\\.(jpg|jpeg|bmp|gif|png)$")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("InvalidPictureFileType"));
        } else {
            selected.setAssetId(selectedAsset.getId());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            blobObject.setBoId(selectedAsset.getId());
            blobObject.setBoUuid(selectedAsset.getUid());
            blobObject.setBoSubType(selected.getFileType());
            dao.save(selected);
            blobDao.save(blobObject);
            cancel();
        }
    }

    public void cancel() {
        this.selected = null;
        this.selectedAsset = null;
        this.blobObject = null;
    }

    @Override
    public void delete() {

        if (blobService.deleteBlobFile(blobObject)) {
            dao.delete(selected);
        }
        cancel();
    }

    public void handleFileUpload(FileUploadEvent event) {

        blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            selected.setName(blobObject.getObjectName());
            selected.setFileUrl(blobObject.getObjectStorageId());
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public void removeAttachment(Integer fileId) {

        if (blobService.deleteBlobFile(blobObject)) {
            selected.setName(null);
            selected.setFileUrl(null);
        }
        
    }

    public void onAssetSelected(SelectEvent event) {
        onServerEvent("assetSelected", event.getObject());
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }

        if (this.selected == null) {
            return;
        }
        this.selected.setAssetId(asset.getId());
        this.selected.setHospitalId(asset.getHospitalId());
        this.selectedAsset = asset;
    }

    public void onSelectedChange() {
        selectedAsset = assetService.getAssetInfo(selected.getAssetId());
        blobObject = blobService.getBlobObjectByStorageId(selected.getFileUrl());
        crudActionName = "Edit";
    }

    public boolean isCreatAction() {
        return "Create".equals(crudActionName);
    }
}
