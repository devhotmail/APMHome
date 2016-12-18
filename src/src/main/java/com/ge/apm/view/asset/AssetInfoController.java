package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;
import webapp.framework.web.WebUtil;
import webapp.framework.dao.SearchFilter;

@ManagedBean
@ViewScoped
public class AssetInfoController extends JpaCRUDController<AssetInfo> {

    AssetInfoRepository dao = null;

    AssetFileAttachmentRepository attachDao = null;

    private boolean resultStatus;

    private UserAccount owner;
    private List<UserAccount> ownerList;

    private List<AssetFileAttachment> attachements;

    @ManagedProperty("#{attachmentFileService}")
    private AttachmentFileService fileService;

    private UaaService uuaService;

    private OrgInfoRepository orgDao;
    
    private String operation;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        this.filterByHospital = true;
        this.setHospitalFilter();
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr,"actionName");
        
       // String actionName = WebUtil.getRequestParameter("actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("View".equalsIgnoreCase(actionName)) {
           // setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
        	setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr,"selectedid")));
            owner = userDao.findById(selected.getAssetOwnerId());
            prepareView();
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
        	setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr,"selectedid")));
            owner = userDao.findById(selected.getAssetOwnerId());
            prepareEdit();
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
        	setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr,"selectedid")));
            owner = userDao.findById(selected.getAssetOwnerId());
            prepareDelete();
        }

        ownerList = uuaService.getUserList(UserContextService.getCurrentUserAccount().getHospitalId());
        if(searchFilters==null) searchFilters = new ArrayList<SearchFilter>();
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
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
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public String getViewPage(String pageName, String actionName) {
    	operation  = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
    	return operation;
    }

    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if (isOK) {
            saveAttachements();
        }
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

    public String applyChange() {
        if (!isTimeValidate()){
            return "";
        }
        this.save();
        if (resultStatus) {
            return "List?faces-redirect=true";
        } else {
            return "";
        }
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

    public void onOwnerChange() {
        if (null != owner) {
            selected.setAssetOwnerId(owner.getId());
            selected.setAssetOwnerName(owner.getName());
            selected.setAssetOwnerTel(owner.getTelephone());
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
                if(item.getId() != null){
                    attachDao.delete(item);
                }
            }
            else {
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

    public List<OrgInfo> getClinicalList() {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        UserContextService.setHospitalFilter(OrgInfoFilters);
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    public boolean isTimeValidate() {
        String message = WebUtil.getMessage("shouldEarly"); //"{0} must before {1}!";
        AssetInfo input = this.selected;
        Date todaydate = new Date();
        boolean isError = false;
        
        if (null!=input.getManufactDate() && null!=input.getPurchaseDate() && input.getManufactDate().after(input.getPurchaseDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("manufactDate"), WebUtil.getMessage("purchaseDate")));
        }
        
        if (null!=input.getArriveDate() && null!=input.getPurchaseDate() && input.getPurchaseDate().after(input.getArriveDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("purchaseDate"), WebUtil.getMessage("arriveDate")));
        }
        
        if (null!=input.getArriveDate() && null!=input.getInstallDate() && input.getArriveDate().after(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("arriveDate"), WebUtil.getMessage("installDate")));
        }
        if (null!=input.getArriveDate() && input.getArriveDate().after(todaydate)) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("todayDate")));
        }
        if (null!=input.getWarrantyDate() && input.getWarrantyDate().before(todaydate)) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(WebUtil.getMessage("shouldLate"), WebUtil.getMessage("warrantyDate"), WebUtil.getMessage("todayDate")));
        }
        if (null!=input.getLastPmDate() && input.getLastPmDate().after(todaydate)) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("lastPmDate"), WebUtil.getMessage("todayDate")));
        }
        if (null!=input.getLastMeteringDate() && input.getLastMeteringDate().after(todaydate)) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("lastMeteringDate"), WebUtil.getMessage("todayDate")));
        }
        if (null!=input.getLastQaDate() && input.getLastQaDate().after(todaydate)) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("lastQaDate"), WebUtil.getMessage("todayDate")));
        }
        
        
        return !isError;
    }

    private String filterAssetStatus = null;
    public String getFilterAssetStatus() {
        return filterAssetStatus;
    }
    public void setFilterAssetStatus(String filterAssetStatus) {
        this.filterAssetStatus = filterAssetStatus;
    }

    public void setAssetStatusFilter(){
        if(filterAssetStatus==null && filterWarrantyDate == null)  return;
        if(searchFilters==null) searchFilters = new ArrayList<SearchFilter>();
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
        if (varWarrantyDateTo==null){
            return warrantyFormatTime;
        } else {
            return "~"+sdf.format(varWarrantyDateTo);
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
    

}
