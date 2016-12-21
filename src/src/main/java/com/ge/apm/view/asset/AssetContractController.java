package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetContractRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.domain.AssetContract;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetContractController extends JpaCRUDController<AssetContract> {

    AssetContractRepository dao = null;

    AttachmentFileService fileService;

    AssetInfo selectedAsset;

    private AssetInfoService assetService;

    private String fileName;

    @Override
    protected void init() {

        dao = WebUtil.getBean(AssetContractRepository.class);
        fileService = WebUtil.getBean(AttachmentFileService.class);
        assetService = WebUtil.getBean(AssetInfoService.class);

        String encodeStr = WebUtil.getRequestParameter("str");
        if (null != encodeStr) {
            String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
            String requestAssetId = (String) UrlEncryptController.getValueFromMap(encodeStr, "assetId");
            if (null != actionName && actionName.equals("Create")) {
                try {
                    this.prepareCreate();
                    this.selected.setAssetId(Integer.parseInt(requestAssetId));
                    this.selectedAsset = assetService.getAssetInfo(Integer.parseInt(requestAssetId));
                } catch (InstantiationException ex) {
                    Logger.getLogger(AssetContractController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AssetContractController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                
            }
        }

        this.filterBySite = true;
        this.setSiteFilter();
    }

    @Override
    protected AssetContractRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetContract> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetContract> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    @Override
    public void onBeforeNewObject(AssetContract object) {
        object.setSiteId(UserContextService.getSiteId());
        this.selectedAsset = null;
    }

    @Override
    public void save() {

        selected.setAssetId(selectedAsset.getId());
        dao.save(selected);
        cancel();
    }

    public void cancel() {
        this.selected = null;
        this.selectedAsset = null;
    }

    @Override
    public void delete() {
    }

    public void handleFileUpload(FileUploadEvent event) {
        fileName = fileService.getFileName(event.getFile());
        Integer id = fileService.uploadFile(event.getFile());
        selected.setFileId(id);
    }

    public void removeAttachment(Integer fileId) {
        fileService.deleteAttachment(fileId);
        selected.setName(null);
        selected.setFileId(null);
        this.fileName = "";
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

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
