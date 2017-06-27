/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212579464
 */
@Component
public class FileScanService {

    private static Long TIME_OUT = 5000l;
    
    @Value("#{urlProperties.FileScanFolder}")
    private String FILE_SCAN_FOLDER;

    public String saveUploadedFile(UploadedFile file) throws Exception {
       
        String folderName = FILE_SCAN_FOLDER.concat(File.separator).concat(UUID.randomUUID().toString());
        FileUtils.createFolder(folderName, true);
        String fileName = folderName.concat(File.separator).concat(FileUtils.getUploadedFileName(file.getFileName()));
        
        try(FileOutputStream out = new FileOutputStream(fileName)) {
            InputStream inputStream = file.getInputstream();
            int count = 0;
            long starttime = new Date().getTime();
            long endtime;
            while (count == 0) {
                count = inputStream.available();
                endtime = new Date().getTime();
                if (endtime - starttime >= TIME_OUT) {
                    throw new Exception("Save file[" + fileName + "] timeout !");
                }
            }
            byte[] buff = new byte[count];
            int bytesRead;

            while (-1 != (bytesRead = inputStream.read(buff))) {
                out.write(buff, 0, bytesRead);
            }
            out.flush();

        } catch (Exception e) {
            throw e;
        } 
        
        return fileName;
        
    }

    public void deleteUploadedFile(String path) {
        FileUtils.deleteFolder(path.substring(0,path.lastIndexOf(File.separator)));
    }
    
    
}
