/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.service.utils.MicroServiceUtil;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.web.WebUtil;
import javax.annotation.PostConstruct;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class FileDownloadService {
    
    private MicroServiceUtil msService;

    @PostConstruct
    protected void init() {
        msService = WebUtil.getBean(MicroServiceUtil.class);
    }
    
    public StreamedContent getFile(String fileId) {
        return msService.downloadSingleFile(fileId);
    }
    
    public String getFileUrl(String fileId){
        return msService.getUrl_downloadFile().concat("/").concat(fileId);
    }
    
}
