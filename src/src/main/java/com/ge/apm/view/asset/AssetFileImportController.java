/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.utils.ExcelDocument;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetFileImportController {

    private UploadedFile file;

    private Map<String, Map<String, Object>> importOrgMap;
    private Map<String, Map<String, Object>> importUserMap;
    private Map<String, Map<String, Object>> importAssetMap;

    private AssetFileImportService aiService;

    private Boolean isImported = false;
    private Boolean hasExportData = false;
    
    private Boolean canbeImported = false;

    private StreamedContent exportFile;

    @PostConstruct
    protected void init() {
        aiService = WebUtil.getBean(AssetFileImportService.class);

    }

    public void uploadDataFile(FileUploadEvent event) {
        try {
            this.file = event.getFile();
            ExcelDocument doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            if(!aiService.checkDocVersion(doc)){
                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError"));
                return;
            }
            importOrgMap = aiService.getOrgInfoMapFromTemplate(doc);
            importAssetMap = aiService.getAssetMapFromTemplate(doc);
            importUserMap = aiService.getUserMapFromTemplate(doc);
            
            canbeImported = aiService.checkData(importOrgMap,importAssetMap,importUserMap);

        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(AssetFileImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError"));
            Logger.getLogger(AssetFileImportController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void importData() {
        if (null != importAssetMap) {
            aiService.importData(importOrgMap, importAssetMap);
            isImported = true;

            try {
                InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/assetImport/APM_Asset_Template.xlsx");
//                InputStream stream = this.file.getInputstream();
                ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
                hasExportData = aiService.exportFailData(doc, importOrgMap, importAssetMap,importUserMap);
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "failedData.xlsx");
            } catch (IOException ex) {
                Logger.getLogger(AssetFileImportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void cancel() {
        this.file = null;

    }

    //Getter and Setter
    public Map<String, Map<String, Object>> getImportOrgMap() {
        return importOrgMap;
    }

    public Map<String, Map<String, Object>> getImportUserMap() {
        return importUserMap;
    }

    public Map<String, Map<String, Object>> getImportAssetMap() {
        return importAssetMap;
    }

    public Boolean getIsImported() {
        return isImported;
    }

    public Boolean getHasExportData() {
        return hasExportData;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public Boolean getCanbeImported() {
        return canbeImported;
    }
    
}
