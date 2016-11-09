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

    private boolean isDialogResizable = true;
    private boolean isDialogDraggable = true;

    public boolean isIsDialogResizable() {
        return isDialogResizable;
    }

    public void setIsDialogResizable(boolean isDialogResizable) {
        this.isDialogResizable = isDialogResizable;
    }

    public boolean isIsDialogDraggable() {
        return isDialogDraggable;
    }

    public void setIsDialogDraggable(boolean isDialogDraggable) {
        this.isDialogDraggable = isDialogDraggable;
    }
    public void showDialog(){
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("resizable", isDialogResizable);
        options.put("draggable", isDialogDraggable);
        options.put("modal", true);
        
        RequestContext.getCurrentInstance().openDialog("/portal/asset/selectAsset.xhtml", options, null);
    }
    public void showDialog2(){
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("resizable", isDialogResizable);
        options.put("draggable", isDialogDraggable);
        options.put("modal", true);
        
        RequestContext.getCurrentInstance().openDialog("/geapm/portal/asset/selectAsset.xhtml", options, null);
    }
    
    public void cencelDialog(){
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void closeDialog() {
        if(this.selected==null){
            WebUtil.addErrorMessageKey("noAssetSelected");
            return;
        }

        RequestContext.getCurrentInstance().closeDialog(this.selected);
    }    
    
}
