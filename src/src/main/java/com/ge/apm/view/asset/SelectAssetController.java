package com.ge.apm.view.asset;

import java.util.List;
import javax.faces.bean.ManagedBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

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

    private TreeNode orgAssetTree;
    public TreeNode getOrgAssetTree() {
        return orgAssetTree;
    }

    public void onSelectRow(){
        selectedAsset = this.selected;
    }

    protected TreeNode selectedNode;
    public TreeNode getSelectedNode() {
        return selectedNode;
    }
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
    
    public void onSelectTreeNode(NodeSelectEvent event){
        TreeNode node = event.getTreeNode();
        node.setExpanded(true);
        
        Object nodeData = node.getData();
        if(nodeData instanceof AssetInfo)
            selectedAsset = (AssetInfo)nodeData;
        else
            selectedAsset = null;
    }

    private AssetInfo selectedAsset = null;
    
    public void confirmSelection(){
        if(this.selectedAsset==null){
            WebUtil.addErrorMessageKey("noAssetSelected");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }

        showDialog = false;
        if(callingController!=null)
            callingController.onServerEvent("onAssetSelected", this.selectedAsset);
    }

    private String updateViewIDs;
    public String getUpdateViewIDs() {
        return updateViewIDs;
    }
    
    private ServerEventInterface callingController;

    public void prepareDialogCallback(ServerEventInterface callingController, String updateViewIDs){
        showDialog = true;

        this.selected = null;
        this.selectedNode = null;
        this.selectedAsset = null;
        
        this.callingController = callingController;
        this.updateViewIDs = updateViewIDs;

        UaaService uaaService = WebUtil.getBean(UaaService.class);
        orgAssetTree = uaaService.getOrgAssetTree(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    public void cancelDialog(){
        showDialog = false;
    }
    
    private boolean showDialog = false;
    public boolean isShowDialog() {
        return showDialog;
    }

    private TreeNode[] selectedNodes;
    public TreeNode[] getSelectedNodes() {
        return selectedNodes;
    }
    public void setSelectedNodes(TreeNode[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }
}
