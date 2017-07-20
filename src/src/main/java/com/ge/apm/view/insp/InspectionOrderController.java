package com.ge.apm.view.insp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class InspectionOrderController extends JpaCRUDController<InspectionOrder> {

    private static final long serialVersionUID = 1L;

    InspectionOrderRepository dao = null;

    private TreeNode orgAssetTree;
    List<Object[]> selectedNodesList;
    TreeNode[] selectedNodes;

    private TreeNode excuteItemTree;
    TreeNode[] excutedItemArray;

    InspectionService inspectionService;

    int period;

    UserAccount owner;
    private List<UserAccount> ownerList;
    private UaaService uuaService;

    OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);

    List<InspectionOrderDetail> orderDetailItemList;

    AttachmentFileService fileService;

    private String operation;

    Integer hospitalId;

    @Override
    protected void init() {
        dao = WebUtil.getBean(InspectionOrderRepository.class);
        fileService = WebUtil.getBean(AttachmentFileService.class);
        inspectionService = WebUtil.getBean(InspectionService.class);
        uuaService = WebUtil.getBean(UaaService.class);
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);

        this.filterBySite = true;
        if (!userContextService.hasRole("MultiHospital")) {
            this.filterByHospital = true;
        }
        
        hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        //String actionName = WebUtil.getRequestParameter("actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
                orgAssetTree = inspectionService.getPlanTree(1,hospitalId);
                owner = new UserAccount();
                ownerList = uuaService.getUserList(hospitalId);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("View".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            owner = inspectionService.getUserAccountById(selected.getOwnerId());
            orderDetailItemList = inspectionService.getDetailList(selected.getId());
            prepareView();
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            owner = inspectionService.getUserAccountById(selected.getOwnerId());
            orderDetailItemList = inspectionService.getDetailList(selected.getId());
            ownerList = uuaService.getUserList(hospitalId);
            prepareEdit();
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            owner = inspectionService.getUserAccountById(selected.getOwnerId());
            orderDetailItemList = inspectionService.getDetailList(selected.getId());
            prepareDelete();
        } else if ("Excute".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            owner = inspectionService.getUserAccountById(selected.getOwnerId());
            excuteItemTree = inspectionService.getInspectionOrderDetailAsTree(selected.getId(), true);
            this.selected.setEndTime(new Date());
            setTreeStatus(excuteItemTree);
        }

    }

    private void setTreeStatus(TreeNode node) {
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

    @Override
    protected InspectionOrderRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<InspectionOrder> loadData(PageRequest pageRequest) {
        this.selected = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, 1));
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<InspectionOrder> getItemList() {
        searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, 1));
        return dao.findBySearchFilter(searchFilters);
    }

    public void onUnselectTreeNode(NodeUnselectEvent event) {
        //for create page unselected asset
        selectedNodesList = getSelectedItem();
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        //for create page selected asset
        selectedNodesList = getSelectedItem();
    }

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
        if (null == selectedNodesList || selectedNodesList.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("noAssetSelected"));
            return "";
        }
        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());
        calender.add(Calendar.DAY_OF_MONTH, -1);
        if (selected.getStartTime() == null || selected.getStartTime().before(calender.getTime())) {
            WebUtil.addErrorMessage(MessageFormat.format(WebUtil.getMessage("shouldLate"), WebUtil.getMessage("cyclicStartDate"), WebUtil.getMessage("todayDate")));
            return "";
        }
        if (selected.getEndTime() == null || selected.getStartTime() == null || selected.getEndTime().before(selected.getStartTime())) {
            WebUtil.addErrorMessage(MessageFormat.format(WebUtil.getMessage("shouldLate"), WebUtil.getMessage("cyclicEndDate"), WebUtil.getMessage("cyclicStartDate")));
            return "";
        }
        boolean isSuccess = inspectionService.createOrder(selected, period, selectedNodesList);
        if (isSuccess) {
            return "InspOrderList?faces-redirect=true";
        } else {
            return "";
        }
    }

    public String updateOrder(){
        inspectionService.updateOrder(this.selected, new ArrayList<>());
        return "InspOrderList?faces-redirect=true";
    }

    @Override
    public void onBeforeNewObject(InspectionOrder object) {
        object.setSiteId(UserContextService.getSiteId());
        object.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
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

    public void getViewPage(String pageName, String actionName) {
        //return pageName + "?faces-redirect=true&actionName=" + actionName + "&selectedid=" + selected.getId();
        operation = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
    }

    public String removeOne() {
        if (inspectionService.deleteOrder(this.selected)) {
            return "InspOrderList?faces-redirect=true";
        } else {
            return "";
        }
    }

    public String excuteOrder() {
        List<InspectionOrderDetail> checkItemList = new ArrayList();
        Set<Integer> assetIdSet = new HashSet();
        for (TreeNode item : excutedItemArray) {
            if (item.getType().equals("checklist")) {
                InspectionOrderDetail checkItem = (InspectionOrderDetail) item.getData();
                checkItem.setIsPassed(true);
                checkItemList.add(checkItem);

                assetIdSet.add(checkItem.getAssetId());
            }
        }
        this.selected.setIsFinished(true);
        inspectionService.excuteOrder(this.selected, checkItemList, assetIdSet);
        return "InspOrderList?faces-redirect=true";
    }

    public void handleFileUpload(FileUploadEvent event) {
        Integer id = fileService.uploadFile(event.getFile());
        selected.setPaperUrl(id.toString());
    }

    public void removeAttach() {
        fileService.deleteAttachment(Integer.valueOf(selected.getPaperUrl()));
        selected.setPaperUrl(null);

    }

    public boolean isExcuteable() {
        //return selected != null && !selected.getIsFinished() && (selected.getStartTime().before(new Date()));
        return selected != null && !selected.getIsFinished();
    }

    public void onHospitalChange() {
        orgAssetTree = inspectionService.getPlanTree(1,hospitalId);
        owner = new UserAccount();
        ownerList = uuaService.getUserList(hospitalId);
        
        this.selected.setOwnerId(0);
        this.selected.setOwnerName(null);
        this.selected.setOwnerOrgId(0);
        this.selected.setOwnerOrgName(null);
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

    private String filterPlanTime = null;
    private String filterIsFinished = null;

    public String getFilterPlanTime() {
        return filterPlanTime;
    }

    public void setFilterPlanTime(String filterPlanTime) {
        this.filterPlanTime = filterPlanTime;
    }

    public String getFilterIsFinished() {
        return filterIsFinished;
    }

    public void setFilterIsFinished(String filterIsFinished) {
        this.filterIsFinished = filterIsFinished;
    }

    public void setPlanTimeFilter() {
        if (filterPlanTime == null || !"1".equals(filterPlanTime)) {
            return;
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        //2个月内，则是在2个月后时间之前所有
        Date startTime = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(startTime);
        c.add(Calendar.MONTH, 2);
        varPlanTimeTo = c.getTime();
        searchFilters.add(new SearchFilter("planTime", SearchFilter.Operator.LTE, varPlanTimeTo));
        searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.EQ, false));
        filterPlanTime = null;
        filterIsFinished = "false";
    }

    private Date varPlanTimeTo = null;
    private String planFormatTime = null;

    public Date getVarPlanTimeTo() {
        return varPlanTimeTo;
    }

    public void setVarPlanTimeTo(Date varPlanTimeTo) {
        this.varPlanTimeTo = varPlanTimeTo;
    }

    public String getPlanFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varPlanTimeTo == null) {
            return planFormatTime;
        } else {
            return "~" + sdf.format(varPlanTimeTo);
        }
    }

    public void setPlanFormatTime(String planFormatTime) {
        this.planFormatTime = planFormatTime;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

}
