/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.data.MM3FileImportService;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.asset.AssetFileImportController;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class MM3DataReportController {

    private UploadedFile file;

    private Map<String, Map<String, Object>> importMM3DataMap;

    private MM3FileImportService mm3Service;
    private AssetCreateService aiService;

    private Boolean isImported = false;

    @PostConstruct
    protected void init() {
        mm3Service = WebUtil.getBean(MM3FileImportService.class);
        aiService = WebUtil.getBean(AssetCreateService.class);
    }

    public void uploadDataFile(FileUploadEvent event) {
        try {
            this.file = event.getFile();
            ExcelDocument doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));

            importMM3DataMap = mm3Service.getMM3DataMapFromTemplate(doc);
            mm3Service.checkData(importMM3DataMap);

        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(MM3DataReportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError"));
            Logger.getLogger(MM3DataReportController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void importData(){
        if (null != importMM3DataMap) {
            mm3Service.importData(importMM3DataMap);
            isImported = true;
        }
    }

    public void cancel() {
        this.file = null;

    }
    
    public String getTenantName(AssetInfo asset){
       return asset==null?"":aiService.getTenantName(asset.getSiteId());
    }
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Boolean getIsImported() {
        return isImported;
    }

    public Map<String, Map<String, Object>> getImportMM3DataMap() {
        return importMM3DataMap;
    }


}
