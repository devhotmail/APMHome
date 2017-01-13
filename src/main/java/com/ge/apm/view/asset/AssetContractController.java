package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetContractRepository;
import com.ge.apm.dao.AssetDepreciationRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.domain.AssetContract;
import com.ge.apm.domain.AssetDepreciation;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetContractController extends JpaCRUDController<AssetContract> {

    AssetContractRepository dao = null;

    AssetDepreciationRepository depredao = null;

    AttachmentFileService fileService;

    AssetInfo selectedAsset;

    private AssetInfoService assetService;

    private String fileName;

    @Override
    protected void init() {

        dao = WebUtil.getBean(AssetContractRepository.class);
        depredao = WebUtil.getBean(AssetDepreciationRepository.class);
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
            } else {

            }
        }

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        this.filterBySite = true;
        if (!userContextService.hasRole("MultiHospital")) {
            this.filterByHospital = true;
        }
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
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event); //To change body of generated methods, choose Tools | Templates.
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

        if (selected.getEndDate().before(selected.getStartDate())) {
            WebUtil.addErrorMessage(MessageFormat.format(WebUtil.getMessage("shouldEarly"), WebUtil.getMessage("startTime"), WebUtil.getMessage("endTime")));
        } else if (null == selectedAsset) {
            WebUtil.addErrorMessage(WebUtil.getMessage("assetName") + WebUtil.getMessage("ValidationRequire"));
        } else if (null == selected.getFileId()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("SelectUploadFile"));
        } else {
            selected.setAssetId(selectedAsset.getId());
            super.save();
            saveDepreciation();
            cancel();
        }
    }

    @Override
    public void onBeforeSave(AssetContract object) {
        DateTime temp;
        if (null != object.getStartDate()) {
            temp = new DateTime(object.getStartDate());
            object.setStartDate(temp.plusHours(12).toDate());
        }
        if (null != object.getEndDate()) {
            temp = new DateTime(object.getEndDate());
            object.setEndDate(temp.plusHours(12).toDate());
        }
    }

    private void saveDepreciation() {
        List<AssetDepreciation> depreList = depredao.getByContractId(selected.getId());
        depredao.delete(depreList);
        AssetDepreciation depre = new AssetDepreciation();
        depre.setAssetId(selected.getAssetId());
        depre.setSiteId(selected.getSiteId());
        depre.setContractId(selected.getId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selected.getEndDate());
        int endMonth = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH);
        calendar.setTime(selected.getStartDate());
        int startMonth = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH);
        int times = (endMonth - startMonth + 1);
        depre.setDeprecateAmount(selected.getContractAmount() / times);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        depre.setDeprecateDate(calendar.getTime());
        depredao.save(depre);
        for (int i = 1; i < times; i++) {
            calendar.add(Calendar.MONTH, 1);
            depre.setDeprecateDate(calendar.getTime());
            depre.setId(null);
            depredao.save(depre);
        }

    }

    public void cancel() {
        this.selected = null;
        this.selectedAsset = null;
    }

    @Override
    public void delete() {
        fileService.deleteAttachment(selected.getFileId());
        List<AssetDepreciation> depreList = depredao.getByContractId(selected.getId());
        depredao.delete(depreList);
        dao.delete(selected);
        this.selected = null;
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
        this.selected.setHospitalId(asset.getHospitalId());
        this.selectedAsset = asset;
    }

    public void onSelectedChange() {
        selectedAsset = assetService.getAssetInfo(selected.getAssetId());
        fileName = fileService.getFileNameById(selected.getFileId());
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
