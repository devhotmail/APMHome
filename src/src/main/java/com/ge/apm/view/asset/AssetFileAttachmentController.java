package com.ge.apm.view.asset;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.view.sysutil.UserContextService;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetFileAttachmentController extends JpaCRUDController<AssetFileAttachment> {

    AssetFileAttachmentRepository dao = null;

    AttachmentFileService fileService;

    UserAccount currentUser;

    List<AssetInfo> assetList;

    AssetInfo selectedAsset;

    private AssetInfoService assetService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        assetService = WebUtil.getBean(AssetInfoService.class);
        fileService = WebUtil.getBean(AttachmentFileService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.filterBySite = true;
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
        } else if (null == selected.getName() || selected.getName().isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("name") + WebUtil.getMessage("ValidationRequire"));
        } else if (null == selected.getFileType() || selected.getFileType().isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("fileType") + WebUtil.getMessage("ValidationRequire"));
        } else {
            selected.setAssetId(selectedAsset.getId());
            dao.save(selected);
            cancel();
        }
    }

    public void cancel() {
        this.selected = null;
        this.selectedAsset = null;
    }

    @Override
    public void delete() {
        if (fileService.deleteAttachment(selected.getFileId())) {
            dao.delete(selected);
            cancel();
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        String fileName = fileService.getFileName(event.getFile());
        selected.setName(fileName);
        Integer id = fileService.uploadFile(event.getFile());
        selected.setFileId(id);
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
        this.selectedAsset = asset;
    }

    public void onSelectedChange() {
        selectedAsset = assetService.getAssetInfo(selected.getAssetId());
        crudActionName = "Edit";
    }

    public boolean isCreatAction() {
        return "Create".equals(crudActionName);
    }
}
