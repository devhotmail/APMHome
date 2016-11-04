/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.domain.AssetFileAttachment;
import java.io.InputStream;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author 212579464
 */
@ManagedBean
public class FileDownloadService {
    private StreamedContent file;
     
//    public FileDownloadService() {        
//        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/demo/images/optimus.jpg");
//        file = new DefaultStreamedContent(stream, "image/jpg", "downloaded_optimus.jpg");
//    }
 
    
    
    public StreamedContent getFile(AssetFileAttachment attachFile) {
        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/img/ajaxloader.gif");
        file = new DefaultStreamedContent(stream, attachFile.getFileType(), attachFile.getName());
        
        return file;
    }
}
