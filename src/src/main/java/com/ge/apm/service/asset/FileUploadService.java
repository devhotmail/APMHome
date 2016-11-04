/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import jodd.io.StreamUtil;
import org.primefaces.model.UploadedFile;

@ManagedBean
public class FileUploadService {
    
    String rootPath = "C:\\uploads/";


    public Boolean uploadFile(UploadedFile file) {
        String fileName = "";
        try {
            fileName = new String(file.getFileName().getBytes(),"utf-8") ;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileUploadService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        File outputFile = new File(rootPath+fileName);
        
        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream fis = file.getInputstream();
            StreamUtil.copy(fis, fos);
            fos.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUploadService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(FileUploadService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
}