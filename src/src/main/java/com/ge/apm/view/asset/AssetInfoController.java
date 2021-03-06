package com.ge.apm.view.asset;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetDepreciationService;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.joda.time.DateTime;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class AssetInfoController extends JpaCRUDController<AssetInfo> {

    private static final long serialVersionUID = -1L;

    AssetInfoRepository dao = null;

    AssetFileAttachmentRepository attachDao = null;

    private UserAccountRepository userDao;

    private boolean resultStatus;

    private UserAccount clinicalOwner;
    private Supplier supplier;
    
    private UserAccount owner;
//    private List<UserAccount> ownerList;
    private List<OrgInfo> ownerOrgList;

    private List<AssetFileAttachment> attachements;

    @ManagedProperty("#{attachmentFileService}")
    private AttachmentFileService fileService;

    private UaaService uuaService;

    private OrgInfoRepository orgDao;

    private String operation;

    private AssetDepreciationService assetDepreciationService;

    Boolean terminate;

    protected void setSelectedByUrlParam(String encodeUrl, String paramName){
        setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeUrl, paramName)));
        
        owner = userDao.findById(selected.getAssetOwnerId());
        
        if (null != selected.getClinicalOwnerId()) {
            clinicalOwner = userDao.findById(selected.getClinicalOwnerId());
        }

        if (null != selected.getSupplierId()) {
            SupplierRepository supplierDao = WebUtil.getBean(SupplierRepository.class);
            supplier = supplierDao.findById(selected.getSupplierId());
        }
    }
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        assetDepreciationService = WebUtil.getBean(AssetDepreciationService.class);
        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
        this.filterBySite = true;
        if (!userContextService.hasRole("MultiHospital")) {
            this.filterByHospital = true;
        }

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");

        // String actionName = WebUtil.getRequestParameter("actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("View".equalsIgnoreCase(actionName)) {
            // setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            setSelectedByUrlParam(encodeStr, "selectedid");
            prepareView();
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            setSelectedByUrlParam(encodeStr, "selectedid");
            prepareEdit();
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            setSelectedByUrlParam(encodeStr, "selectedid");
            prepareDelete();
        }

//        ownerList = uuaService.getUserList(UserContextService.getCurrentUserAccount().getHospitalId());
        ownerOrgList = uuaService.getOrgListByHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }

        if (terminate) {
            this.searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, false));
        } else {
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        }
    }

    public void setFileService(AttachmentFileService fileService) {
        this.fileService = fileService;
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        selected = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public String getViewPage(String pageName, String actionName) {
        operation = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
        return operation;
    }

    public String getDetailPage() {
        operation = "Detail.xhtml?actionName=View&selectedid=" + selected.getId() + "&asset_name=" + selected.getName();
        return operation;
    }

    public String getDetailPage(String assetId, String assetName) {
        operation = "Detail.xhtml?actionName=View&selectedid=" + assetId + "&asset_name=" + assetName;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public String getEditPage(String assetId, String assetName) {
        operation = "Create.xhtml?actionName=Edit&selectedid=" + assetId + "&asset_name=" + assetName;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void assembleMaintainData() {
        operation = "/portal/wo/woCreate?assetId=" + selected.getId() + "&assetName=" + selected.getName()
                + "&assetOwnerId=" + selected.getAssetOwnerId() + "&assetOwnerName=" + selected.getAssetOwnerName();
    }

    public void assembleMaintainList() {
        operation = "/portal/wo/woList?assetId=" + selected.getId();
    }

    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if (isOK) {
            saveAttachements();
        }
    }

    public String getContractListLink() {
        return "/portal/asset/contract/List?assetId=" + selected.getId();
    }

    public String getContractAddLink() {
        return "/portal/asset/contract/List?assetId=" + selected.getId() + "&actionName=Create";
    }

    @Override
    public void onBeforeNewObject(AssetInfo object) {
        object.setSiteId(UserContextService.getSiteId());
        object.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
        object.setIsValid(true);
        object.setStatus(1);
    }

    @Override
    public void onBeforeSave(AssetInfo object) {
        OrgInfo org = orgDao.findById(object.getClinicalDeptId());
        object.setClinicalDeptName(org.getName());
        DateTime temp;
        if (null != object.getManufactDate()) {
            temp = new DateTime(object.getManufactDate());
            object.setManufactDate(temp.plusHours(12).toDate());
        }
        if (null != object.getPurchaseDate()) {
            temp = new DateTime(object.getPurchaseDate());
            object.setPurchaseDate(temp.plusHours(12).toDate());
        }
        if (null != object.getArriveDate()) {
            temp = new DateTime(object.getArriveDate());
            object.setArriveDate(temp.plusHours(12).toDate());
        }
        if (null != object.getInstallDate()) {
            temp = new DateTime(object.getInstallDate());
            object.setInstallDate(temp.plusHours(12).toDate());
        }
        if (null != object.getWarrantyDate()) {
            temp = new DateTime(object.getWarrantyDate());
            object.setWarrantyDate(temp.plusHours(12).toDate());
        }
        if (null != object.getTerminateDate()) {
            temp = new DateTime(object.getTerminateDate());
            object.setTerminateDate(temp.plusHours(12).toDate());
        }
    }

    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if (isOK) {
            saveAttachements();
        }
    }

    @Override
    public void onAfterDeleteObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    public String removeOne() {
        this.delete();
        if (resultStatus) {
            return "List?faces-redirect=true";
        } else {
            return "";
        }
    }

    @Transactional
    public String applyChange() {
        if (!isTimeValidate()) {
            return "";
        }
        this.save();
        assetDepreciationService.saveAssetDerpeciation(selected);
        if (resultStatus) {
            return getListPageLink();
        } else {
            return "";
        }
    }

    public String getListPageLink() {
        return "List?faces-redirect=true" + (this.isTerminate() ? "&terminate=true" : "");
    }

    public UserAccount getOwner() {
        return owner;
    }

    public List<OrgInfo> getOwnerOrgList() {
        return ownerOrgList;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public void onOwnerChange() {
        if (null != owner) {
            selected.setAssetOwnerId(owner.getId());
            selected.setAssetOwnerName(owner.getName());
            selected.setAssetOwnerTel(owner.getTelephone());
        }
    }

    public void onClinicalDeptChange() {
        this.clinicalOwner = null;
        selected.setClinicalOwnerId(null);
        selected.setClinicalOwnerName(null);
        selected.setClinicalOwnerTel(null);
    }

    public void onClinicalOwnerChange() {
        if (null != clinicalOwner) {
            selected.setClinicalOwnerId(clinicalOwner.getId());
            selected.setClinicalOwnerName(clinicalOwner.getName());
            selected.setClinicalOwnerTel(clinicalOwner.getTelephone());
        }
    }

    public void onSupplierChange() {
        if (null != supplier) {
            selected.setSupplierId(supplier.getId());
            selected.setVendor(supplier.getName());
        }
    }
    
    public void handleFileUpload(FileUploadEvent event) {

        String type = event.getComponent().getId();
        type = type.substring(type.indexOf("type") + 4);
        AssetFileAttachment attach = new AssetFileAttachment();
        String fileName = fileService.getFileName(event.getFile());
        attach.setName(fileName);
        attach.setFileType(type);
        attach.setSiteId(UserContextService.getCurrentUserAccount().getSiteId());
        attach.setAssetId(selected.getId());
        attach.setHospitalId(selected.getHospitalId());
        Integer uploadFileId = fileService.uploadFile(event.getFile());
        if (uploadFileId > 0) {
            attach.setFileId(uploadFileId);
            WebUtil.addSuccessMessage(fileName + WebUtil.getMessage("SuccessUploaded"));
            attachements.add(attach);
        }

    }

    public List<AssetFileAttachment> getAttachList(Integer assetid, String type) {
        if (attachements == null || attachements.isEmpty()) {
            List<SearchFilter> sfs = new ArrayList();
            //sfs.addAll(this.searchFilters);
            sfs.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetid));
            attachements = attachDao.findBySearchFilter(sfs);
        }

        List<AssetFileAttachment> result = new ArrayList();

        for (AssetFileAttachment item : attachements) {
            if (item.getFileType().equals(type)) {
                result.add(item);
            }
        }
        return result;
    }

    private void saveAttachements() {
        for (AssetFileAttachment item : attachements) {
            if (null == item.getId()) {
                item.setAssetId(selected.getId());
                attachDao.save(item);
            }
        }
        attachements.clear();
    }

    public void removeAttachment(Integer attachid) {
        List<AssetFileAttachment> tempList = new ArrayList();
        for (AssetFileAttachment item : attachements) {
            if (attachid.equals(item.getFileId())) {
                fileService.deleteAttachment(item.getFileId());
                if (item.getId() != null) {
                    attachDao.delete(item);
                }
            } else {
                tempList.add(item);
            }
        }
        attachements = tempList;
    }

    public OrgInfo getHospital(Integer id) {
        if (id == 0) {
            return orgDao.findById(UserContextService.getCurrentUserAccount().getHospitalId());
        } else {
            return orgDao.findById(id);
        }
    }

    public void onHospitalChange() {
        this.owner = null;
    }

    public List<OrgInfo> getClinicalDeptList() {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, selected.getHospitalId()));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    public List<UserAccount> getOwnerList() {

        Integer hospitalId;
        if (selected != null) {
            hospitalId = selected.getHospitalId();
        } else {
            hospitalId = UserContextService.getCurrentUserAccount().getHospitalId();
        }

        List<UserAccount> res = userDao.getAssetOnwers(UserContextService.getSiteId(), hospitalId);
        return res;
    }

    public List<UserAccount> getClinicalOwnerList() {
        List<SearchFilter> usersFilters = new ArrayList<>();
        if (null != selected.getClinicalDeptId()) {
            usersFilters.add(new SearchFilter("orgInfoId", SearchFilter.Operator.EQ, selected.getClinicalDeptId()));
            return userDao.findBySearchFilter(usersFilters);
        } else {
            return new ArrayList<UserAccount>();
        }
    }

    public boolean isTimeValidate() {
        String message = WebUtil.getMessage("shouldEarly"); //"{0} must before {1}!";
        AssetInfo input = this.selected;
//        Date todaydate = new Date();
        boolean isError = false;

        if (null != input.getManufactDate() && null != input.getPurchaseDate() && input.getManufactDate().after(input.getPurchaseDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("manufactDate"), WebUtil.getMessage("purchaseDate")));
        }

        if (null != input.getArriveDate() && null != input.getPurchaseDate() && input.getPurchaseDate().after(input.getArriveDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("purchaseDate"), WebUtil.getMessage("arriveDate")));
        }

        if (null != input.getArriveDate() && null != input.getInstallDate() && input.getArriveDate().after(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("arriveDate"), WebUtil.getMessage("installDate")));
        }
        if (null != input.getWarrantyDate() && null != input.getInstallDate() && input.getWarrantyDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("warrantyDate")));
        }
        if (null != input.getTerminateDate() && null != input.getInstallDate() && input.getTerminateDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("terminateDate")));
        }
        if (null != input.getLastPmDate() && null != input.getInstallDate() && input.getLastPmDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("lastPmDate")));
        }
        if (null != input.getLastMeteringDate() && null != input.getInstallDate() && input.getLastMeteringDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("lastMeteringDate")));
        }
        if (null != input.getLastQaDate() && null != input.getInstallDate() && input.getLastQaDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("lastQaDate")));
        }

//        if (null!=input.getLastPmDate() && input.getLastPmDate().after(todaydate)) {
//            isError = true;
//            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("lastPmDate"), WebUtil.getMessage("todayDate")));
//        }
//        if (null!=input.getLastMeteringDate() && input.getLastMeteringDate().after(todaydate)) {
//            isError = true;
//            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("lastMeteringDate"), WebUtil.getMessage("todayDate")));
//        }
//        if (null!=input.getLastQaDate() && input.getLastQaDate().after(todaydate)) {
//            isError = true;
//            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("lastQaDate"), WebUtil.getMessage("todayDate")));
//        }
        return !isError;
    }

    private String filterAssetStatus = null;

    public String getFilterAssetStatus() {
        return filterAssetStatus;
    }

    public void setFilterAssetStatus(String filterAssetStatus) {
        this.filterAssetStatus = filterAssetStatus;
    }

    public void setAssetStatusFilter() {
        if (filterAssetStatus == null && filterWarrantyDate == null) {
            return;
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (filterAssetStatus != null) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, filterAssetStatus));
        }
        if (filterWarrantyDate != null && "1".equals(filterWarrantyDate)) {
            Date warrantyDate = new Date();
            //2个月内，则是保修到期时间在2个月后时间之前所有
            Calendar c = Calendar.getInstance();
            c.setTime(warrantyDate);
            c.add(Calendar.MONTH, 2);
            varWarrantyDateTo = c.getTime();
            searchFilters.add(new SearchFilter("warrantyDate", SearchFilter.Operator.LTE, varWarrantyDateTo));
            filterWarrantyDate = null;
        }
    }

    private String filterWarrantyDate = null;

    public String getFilterWarrantyDate() {
        return filterWarrantyDate;
    }

    public void setFilterWarrantyDate(String filterWarrantyDate) {
        this.filterWarrantyDate = filterWarrantyDate;
    }

    private Date varWarrantyDateTo = null;
    private String warrantyFormatTime = null;

    public Date getVarWarrantyDateTo() {
        return varWarrantyDateTo;
    }

    public void setVarWarrantyDateTo(Date varWarrantyDateTo) {
        this.varWarrantyDateTo = varWarrantyDateTo;
    }

    public String getWarrantyFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varWarrantyDateTo == null) {
            return warrantyFormatTime;
        } else {
            return "~" + sdf.format(varWarrantyDateTo);
        }
    }

    public void setWarrantyFormatTime(String warrantyFormatTime) {
        this.warrantyFormatTime = warrantyFormatTime;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Boolean isTerminate() {
        if (null != selected) {
            return !selected.getIsValid();
        }
        return terminate;
    }

    public UserAccount getClinicalOwner() {
        return clinicalOwner;
    }

    public void setClinicalOwner(UserAccount clinicalOwner) {
        this.clinicalOwner = clinicalOwner;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
}
