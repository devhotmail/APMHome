/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeAttachmentRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.SiteInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeAttachment;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.SiteInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkOrderPhoto;
import com.ge.apm.service.utils.ConfigUtils;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.service.wechat.CoreService;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class AssetCreateService {

    @Autowired
    protected WxMpService wxMpService;
    @Autowired
    private SiteInfoRepository siteDao;
    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private QrCodeLibRepository qrLibDao;
    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    AssetFileAttachmentRepository attachDao;
    @Autowired
    QrCodeAttachmentRepository qrCodeAttachDao;
    @Autowired
    CoreService coreService;
    @Autowired
    ConfigUtils configUtils;

    AttachmentFileService fileService = WebUtil.getBean(AttachmentFileService.class);

    
    
    public String getSiteName(Integer siteId) {
        SiteInfo si = siteDao.findById(siteId);
        return si.getName();
    }

    public String getHospitalName(Integer hospitalId) {
        OrgInfo org = orgDao.findById(hospitalId);
        return org.getName();
    }

    public QrCodeLib getCreateRequest(String qrCode) {
        return qrLibDao.findByQrCode(qrCode);
    }

    public List<OrgInfo> getClinicalDeptList(Integer hospitalId) {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, hospitalId));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    @Transactional
    public Boolean CreateAeest(AssetInfo assetInfo) {
        return (null != assetDao.save(assetInfo));
    }

    public List<UserAccount> getAssetOnwers(int siteId, Integer hospitalId) {
        //get all users in this hospital and has 'MultiHospital' users in this site 
        List<UserAccount> users = userDao.getAssetOnwers(siteId, hospitalId);
        return users;
    }

    @Transactional
    public void createAttachments(AssetInfo assetInfo, Integer[] selectedPictures) {
        List<AssetFileAttachment> attachList = new ArrayList();
        for (Integer item : selectedPictures) {
            AssetFileAttachment attach = new AssetFileAttachment();
            attach.setAssetId(assetInfo.getId());
            attach.setFileId(item);
            attach.setFileType("1");
            attach.setHospitalId(assetInfo.getHospitalId());
            attach.setSiteId(assetInfo.getSiteId());
            attach.setName("image".concat(item.toString()));
            attachList.add(attach);
        }
        attachDao.save(attachList);
    }

    public List<QrCodeAttachment> getQrCodePictureList(Integer qrCodeLibId) {
        List<SearchFilter> qrCodeFilters = new ArrayList<>();
        qrCodeFilters.add(new SearchFilter("qrCodeId", SearchFilter.Operator.EQ, qrCodeLibId));
        qrCodeFilters.add(new SearchFilter("fileType", SearchFilter.Operator.EQ, 1));
        List<QrCodeAttachment> picsList = qrCodeAttachDao.findBySearchFilter(qrCodeFilters);
        return picsList;
    }

    @Transactional
    public void updateQrLib(String qrCode, int i) {
        QrCodeLib qrLibItem = qrLibDao.findByQrCode(qrCode);
        qrLibItem.setStatus(i);
        qrLibDao.save(qrLibItem);
    }

    public List<QrCodeAttachment> getQrCodeAudioList(Integer qrCodeLibId) {
        List<SearchFilter> qrCodeFilters = new ArrayList<>();
        qrCodeFilters.add(new SearchFilter("qrCodeId", SearchFilter.Operator.EQ, qrCodeLibId));
        qrCodeFilters.add(new SearchFilter("fileType", SearchFilter.Operator.EQ, 2));
        List<QrCodeAttachment> picsList = qrCodeAttachDao.findBySearchFilter(qrCodeFilters);
        return picsList;
    }

    public boolean rejectReturn(QrCodeLib request, String rejectText) {
        String _openId = request.getSubmitWechatId();
        String _templateId = configUtils.fetchProperties("asset_build_template_id");
        Map<String, Object> map = new HashMap();
        map.put("first", "建档失败");
//        map.put("_qrCode", request.getQrCode());
//        map.put("_requestTime", TimeUtils.getStrDate(request.getSubmitDate(), "yyyy-MM-dd HH:mm:ss"));
//        map.put("_detail", rejectText);
        map.put("keyword1", rejectText);
        map.put("keyword2", TimeUtils.getStrDate(request.getSubmitDate(), "yyyy-MM-dd HH:mm:ss"));
        map.put("linkUrl", "wechat/asset/createInfoDetail.xhtml?qrCode=".concat(request.getQrCode()));

        coreService.sendWxTemplateMessage(_openId, _templateId, map);

        rejectText = TimeUtils.getStrDate(new Date(), "[yyyy-MM-dd HH:mm:ss]").concat(rejectText);

        request.setFeedback(request.getFeedback() == null ? rejectText : request.getFeedback().concat("//").concat(rejectText));
        qrLibDao.save(request);
        return true;
    }

    public OrgInfo getOrgInfo(Integer clinicalDeptId) {
        return orgDao.findById(clinicalDeptId);
    }

    public List<OrgInfo> getOrgListByHospital(Integer hospitalId) {
        return orgDao.getByHospitalId(hospitalId);
    }

    public String pushVoiceToWechat(Integer fileId) throws Exception {
        StreamedContent sc = fileService.getFile(fileId);
        return coreService.uploadMediaToWechat(sc.getStream());
    }

//    public String pushImageToWechat(Integer fileId) throws Exception {
//        StreamedContent sc = fileService.getFile(fileId);
//        return coreService.uploadImageToWechat(sc.getStream());
//    }

    public UserAccount getUserInfo(Integer userId) {
        return userDao.getById(userId);
    }

    public List<UserAccount> getClinicalOwnerList(Integer clinicalDeptId) {
        List<SearchFilter> usersFilters = new ArrayList<>();
        if (null != clinicalDeptId) {
            usersFilters.add(new SearchFilter("orgInfoId", SearchFilter.Operator.EQ, clinicalDeptId));
            return userDao.findBySearchFilter(usersFilters);
        } else {
            return new ArrayList<UserAccount>();
        }
    }

    @Transactional
    public void removeAttachment(Integer id, int fileid) {
        List<SearchFilter> attachFilters = new ArrayList<>();
        attachFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, id));
        attachFilters.add(new SearchFilter("fileId", SearchFilter.Operator.EQ, fileid));
        List<AssetFileAttachment> attList = attachDao.findBySearchFilter(attachFilters);
        attList.forEach((item) -> {
            fileService.deleteAttachment(item.getFileId());
            attachDao.delete(item);
        });
    }

    public void addWechatPicAttachment(AssetInfo assetInfo, String serverId) {
        File file = null;
        try {
            file = wxMpService.getMaterialService().mediaDownload(serverId);
        } catch (WxErrorException ex) {
            Logger.getLogger(AssetCreateService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (file == null) {
            return;
        }
        Integer uploadFileId = fileService.uploadFile(file);

        if (uploadFileId > 0) {
            AssetFileAttachment attach = new AssetFileAttachment();
            attach.setAssetId(assetInfo.getId());
            attach.setFileId(uploadFileId);
            attach.setFileType("1");
            attach.setHospitalId(assetInfo.getHospitalId());
            attach.setSiteId(assetInfo.getSiteId());
            attach.setName("image".concat(uploadFileId.toString()));
            attachDao.save(attach);
        }
        file.delete();
    }

	public String getOrgName(Integer orgId) {
		if(orgId != null){
			return orgDao.findById(orgId).getName();
		}
		return null;
	}

}
