package com.ge.apm.service.wechat;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeAttachmentRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.joda.time.DateTime;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 212605082 on 2017/3/21.
 */
@Service
public class QrCreateAssetService {

    @Autowired
    protected WxMpService wxMpService;

    @Autowired
    protected CoreService coreService;

    @Autowired
    QrCodeLibRepository qrCodeLibRepository;

    @Autowired
    QrCodeAttachmentRepository qrCodeAttachmentRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    OrgInfoRepository orgInfoRepository;

    @Autowired
    protected BlobFileService blobService;

    @Autowired
    BlobObjectRepository blobDao;

    @Transactional
    public Boolean createAsset(String openId, String qrCode, String voiceServerId, String imageServerIds, String uploaderFileBase64, String comment, String assetName, String assetGroupSelect, String orgSelect, String userSelect) throws WxErrorException {

        QrCodeLib qrCodeLib = qrCodeLibRepository.findByQrCode(qrCode);

        if (qrCodeLib == null || qrCodeLib.getStatus() == 3) {
            Logger.getLogger(QrCreateAssetService.class.getName()).log(Level.SEVERE, "error qrcode or error qrCode status", "");
            return Boolean.FALSE;
        }

        qrCodeLib.setAssetName(assetName);
        if (assetGroupSelect != null && !assetGroupSelect.equals("")) {
            qrCodeLib.setAssetGroup(Integer.valueOf(assetGroupSelect));
        }
        if (orgSelect != null && !orgSelect.equals("")) {
            qrCodeLib.setOrgId(Integer.valueOf(orgSelect));
        } else {
            qrCodeLib.setOrgId(null);
        }
        if (userSelect != null && !userSelect.equals("")) {
            qrCodeLib.setUserId(Integer.valueOf(userSelect));
        } else {
            qrCodeLib.setUserId(null);
        }
        qrCodeLib.setSubmitDate(new Date());
        qrCodeLib.setSubmitWechatId(openId);
        qrCodeLib.setStatus(2);

        DateTime dt = new DateTime();
        String dtStr = dt.toString("yyyyMMdd HH:mm:ss");

        String tempComment = null;
        if (comment != null && !comment.trim().equals("")) {
            tempComment = dtStr + " " + comment;
        }
        qrCodeLib.setComment(qrCodeLib.getComment() == null ? tempComment : qrCodeLib.getComment() + "|" + tempComment);

        QrCodeLib tempQrCodeLib = qrCodeLibRepository.save(qrCodeLib);

        if (tempQrCodeLib == null) {
            return Boolean.FALSE;
        }

        /* 处理图片 */
        if (imageServerIds != null && !imageServerIds.isEmpty()) {

            JSONObject data = new JSONObject(imageServerIds);
            JSONArray fileList = data.getJSONArray("imageServerIds");
            for (int i = 0; i < fileList.length(); i++) {
                JSONObject item = (JSONObject) fileList.getJSONObject(i);
                String imageServerId = item.getString("imageServerId");

                File file = wxMpService.getMaterialService().mediaDownload(imageServerId);
//                Integer fileId = coreService.uploadFile(file);

                V2_BlobObject blobObject = blobService.uploadWxFile(openId, file);
                if (null != blobObject) {
                    blobObject.setBoId(qrCodeLib.getId());
                    blobObject.setBoSubType("1");
                    blobObject.setBoType(V2_BlobObject.BO_TYPE_QRCodeLib);
                    blobObject.setBoUuid(qrCodeLib.getUid());
                    blobDao.save(blobObject);
                    QrCodeAttachment qrCodeAttachment = new QrCodeAttachment();
                    qrCodeAttachment.setQrCodeId(qrCodeLib.getId());
                    qrCodeAttachment.setFileType(1);
                    qrCodeAttachment.setObjectId(blobObject.getObjectStorageId());
                    qrCodeAttachmentRepository.save(qrCodeAttachment);
                }

//                QrCodeAttachment qrCodeAttachment = new QrCodeAttachment();
//                qrCodeAttachment.setFileId(fileId);
//                qrCodeAttachment.setQrCodeId(qrCodeLib.getId());
                /*-- 1:照片 / 2: 语音*/
//                qrCodeAttachment.setFileType(1);
//                qrCodeAttachmentRepository.save(qrCodeAttachment);
            }

        }

        /* 处理语音 */
        if (voiceServerId != null && !voiceServerId.isEmpty()) {

            File file = wxMpService.getMaterialService().mediaDownload(voiceServerId);
            V2_BlobObject blobObject = blobService.uploadWxFile(openId, file);
            if (null != blobObject) {
                blobObject.setBoId(qrCodeLib.getId());
                blobObject.setBoSubType("2");
                blobObject.setBoType(V2_BlobObject.BO_TYPE_QRCodeLib);
                blobObject.setBoUuid(qrCodeLib.getUid());
                blobDao.save(blobObject);
                QrCodeAttachment qrCodeAttachment = new QrCodeAttachment();
                qrCodeAttachment.setQrCodeId(qrCodeLib.getId());
                qrCodeAttachment.setFileType(2);
                qrCodeAttachment.setObjectId(blobObject.getObjectStorageId());
                qrCodeAttachmentRepository.save(qrCodeAttachment);
            }

//            Integer fileId = coreService.uploadFile(file);
//
//            QrCodeAttachment qrCodeAttachment = new QrCodeAttachment();
//            qrCodeAttachment.setFileId(fileId);
//            qrCodeAttachment.setQrCodeId(qrCodeLib.getId());
//            /*-- 1:照片 / 2: 语音*/
//            qrCodeAttachment.setFileType(2);
//            qrCodeAttachmentRepository.save(qrCodeAttachment);

        }

        return Boolean.TRUE;
    }

    public String validateQrCode(String openId, String qrCode) {

        QrCodeLib qrCodeLib = qrCodeLibRepository.findByQrCode(qrCode);

        if (qrCodeLib != null) {
            if (qrCodeLib.getStatus() == 3) {
                UserAccount userAccount = userAccountRepository.getByWeChatId(openId);
                if (userAccount == null) {
                    //qrcode已经关联设备，但扫码用户未绑定
                    return "3";
                } else {
                    //qrcode已经关联设备，但扫码用户已绑定
                    return "2";
                }
            } else if (qrCodeLib.getStatus() == 2) {
                //qrcode已经有上传资料但还没建档关联设备
                return "4";
            } else {
                //qrcode还未关联设备
                return "1";
            }

        } else {
            //不是系统生成的qrcode
            return "9";
        }
    }

    public Boolean createQrCode(int count, int siteId, int hospitalId) {

        Set<String> tempSet = new HashSet<String>(count);

        DateTime dt = new DateTime();
        String dtStr = dt.toString("yyMMdd");

        int i = 1;
        while (true) {
            String qrCode = dtStr + Long.valueOf((long) ((Math.random() * 9 + 1) * 1000000000L));

            if (!tempSet.contains(qrCode)) {

                QrCodeLib qrCodeLib = qrCodeLibRepository.findByQrCode(qrCode);
                if (null == qrCodeLib) {
                    qrCodeLib = new QrCodeLib();
                    qrCodeLib.setSiteId(siteId);
                    qrCodeLib.setHospitalId(hospitalId);
                    /*-- 1:已发行(未上传) / 2: 已上传(待建档) / 3: 已建档(待删除)*/
                    qrCodeLib.setStatus(1);
                    qrCodeLib.setIssueDate(new Date());
                    qrCodeLib.setQrCode(qrCode);
                    tempSet.add(qrCode);
                    qrCodeLibRepository.save(qrCodeLib);

                    System.out.println("插入" + qrCode);
                    i++;
                }

            } else {
                System.out.println("重复" + qrCode);
            }

            if (i > count) {
                return Boolean.TRUE;
            }
        }
    }

    public List<I18nMessage> getMsg(String msgType) {
        return coreService.getMsg(msgType);
    }

    public List<OrgInfo> getOrgList(String qrCode) {
        QrCodeLib qrCodeLib = qrCodeLibRepository.findByQrCode(qrCode);

        if (qrCodeLib != null) {
            return orgInfoRepository.getByHospitalId(qrCodeLib.getHospitalId());
        } else {
            return null;
        }

    }

    public List<UserAccount> getUserList(int orgInfoId) {
        return userAccountRepository.getByOrgInfoId(orgInfoId);
    }

    public static void main(String args[]) {

        QrCreateAssetService t = new QrCreateAssetService();
        //t.createQrCode(10);

    }

}
