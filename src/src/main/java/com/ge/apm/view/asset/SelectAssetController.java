package com.ge.apm.view.asset;

import java.util.List;
import javax.faces.bean.ManagedBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import java.util.HashMap;
import java.util.Map;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class SelectAssetController extends JpaCRUDController<AssetInfo> {

    AssetInfoRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        this.filterByHospital = true;
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        return super.loadData(pageRequest);
    }

    public List<AssetInfo> getOrgAssetTree() {
        return dao.findBySearchFilter(searchFilters);
    }

    public void showDialog(Integer contentWidth, Integer contentHeight, boolean isDialogResizable, boolean isDialogDraggable){
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("resizable", isDialogResizable);
        options.put("draggable", isDialogDraggable);
        options.put("modal", true);
        options.put("contentWidth", contentWidth);
        options.put("contentHeight", contentHeight);
        
        RequestContext.getCurrentInstance().openDialog("/portal/asset/selectAsset.xhtml", options, null);
    }
    
    public void cencelDialog(){
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void closeDialog() {
        confirmSelection();
        
        RequestContext.getCurrentInstance().closeDialog(this.selected);
    }    

    public void confirmSelection(){
        if(this.selected==null){
            WebUtil.addErrorMessageKey("noAssetSelected");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);

            return;
        }
        
        if(callingController!=null){
            callingController.onServerEvent("onAssetSelected", this.selected);
        }
    }

    private String updateViewIDs;
    public String getUpdateViewIDs() {
        return updateViewIDs;
    }
    
    private JpaCRUDController callingController;
    public void prepareDialogCallback(JpaCRUDController callingController, String updateViewIDs){
        showDialog = true;
        
        this.callingController = callingController;
        this.updateViewIDs = updateViewIDs;
    }

    public void cancelDialog(){
        showDialog = false;
    }
    
    private boolean showDialog = false;
    public boolean isShowDialog() {
        return showDialog;
    }
    
}
