/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.domain.AssetFileAttachment;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.WebUtil;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.data.DataMigrateService;
import com.ge.apm.service.utils.MicroServiceUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class DataMigrateController {

    private List<AssetFileAttachment> assetAttachmentList;

    private AssetFileAttachmentRepository attchDao;

    private AssetCreateService acService;
    private MicroServiceUtil msService;
    private DataMigrateService dmService;

    @PostConstruct
    protected void init() {

        attchDao = WebUtil.getBean(AssetFileAttachmentRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        msService = WebUtil.getBean(MicroServiceUtil.class);
        dmService = WebUtil.getBean(DataMigrateService.class);

        assetAttachmentList = getAttachementList();
    }

    public List<AssetFileAttachment> getAttachementList() {
        List<AssetFileAttachment> alllist = attchDao.find();
        return alllist.stream().filter(item ->  item.getFileUrl()==null).collect(Collectors.toList());
    }

    public String getHospitalName(Integer hospitalId) {
        return acService.getHospitalName(hospitalId);
    }

    public String getSiteName(Integer siteId) {
        return acService.getSiteName(siteId);
    }

    public void migrateAssetAttachData() {

        if(UserContextService.getCurrentUserAccount().getWeChatId()==null){
            WebUtil.addErrorMessage("do not have wechatid");
            return;
        }
        
        String token = UserContextService.getAccessToken();
        if(token==null || token.isEmpty()){
            WebUtil.addErrorMessage("do not have premission");
            return;
        }

        assetAttachmentList.stream().forEach(item -> {
            dmService.migrateAttchment(item,token);
        });
        
    }

    public List<AssetFileAttachment> getAssetAttachmentList() {
        return assetAttachmentList;
    }

}
