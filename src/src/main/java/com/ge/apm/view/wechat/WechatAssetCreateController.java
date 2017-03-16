/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wechat;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AttachmentFileService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WechatAssetCreateController extends JpaCRUDController<AssetInfo> {

    AssetInfoRepository dao = null;

    private OrgInfoRepository orgDao;

    private UserAccountRepository userDao;

    private UserAccount assetOwner;
    private UserAccount clinicalOwner;

    private boolean resultStatus = false;

    @ManagedProperty("#{attachmentFileService}")
    private AttachmentFileService fileService;

    private String uploaderFiles;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);

        String action = WebUtil.getRequestParameter("action");
        if ("Edit".equals(action)) {
            this.prepareEdit();
            String id = WebUtil.getRequestParameter("assetid");
            selected = dao.findById(Integer.parseInt(id));
            if ( selected.getAssetOwnerId() > 0) {
                this.assetOwner = userDao.findById(selected.getAssetOwnerId());
            }
//            if (null != selected.getClinicalOwnerId() && selected.getClinicalOwnerId() > 0) {
//                this.clinicalOwner = userDao.findById(selected.getClinicalOwnerId());
//            }
        } else {

            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(WechatAssetCreateController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void onBeforeNewObject(AssetInfo object) {
        String qrCode = WebUtil.getRequestParameter("qrCode");
        if (null != qrCode && !qrCode.isEmpty()) {
            object.setQrCode(qrCode);
        }
        object.setSiteId(UserContextService.getSiteId());
        object.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
        object.setIsValid(true);
        object.setStatus(1);
    }

    public String getContextPath() {
        String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        return path;
    }

    public void onContinueAdd() {
        this.resultStatus = false;
        try {
            prepareCreate();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(WechatAssetCreateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onOwnerChange() {

    }

    public void onHospitalChange() {
        List<UserAccount> ownerList = getOwnerList();
        if (!ownerList.contains(this.assetOwner)) {
            this.assetOwner = null;
        }
        this.selected.setClinicalDeptId(null);
        this.onClinicalDeptChange();
    }

    public void onClinicalDeptChange() {
        this.clinicalOwner = null;
//        selected.setClinicalOwnerId(null);
//        selected.setClinicalOwnerName(null);
//        selected.setClinicalOwnerTel(null);
    }

    public List<OrgInfo> getClinicalDeptList() {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, selected.getHospitalId()));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    public List<UserAccount> getClinicalOwnerList() {
        List<SearchFilter> usersFilters = new ArrayList<>();
        if (null != selected.getClinicalDeptId()) {
            usersFilters.add(new SearchFilter("orgInfoId", SearchFilter.Operator.EQ, selected.getClinicalDeptId()));
            return userDao.findBySearchFilter(usersFilters);
        } else {
            return new ArrayList<>();
        }
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

    @Override
    public void onBeforeSave(AssetInfo object) {
        OrgInfo org = orgDao.findById(object.getClinicalDeptId());
//        object.setClinicalOwnerId(clinicalOwner.getId());
        object.setClinicalDeptName(org.getName());
        object.setClinicalDeptId(org.getId());
//        object.setClinicalOwnerTel(clinicalOwner.getTelephone());
//        object.setClinicalOwnerName(clinicalOwner.getName());
        object.setAssetOwnerName(this.assetOwner.getName());
        object.setAssetOwnerId(assetOwner.getId());

    }

    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
        onAfterUpdateObject(object, isOK);
    }

    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if (isOK) {
            if (null != uploaderFiles || uploaderFiles.length() > 0) {
                saveAttachement(uploaderFiles);
            }
        }
    }

    public Boolean isCreateAction() {
        return "Create".equalsIgnoreCase(this.crudActionName);
    }

    private void saveAttachement(String uploaderFiles) {
        JSONObject data = new JSONObject(uploaderFiles);
        JSONArray fileList = data.getJSONArray("pics");
        for (int i = 0; i < fileList.length(); i++) {
            JSONObject item = (JSONObject) fileList.getJSONObject(i);
            String fileName = item.getString("name");

            AssetFileAttachment attach = new AssetFileAttachment();
            attach.setName(fileName);
            attach.setFileType("1");
            attach.setSiteId(UserContextService.getCurrentUserAccount().getSiteId());
            attach.setAssetId(selected.getId());
            attach.setHospitalId(selected.getHospitalId());
            Integer uploadFileId = fileService.uploadBase64File(item.getString("src"), fileName);
            attach.setFileId(uploadFileId);
            AssetFileAttachmentRepository attachDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
            attachDao.save(attach);
        }

    }

    public void applySave() {
        this.save();
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return dao;
    }

    public UserAccount getAssetOwner() {
        return assetOwner;
    }

    public void setAssetOwner(UserAccount assetOwner) {
        this.assetOwner = assetOwner;
    }

    public UserAccount getClinicalOwner() {
        return clinicalOwner;
    }

    public void setClinicalOwner(UserAccount clinicalOwner) {
        this.clinicalOwner = clinicalOwner;
    }

    public boolean isResultStatus() {
        return resultStatus;
    }

    public void setFileService(AttachmentFileService fileService) {
        this.fileService = fileService;
    }

    public String getUploaderFiles() {
        return uploaderFiles;
    }

    public void setUploaderFiles(String uploaderFiles) {
        this.uploaderFiles = uploaderFiles;
    }

}
