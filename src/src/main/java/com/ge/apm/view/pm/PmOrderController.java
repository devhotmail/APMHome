package com.ge.apm.view.pm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.event.FileUploadEvent;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.FileUploadedRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.FileUploaded;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PmOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class PmOrderController extends JpaCRUDController<PmOrder> {

	private static final long serialVersionUID = 1L;

	private PmOrderRepository dao = null;
    
    private UserAccount currentUser;
    
    private List<FileUploaded> attachements ;
    
    private AttachmentFileService fileService;
    
    private UserAccount owner;
    
    private List<UserAccount> ownerList;
    
    private UserAccountRepository userDao ;
    
    private UaaService uuaService;
    
    private FileUploadedRepository fileUploadedRepository;
    
    @Override
    protected void init() {
		this.filterByHospital = true;
		this.filterBySite = true;
		dao = WebUtil.getBean(PmOrderRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		fileService = WebUtil.getBean(AttachmentFileService.class);
		userDao = WebUtil.getBean(UserAccountRepository.class);
		fileUploadedRepository = WebUtil.getBean(FileUploadedRepository.class);
		uuaService = (UaaService) WebUtil.getBean(UaaService.class);
		attachements = new ArrayList<FileUploaded>();
        ownerList =uuaService.getUsersWithAssetHeadOrStaffRole(currentUser.getHospitalId());
    }

    @Override
    protected PmOrderRepository getDAO() {
        return dao;
    }

    @Override
    public void onBeforeSave(PmOrder po) {
    	po.setSiteId(this.currentUser.getSiteId());
    	po.setHospitalId(this.currentUser.getHospitalId());
    	po.setCreatorId(this.currentUser.getId());
    	po.setCreatorName(this.currentUser.getName());
    	po.setCreateTime(TimeUtil.timeNowInDefaultTimeZone().toDate());
    }
   
    @Override
    public void onBeforeNewObject(PmOrder object){
    	this.owner = null;
    	super.onBeforeNewObject(object);
    }
    
    @Override
    public void onAfterNewObject(PmOrder object, boolean isOK) {
    	if(isOK){
    		this.selected = null;
    	}
    }
    
    public void onCancel(){
    	this.selected = null;
    	this.onBeforeNewObject(null);
    }
    
    public void onBeforeUpdateObject(PmOrder object)
    {
    	if(this.attachements.isEmpty()){
    		object.setFileId(null);
		}
    }
    
   @Override
    public void onAfterUpdateObject(PmOrder object, boolean isOK) {
    	if(isOK){
    		this.selected = null;
    	}
    }
   
    @Override
    public void prepareEdit(){
    	attachements.clear();
    	if(selected.getOwnerId() != null){
    		owner = userDao.findById(selected.getOwnerId());
    	}
    	if(selected.getFileId() != null){
    		FileUploaded fu =  fileUploadedRepository.findById(this.selected.getFileId());
    		if(fu != null){
    			attachements.add(fu);
    		}
    	}
    	super.prepareEdit();
    }
    
    @Override
	public void onAfterDeleteObject(PmOrder object, boolean isOK) {
    	if(isOK){
    		this.selected = null;
    		this.owner = null;
    	}
	}
    
    @Override
	public void prepareCreate() throws InstantiationException, IllegalAccessException {
    	attachements.clear();
    	super.prepareCreate();
	}
    
    @Override
    public void onServerEvent(String eventName, Object eventObject){
        AssetInfo asset = (AssetInfo) eventObject;
        if(asset==null) return;
        
        if(this.selected==null) return;
        this.selected.setAssetId(asset.getId());
        this.selected.setAssetName(asset.getName());
        this.selected.setOwnerId(asset.getAssetOwnerId());
        this.selected.setOwnerName(asset.getAssetOwnerName());
    	this.selected.setOwnerOrgId(asset.getAssetOwnerId());
        OrgInfoRepository orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        OrgInfo orgInfo = orgInfoRepository.findById(asset.getAssetOwnerId());
        if(orgInfo != null){
        	this.selected.setOwnerOrgName(orgInfo.getName());
        }
    }

    public void removeAttachment(Integer fileId) {
    	if(fileId == null || fileId <= 0){
    		return;
    	}
    	boolean isFindFile = false;
        for (FileUploaded item : attachements) {
            if (fileId.equals(item.getId())) {
            	isFindFile = true;
                fileService.deleteAttachment(item.getId());
            }
        }
        if(isFindFile){
        attachements.clear();
        }
    }
    
    @Transactional
    public void handleFileUpload(FileUploadEvent event) {
    	FileUploaded attach = new FileUploaded();
        String fileName = fileService.getFileName(event.getFile());
        attach.setFileName(fileName);
        Integer uploadFileId = fileService.uploadFile(event.getFile());
        if(uploadFileId>0){
        	attach.setId(uploadFileId);
            WebUtil.addSuccessMessage(String.format(WebUtil.getMessage("is uploaded."), fileName));
            attachements.add(attach);
            this.selected.setFileId(uploadFileId);
        }
    }
    
    public void onOwnerChange() {
        if (null != owner) {
            selected.setOwnerId(owner.getId());
            selected.setOwnerName(owner.getName());
            selected.setOwnerOrgId(owner.getOrgInfoId());
            OrgInfoRepository orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
            OrgInfo orgInfo = orgInfoRepository.findById(owner.getOrgInfoId());
            if(orgInfo != null){
            	this.selected.setOwnerOrgName(orgInfo.getName());
            }
        }
    }
    
    public boolean isHaveFile(PmOrder pmOrder){
    	if(pmOrder == null || pmOrder.getId() == null){
    		return false;
    	}
    	PmOrder pmOrderFromDB = dao.findById(pmOrder.getId());
		if(pmOrderFromDB.getFileId() == null || pmOrderFromDB.getFileId() <= 0){
			return false;
		}
		return true;
    }
    
    public void applyChange() {
        if (!isTimeValidate()){
        	return;
        }
        if (selected.getAssetName() == null || selected.getAssetName().trim().equals("") ) {
            WebUtil.addErrorMessage(WebUtil.getMessage("noAssetSelected"));
            return ;
        }
    	if(selected.getIsFinished()){
    		if(selected.getStartTime() == null){
    			 WebUtil.addErrorMessage(String.format(WebUtil.getMessage("WhenIsFinished"), WebUtil.getMessage("startTime")));
    			 selected.setIsFinished(false);
    			 return;
    		}
    		if(selected.getEndTime() == null){
    			 WebUtil.addErrorMessage(String.format(WebUtil.getMessage("WhenIsFinished"), WebUtil.getMessage("endTime")));
    			 selected.setIsFinished(false);
    			 return;
    		}
    	}
        this.save();
    }
    
    public boolean isTimeValidate() {
        String message =  WebUtil.getMessage("shouldEarly");
        PmOrder input = this.selected;
       // Date todaydate = new Date();
        boolean isError = false;
        
        if (null!=input.getStartTime() && null!=input.getEndTime() && input.getEndTime().before(input.getStartTime())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("startTime"), WebUtil.getMessage("endTime")));
        }
        
        if (null!=input.getNextTime() && null!=input.getStartTime() && input.getStartTime().after(input.getNextTime())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("startTime"), WebUtil.getMessage("nextTime")));
        }
//        if(null != input.getNextTime() && todaydate.after(input.getNextTime())){
//        	isError = true;
//        	 WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("todayDate"), WebUtil.getMessage("nextTime")));
//        }
        return !isError;
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

	public List<FileUploaded> getAttachements() {
		return attachements;
	}

	public void setAttachements(List<FileUploaded> attachements) {
		this.attachements = attachements;
	}
    
        private String filterStartTime = null;
        private String filterIsFinished = null;

        public String getFilterStartTime() {
            return filterStartTime;
        }

        public void setFilterStartTime(String filterStartTime) {
            this.filterStartTime = filterStartTime;
        }
        
        public String getFilterIsFinished() {
            return filterIsFinished;
        }

        public void setFilterIsFinished(String filterIsFinished) {
            this.filterIsFinished = filterIsFinished;
        }

        public void setStartTimeFilter(){
            if(filterStartTime==null || !"1".equals(filterStartTime))  return;
            if(searchFilters==null) searchFilters = new ArrayList<SearchFilter>();
            Date startTime = new Date();
            //一周内，则是在一周后时间之前所有
            Calendar c = Calendar.getInstance();
            c.setTime(startTime);
            c.add(Calendar.DAY_OF_WEEK, 7);
            varStartTimeTo = c.getTime();
            searchFilters.add(new SearchFilter("startTime", SearchFilter.Operator.LTE, varStartTimeTo));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.EQ, false));
            filterStartTime = null;
            filterIsFinished = "false";
        }
        
        private Date varStartTimeTo = null;
        private String startFormatTime = null;

        public Date getVarStartTimeTo() {
            return varStartTimeTo;
        }

        public void setVarStartTimeTo(Date varStartTimeTo) {
            this.varStartTimeTo = varStartTimeTo;
        }

        public String getStartFormatTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            if (varStartTimeTo==null){
                return startFormatTime;
            } else {
                return "~"+sdf.format(varStartTimeTo);
            } 
        }

        public void setStartFormatTime(String startFormatTime) {
            this.startFormatTime = startFormatTime;
        }

}