package com.ge.apm.view.pm;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.FileUploadedRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.FileUploaded;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PmOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.asset.AssetInfoController;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class PmOrderController extends JpaCRUDController<PmOrder> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PmOrderRepository dao = null;
    
    UserAccount currentUser;
    
    private List<AssetFileAttachment> attachements ;
    
    //AssetFileAttachmentRepository attachDao = null;

    //@ManagedProperty("{attachmentFileService}")
    private AttachmentFileService fileService;
    
    private UserAccount owner;
    
    private List<UserAccount> ownerList;
    
    UserAccountRepository userDao ;
    
    private UaaService uuaService;
    
    FileUploadedRepository fileUploadDao;
    
    @Override
    protected void init() {
		this.filterByHospital = true;
		this.filterBySite = true;
		dao = WebUtil.getBean(PmOrderRepository.class);
//		attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		fileService = WebUtil.getBean(AttachmentFileService.class);
		userDao = WebUtil.getBean(UserAccountRepository.class);
		fileUploadDao = WebUtil.getBean(FileUploadedRepository.class);
		uuaService = (UaaService) WebUtil.getBean(UaaService.class);
		attachements = new ArrayList<AssetFileAttachment>();
//        String actionName = WebUtil.getRequestParameter("actionName");
//        if ("Create".equalsIgnoreCase(actionName)) {
//            try {
//                prepareCreate();
//            } catch (InstantiationException | IllegalAccessException ex) {
//                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else if ("View".equalsIgnoreCase(actionName)) {
//            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
//            owner = userDao.findById(selected.getOwnerId());
//            prepareView();
//        } else if ("Edit".equalsIgnoreCase(actionName)) {
//            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
//            owner = userDao.findById(selected.getOwnerId());
//            prepareEdit();
//        } else if ("Delete".equalsIgnoreCase(actionName)) {
//            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
//            owner = userDao.findById(selected.getOwnerId());
//            prepareDelete();
//        }

        ownerList = uuaService.getUserList(currentUser.getHospitalId());
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
    
    @Override
    public void onAfterUpdateObject(PmOrder object, boolean isOK) {
    	if(isOK){
    		this.selected = null;
    	}
    }
   
    @Override
    public void prepareEdit(){
    	System.out.println("1prepareEdit ......id is "+selected);
    	owner = userDao.findById(selected.getOwnerId());
    	System.out.println("2prepareEdit ......id is "+selected);
    	super.prepareEdit();
    	System.out.println("3prepareEdit ......id is "+selected);
    }
    
    @Override
	public void onAfterDeleteObject(PmOrder object, boolean isOK) {
    	if(isOK){
    		this.selected = null;
    		this.owner = null;
    	}
	}
    
/*    @Override
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
    }*/
    
    // file operate
/*    public List<AssetFileAttachment> getReportList(Integer assetid, String type) {
        if (attachements == null || attachements.isEmpty()) {
            List<SearchFilter> sfs = new ArrayList<SearchFilter>();
            UserContextService.setSiteFilter(sfs);
            sfs.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetid));
            attachements = attachDao.findBySearchFilter(sfs);
        }

        List<AssetFileAttachment> result = new ArrayList<AssetFileAttachment>();

        for (AssetFileAttachment item : attachements) {
            if (item.getFileType().equals(type)) {
                result.add(item);
            }
        }
        return result;
    }*/
    
    public List<AssetFileAttachment> getReportList(Integer pmOrderId) {
//    	List<AssetFileAttachment> result = new ArrayList<AssetFileAttachment>();
    	if(pmOrderId != null && pmOrderId > 0){
    		PmOrder pmOrder = dao.findById(pmOrderId);
    		if(pmOrder.getFileId() == null || pmOrder.getFileId() <= 0){
    			return attachements;
    		}
    		FileUploaded fu =  fileUploadDao.findById(pmOrder.getFileId());
    		if(fu != null){
    			AssetFileAttachment aft = new AssetFileAttachment();
    			aft.setFileId(fu.getId());
    			aft.setName(fu.getFileName());
    			attachements.add(aft);
    		}
    		return attachements;
    	}
    	return attachements;
    }

/*    public void saveAttachements() {
        for (AssetFileAttachment item : attachements) {
            if (null == item.getId()) {
                attachDao.save(item);
            }
        }
        attachements.clear();
    }*/

    public void removeAttachment(Integer fileId) {
    	if(fileId == null || fileId <= 0){
    		return;
    	}
        for (AssetFileAttachment item : attachements) {
            if (fileId.equals(item.getFileId())) {
 //               fileService.deleteAttachment(item.getFileId());
                attachements.remove(item);
                break;
            }
        }
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        AssetFileAttachment attach = new AssetFileAttachment();
        String fileName = fileService.getFileName(event.getFile());
        attach.setName(fileName);
        Integer uploadFileId = fileService.uploadFile(event.getFile());
        if(uploadFileId>0){
        	attach.setId(uploadFileId);
            WebUtil.addSuccessMessage("Succesful", fileName + " is uploaded.");
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
    

}