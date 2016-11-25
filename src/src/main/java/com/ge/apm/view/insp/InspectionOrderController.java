package com.ge.apm.view.insp;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.InspectionOrderRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InspectionOrder;
import com.ge.apm.domain.InspectionOrderDetail;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.insp.InspectionService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.asset.AssetInfoController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InspectionOrderController extends JpaCRUDController<InspectionOrder> {

    InspectionOrderRepository dao = null;

    private TreeNode orgAssetTree;

    List<TreeNode> selectedNodesList;
    TreeNode[] selectedNodes;

    @ManagedProperty("#{inspectionService}")
    InspectionService inspectionService;

    int assetGrossCount;
    int period;

    UserAccount owner;
    private List<UserAccount> ownerList;
    private UaaService uuaService;

    OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);

    List<InspectionOrderDetail> orderDetailItemList;

    private boolean allPass;

    AttachmentFileService fileService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(InspectionOrderRepository.class);
        fileService = WebUtil.getBean(AttachmentFileService.class);
        uuaService = WebUtil.getBean(UaaService.class);
        orgAssetTree = uuaService.getOrgAssetTree(UserContextService.getCurrentUserAccount().getHospitalId());
        assetGrossCount = getTreeCount(orgAssetTree);

        String actionName = WebUtil.getRequestParameter("actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
                owner = new UserAccount();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("View".equalsIgnoreCase(actionName)) {
            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            owner = inspectionService.getUserAccountById(selected.getOwnerId());
            orderDetailItemList = inspectionService.getDetailList(selected.getId());
            prepareView();
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            owner = inspectionService.getUserAccountById(selected.getOwnerId());
            orderDetailItemList = inspectionService.getDetailList(selected.getId());
            prepareEdit();
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            owner = inspectionService.getUserAccountById(selected.getOwnerId());
            orderDetailItemList = inspectionService.getDetailList(selected.getId());
            prepareDelete();
        }

        ownerList = uuaService.getUserList(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    private int getTreeCount(TreeNode node) {
        int sum = 0;
        if ("asset".equals(node.getType())) {
            return 1;
        } else if (node.getChildCount() == 0) {
            return 0;
        }
        for (TreeNode item : node.getChildren()) {
            sum += getTreeCount(item);
        }
        return sum;
    }

    @Override
    protected InspectionOrderRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<InspectionOrder> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<InspectionOrder> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    public void onUnselectTreeNode(NodeUnselectEvent event) {
//        if (!event.getTreeNode().getType().equals("asset")) {
//            for (TreeNode item : event.getTreeNode().getChildren()) {
//                item.setSelected(false);
//            }
//        }
        selectedNodesList = getSelectedAsset();
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
//        if (event.getTreeNode().getType() == "org") {
//            for (TreeNode item : event.getTreeNode().getChildren()) {
//                item.setSelected(true);
//            }
//        }

        selectedNodesList = getSelectedAsset();
    }

    public void removeSelectedAsset(TreeNode node) {

        selectedNodesList.remove(node);

//        for(TreeNode item: selectedNodesList){
//            if(((AssetInfo)item.getData()).getId()!=((AssetInfo)node.getData()).getId()){
//                selectedNodesList.remove(item);
//            }
//        }
        node.setSelected(false);
//        AssetInfo assetData = (AssetInfo)node.getData();
//        List<TreeNode> tempList = Arrays.asList(selectedNodes);
//        for (TreeNode item : selectedNodes) {
//            if (item.getType().equals("asset") && assetData.getId()!= ((AssetInfo)item.getData()).getId()) {
//                tempList.remove(item);
//            }
//        }
//        
//        node.setSelected(false);
//        selectedNodes = tempList.toArray(selectedNodes);
    }

    public List<TreeNode> getSelectedAsset() {
        List<TreeNode> tempList = new ArrayList();
        for (TreeNode item : selectedNodes) {
            if (item.getType() == "asset") {
                tempList.add(item);
            }
        }
        return tempList;
    }

//    public List<TreeNode> getSelectedAsset(TreeNode treeNode) {
//
//        List<TreeNode> tempList = new ArrayList();
//
//        if (treeNode.getType() == "asset") {
//            if (treeNode.isSelected()) {
//                tempList.add(treeNode);
//            }
//            return tempList;
//        } else {
//            for (TreeNode item : treeNode.getChildren()) {
//                if (treeNode.isSelected()) {
//                    item.setSelected(true);
//                }
//                tempList.addAll(getSelectedAsset(item));
//            }
//        }
//        return tempList;
//    }
    public String createOrder() {

        if (selectedNodesList.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("noAssetSelected"));
            return "";
        }

        boolean isSuccess = true;
        for (TreeNode item : selectedNodesList) {
            AssetInfo asset = (AssetInfo) item.getData();
//            selected.setAssetId(asset.getId());
            isSuccess = isSuccess && inspectionService.createOrder(selected, period);
        }
        if (isSuccess) {
            return "InspOrderList";
        } else {
            return "";
        }
    }

    @Override
    public void onBeforeNewObject(InspectionOrder object) {
        object.setSiteId(UserContextService.getSiteId());
        object.setCreatorId(UserContextService.getCurrentUserAccount().getId());
        object.setCreatorName(UserContextService.getCurrentUserAccount().getName());
        object.setOrderType(1);
        object.setCreateTime(new Date());

    }

    public void onOwnerChange() {
        this.selected.setOwnerId(owner.getId());
        this.selected.setOwnerName(owner.getName());
        this.selected.setOwnerOrgId(owner.getOrgInfoId());
        OrgInfo org = orgDao.findById(owner.getOrgInfoId());
        this.selected.setOwnerOrgName(org.getName());
    }

    public String getViewPage(String pageName, String actionName) {
        return pageName + "?faces-redirect=true&actionName=" + actionName + "&selectedid=" + selected.getId();
    }

    public String removeOne() {
        if (inspectionService.deleteOrder(this.selected)) {
            return "InspOrderList";
        } else {
            return "";
        }
    }

    public void selectAllPass() {
        for (InspectionOrderDetail item : orderDetailItemList) {
            item.setIsPassed(allPass);
        }
    }

    public String excuteOrder() {
        boolean isFinished = true;
        for (InspectionOrderDetail item : orderDetailItemList) {
            isFinished = isFinished && item.getIsPassed();
        }
        this.selected.setIsFinished(isFinished);
        inspectionService.excuteOrder(this.selected, orderDetailItemList);

        return "InspOrderList";
    }

    public String updateOrder() {
        inspectionService.updateOrder(this.selected, orderDetailItemList);
        return "InspOrderList";
    }

    public void handleFileUpload(FileUploadEvent event) {
//        String fileName = fileService.getFileName(event.getFile());
//        selected.setName(fileName);
        Integer id = fileService.uploadFile(event.getFile());
        selected.setPaperUrl(id.toString());
    }
    
    public void removeAttach(){
        fileService.deleteAttachment(Integer.valueOf(selected.getPaperUrl()));
        selected.setPaperUrl(null);
        
    }

    /*
    @Override
    public void onBeforeNewObject(InspectionOrder object) {
    }
    
    @Override
    public void onAfterNewObject(InspectionOrder object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(InspectionOrder object) {
    }
    
    @Override
    public void onAfterUpdateObject(InspectionOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(InspectionOrder object) {
    }
    
    @Override
    public void onAfterDeleteObject(InspectionOrder object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(InspectionOrder object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
     */
    public TreeNode getOrgAssetTree() {
        return orgAssetTree;
    }

    public void setOrgAssetTree(TreeNode orgAssetTree) {
        this.orgAssetTree = orgAssetTree;
    }

    public TreeNode[] getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public List<TreeNode> getSelectedNodesList() {
        return selectedNodesList;
    }

    public void setSelectedNodesList(List<TreeNode> selectedNodesList) {
        this.selectedNodesList = selectedNodesList;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public InspectionService getInspectionService() {
        return inspectionService;
    }

    public void setInspectionService(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
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

    public UaaService getUuaService() {
        return uuaService;
    }

    public void setUuaService(UaaService uuaService) {
        this.uuaService = uuaService;
    }

    public List<InspectionOrderDetail> getOrderDetailItemList() {
        return orderDetailItemList;
    }

    public void setOrderDetailItemList(List<InspectionOrderDetail> orderDetailItemList) {
        this.orderDetailItemList = orderDetailItemList;
    }

    public boolean isAllPass() {
        return allPass;
    }

    public void setAllPass(boolean allPass) {
        this.allPass = allPass;
    }

}
