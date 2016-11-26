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
import com.ge.apm.domain.InspectionChecklist;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InspectionOrderController extends JpaCRUDController<InspectionOrder> {

    InspectionOrderRepository dao = null;

    private TreeNode orgAssetTree;
    List<Object[]> selectedNodesList;
    TreeNode[] selectedNodes;

    private TreeNode excuteItemTree;
    TreeNode[] excutedItemArray;

    InspectionService inspectionService;

    int assetGrossCount;
    int period;

    UserAccount owner;
    private List<UserAccount> ownerList;
    private UaaService uuaService;

    OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);

    List<InspectionOrderDetail> orderDetailItemList;

    AttachmentFileService fileService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(InspectionOrderRepository.class);
        fileService = WebUtil.getBean(AttachmentFileService.class);
        inspectionService = WebUtil.getBean(InspectionService.class);
        uuaService = WebUtil.getBean(UaaService.class);
        orgAssetTree = inspectionService.getPlanTree(1);
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
        } else if ("Excute".equalsIgnoreCase(actionName)) {
            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            owner = inspectionService.getUserAccountById(selected.getOwnerId());
            excuteItemTree = inspectionService.getInspectionOrderDetailAsTree(selected.getId(), true);
            setTreeStatus(excuteItemTree);
        }

        ownerList = uuaService.getUserList(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    private void setTreeStatus(TreeNode node) {
        int sum = 0;
        if ("checklist".equals(node.getType())) {
            boolean status = ((InspectionOrderDetail) node.getData()).getIsPassed();
            if (status) {
                node.setSelected(true);
                node.setSelectable(false);
            }
        }
        for (TreeNode item : node.getChildren()) {
            setTreeStatus(item);
        }
    }

    private int getTreeCount(TreeNode node) {
        int sum = 0;
        if ("checklist".equals(node.getType())) {
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
        return dao.find();
    }

    public void onUnselectTreeNode(NodeUnselectEvent event) {
        //for create page unselected asset
        selectedNodesList = getSelectedItem();
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        //for create page selected asset
        selectedNodesList = getSelectedItem();
    }

//    public void removeSelectedAsset(TreeNode node) {
//        selectedNodesList.remove(node);
//        node.setSelected(false);
//    }

    public List<Object[]> getSelectedItem() {
        //for create page datalist
        List<Object[]> tempList = new ArrayList();
        for (TreeNode item : selectedNodes) {
            if (item.getType().equals("checklist")) {
                Object[] content = new Object[3];
                content[0] = (InspectionChecklist) item.getData();
                content[1] = (AssetInfo) item.getParent().getData();
                content[2] = (OrgInfo) item.getParent().getParent().getData();
                tempList.add(content);
            }
        }
        return tempList;
    }
    
    public String createOrder() {
        //for create page submit
        if (selectedNodesList.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("noAssetSelected"));
            return "";
        }
        boolean isSuccess = inspectionService.createOrder(selected, period, selectedNodesList);
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

//    public void selectAllPass() {
//        for (InspectionOrderDetail item : orderDetailItemList) {
//            item.setIsPassed(allPass);
//        }
//    }

    public String excuteOrder() {
        List<InspectionOrderDetail> checkItemList = new ArrayList();
        int excutedCount = 0;
        for (TreeNode item : excutedItemArray) {
            if (item.getType().equals("checklist")) {
                InspectionOrderDetail checkItem = (InspectionOrderDetail) item.getData();
                checkItem.setIsPassed(true);
                checkItemList.add(checkItem);
                excutedCount++;
            }
        }
        this.selected.setIsFinished(getTreeCount(excuteItemTree) == excutedCount);
        inspectionService.excuteOrder(this.selected, checkItemList);
        return "InspOrderList";
    }

//    public String updateOrder() {
//        inspectionService.updateOrder(this.selected, orderDetailItemList);
//        return "InspOrderList";
//    }

    public void handleFileUpload(FileUploadEvent event) {
        Integer id = fileService.uploadFile(event.getFile());
        selected.setPaperUrl(id.toString());
    }

    public void removeAttach() {
        fileService.deleteAttachment(Integer.valueOf(selected.getPaperUrl()));
        selected.setPaperUrl(null);

    }

    //getter and setter
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

    public List<Object[]> getSelectedNodesList() {
        return selectedNodesList;
    }

    public void setSelectedNodesList(List<Object[]> selectedNodesList) {
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

    public TreeNode getExcuteItemTree() {
        return excuteItemTree;
    }

    public void setExcuteItemTree(TreeNode excuteItemTree) {
        this.excuteItemTree = excuteItemTree;
    }

    public TreeNode[] getExcutedItemArray() {
        return excutedItemArray;
    }

    public void setExcutedItemArray(TreeNode[] excutedItemArray) {
        this.excutedItemArray = excutedItemArray;
    }

}
