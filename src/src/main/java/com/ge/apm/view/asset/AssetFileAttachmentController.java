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
import com.ge.apm.service.asset.FileUploadService;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetFileAttachmentController extends JpaCRUDController<AssetFileAttachment> {

    AssetFileAttachmentRepository dao = null;
    
    FileUploadService fileService;

    UserAccount currentUser;
    
    
    List<AssetInfo> assetList;
    
    AssetInfo selectedAsset;
    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        fileService = WebUtil.getBean(FileUploadService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.filterBySite = true;
        this.setSiteFilter();
        
        AssetInfoService service = (AssetInfoService) WebUtil.getBean(AssetInfoService.class);
        assetList =  service.getAssetList();
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
        //to do: change the code if necessary
        return dao.findBySearchFilter(searchFilters);
    }

    @Override
    public void onBeforeNewObject(AssetFileAttachment object) {
        object.setSiteId(currentUser.getSiteId());
        if(null!=selectedAsset)
         selected.setAssetId(selectedAsset.getId());
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
    
    
    
/*
    
    
    @Override
    public void onAfterNewObject(AssetFileAttachment object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(AssetFileAttachment object) {
    }
    
    @Override
    public void onAfterUpdateObject(AssetFileAttachment object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(AssetFileAttachment object) {
    }
    
    @Override
    public void onAfterDeleteObject(AssetFileAttachment object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(AssetFileAttachment object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
    
    
    public void handleFileUpload(FileUploadEvent event) {
        
        String fileName = "";
        try {
            fileName = new String(event.getFile().getFileName().getBytes(),"utf-8") ;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AssetFileAttachmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        selected.setName(fileName);
        selected.setFileUrl("C:\\uploads/"+fileName);
        if(fileService.uploadFile(event.getFile())){
            FacesMessage message = new FacesMessage("Succesful", fileName + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
    
    public void chooseAsset() {
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        RequestContext.getCurrentInstance().openDialog("selectAsset", options, null);
    }
     
    public void onAssetChosen(SelectEvent event) {
        AssetInfo asset = (AssetInfo) event.getObject();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Asset Selected", "Id:" + asset.getId());
        FacesContext.getCurrentInstance().addMessage(null, message);
        this.selectedAsset = asset;
        selected.setAssetId(asset.getId());
    }
    
    public void selectAssetFromDialog(AssetInfo asset){
        RequestContext.getCurrentInstance().closeDialog(asset);
        this.selectedAsset = asset;
    }
    
    public List<AssetFileAttachment> getAttachList(Integer assetid, String type){
        List<SearchFilter> sfs = new ArrayList();
        sfs.addAll(this.searchFilters);
        sfs.add(new SearchFilter("assetId",SearchFilter.Operator.EQ,assetid));
        sfs.add(new SearchFilter("fileType",SearchFilter.Operator.EQ,type));
        List<AssetFileAttachment> result = dao.findBySearchFilter(sfs);
        return result;
    }
    
}