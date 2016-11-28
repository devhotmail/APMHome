package com.ge.apm.view.insp;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.InspectionChecklistRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InspectionChecklist;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InspectionChecklistController extends JpaCRUDController<InspectionChecklist> {

    InspectionChecklistRepository dao = null;

    AssetInfo selectedAsset;
    Integer checklistType;

    TreeNode selectedNode;

    List<InspectionChecklist> checklistitemList;

    private TreeNode orgAssetTree;

    boolean isOrderChange = false;
    private String type;

    @Override
    protected void init() {
        dao = WebUtil.getBean(InspectionChecklistRepository.class);
        this.filterBySite = true;
        this.setSiteFilter();
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        orgAssetTree = uaaService.getOrgAssetTree(UserContextService.getCurrentUserAccount().getHospitalId());
        checklistitemList = new ArrayList();
        type = WebUtil.getRequestParameter("type");
        checklistType = (null == type ? null : Integer.valueOf(type));
        if (null == type || type.isEmpty()) {
            setCheckType(orgAssetTree);
        }else{
            checklistType = Integer.valueOf(type);
        }

    }

    private void setCheckType(TreeNode node) {

        Object nodeData = node.getData();
        if (nodeData instanceof AssetInfo) {
            new DefaultTreeNode("checklist", new Object[]{1, WebUtil.getFieldValueMessage("checklistType", "1")}, node);
            new DefaultTreeNode("checklist", new Object[]{2, WebUtil.getFieldValueMessage("checklistType", "2")}, node);
            new DefaultTreeNode("checklist", new Object[]{3, WebUtil.getFieldValueMessage("checklistType", "3")}, node);
        } else {
            for (TreeNode item : node.getChildren()) {
                setCheckType(item);
            }
        }
    }

    @Override
    protected InspectionChecklistRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<InspectionChecklist> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            this.setSiteFilter();
            if (null != selectedAsset) {
                this.searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, selectedAsset.getId()));
            }
            this.searchFilters.add(new SearchFilter("checklistType", SearchFilter.Operator.EQ, checklistType));

            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<InspectionChecklist> getItemList() {
        if (null != selectedAsset && null != checklistType) {
            this.searchFilters.clear();
            this.setSiteFilter();
            this.searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, selectedAsset.getId()));
            this.searchFilters.add(new SearchFilter("checklistType", SearchFilter.Operator.EQ, checklistType));
            return dao.findBySearchFilter(searchFilters);
        } else {
            return new ArrayList<InspectionChecklist>();
        }
    }

    @Override
    public void onBeforeNewObject(InspectionChecklist object) {
        this.selected.setAssetId(selectedAsset.getId());
        this.selected.setSiteId(UserContextService.getSiteId());
        this.selected.setChecklistType(checklistType);
        this.selected.setDisplayOrder(this.checklistitemList.size());
    }

    @Override
    public void onAfterNewObject(InspectionChecklist object, boolean isOK) {
        if (isOK) {
            checklistitemList.add(object);
        }
    }

    @Override
    public void onAfterDeleteObject(InspectionChecklist object, boolean isOK) {
        if (isOK) {
            checklistitemList.remove(object);
        }
    }

    public void onExpandTreeNode(NodeExpandEvent event) {
        event.getTreeNode().setExpanded(true);
    }

    public void onCollapseTreeNode(NodeCollapseEvent event) {
        event.getTreeNode().setExpanded(false);
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        node.setExpanded(true);

        if (node.getType().equals("checklist")) {
            selectedAsset = (AssetInfo) node.getParent().getData();
            Object[] data = (Object[]) node.getData();
            checklistType = (Integer) data[0];
            checklistitemList = this.getItemList();
        } else if (node.getType().equals("asset") && null!=type) {
            checklistType = Integer.valueOf(type);
            selectedAsset = (AssetInfo) node.getData();
            checklistitemList = this.getItemList();
        } else {
            selected = null;
            checklistType = 0;
            selectedAsset = null;
            checklistitemList.clear();
        }

    }

    public void saveOrder() {
        for (int i = 0; i < checklistitemList.size(); i++) {
            InspectionChecklist item = checklistitemList.get(i);
            if (item.getDisplayOrder() != i) {
                item.setDisplayOrder(i);
                dao.save(item);
            }
        }
        this.isOrderChange = false;
    }

    public void onRowReorder(ReorderEvent event) {
        this.isOrderChange = true;
    }

    public void onSelect(SelectEvent event) {
        this.selected = (InspectionChecklist) event.getObject();
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public Integer getChecklistType() {
        return checklistType;
    }

    public void setChecklistType(Integer checklistType) {
        this.checklistType = checklistType;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void setOrgAssetTree(TreeNode orgAssetTree) {
        this.orgAssetTree = orgAssetTree;
    }

    public TreeNode getOrgAssetTree() {
        return orgAssetTree;
    }

    public List<InspectionChecklist> getChecklistitemList() {
        return checklistitemList;
    }

    public void setChecklistitemList(List<InspectionChecklist> checklistitemList) {
        this.checklistitemList = checklistitemList;
    }

    public boolean isIsOrderChange() {
        return isOrderChange;
    }

    public void setIsOrderChange(boolean isOrderChange) {
        this.isOrderChange = isOrderChange;
    }

}
