package com.ge.apm.service.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.Logger;
//import java.io.ByteArrayOutputStream;
//import sun.misc.BASE64Encoder;

public class FileUtils {

    private static final Logger log = Logger.getLogger(FileUtils.class.getName());

    public static boolean isFileEmpty(String filePath){
        File file = new File(filePath);
        return file.length()==0;
    }
    
    public static void createFolder(String folderPath, boolean deleteExistingFiles) {
        if(deleteExistingFiles) deleteFolder(folderPath);
        
        File file = new File(folderPath);
        if (!file.exists()) file.mkdirs();
    }

    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            file.delete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    public static void moveFile(String filePath,String newFilePath) {
        try {
            File file = new File(filePath);
            file.renameTo(new File(newFilePath));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    public static void deleteFolder(String folderPath) {
        try {
            deleteAllFiles(folderPath);
            java.io.File myFilePath = new java.io.File(folderPath);
            myFilePath.delete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static List<String> getFileList(String path){
        List<String> result = new ArrayList<String>(0);
        
        File rootFile = new File(path);
        if (!rootFile.exists())  return result;
        if (!rootFile.isDirectory()) return result;

        String[] fileList = rootFile.list();
        File file;
        for (String fileName : fileList) {
            if (path.endsWith(File.separator)) {
                file = new File(path + fileName);
            } else {
                file = new File(path + File.separator + fileName);
            }
            if (file.isFile()) {
                result.add(file.getPath());
            }
        }

        return result;
    }
    
    public static boolean deleteAllFiles(String path) {
        boolean flag = false;
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            return flag;
        }
        if (!rootFile.isDirectory()) {
            return flag;
        }
        String[] fileList = rootFile.list();
        File file;
        for (String fileName : fileList) {
            if (path.endsWith(File.separator)) {
                file = new File(path + fileName);
            } else {
                file = new File(path + File.separator + fileName);
            }
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                deleteAllFiles(path + "/" + fileName);
                deleteFolder(path + "/" + fileName);
                flag = true;
            }
        }
        return flag;
    }

    public static String getFileListAsString(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        if (!file.isDirectory()) {
            return null;
        }
        String fileNames = "";
        String[] tempList = file.list();
        for (String fileName : tempList) {
            fileNames += fileName + " ";
        }
        return fileNames;
    }

    public static void downloadFile(String ftpURL, String localFilePath, long timeout)
            throws Exception {

        InputStream inputStream = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(ftpURL);
            inputStream = url.openStream();
            int count = 0;
            long starttime = new Date().getTime();
            long endtime;
            while (count == 0) {
                count = inputStream.available();
                endtime = new Date().getTime();
                if (timeout != 0 && (endtime - starttime >= timeout)) {
                    throw new Exception("Download file[" + ftpURL + "] timeout !");
                }
            }

            out = new FileOutputStream(localFilePath);
            byte[] buff = new byte[count];
            int bytesRead;

            while (-1 != (bytesRead = inputStream.read(buff))) {
                out.write(buff, 0, bytesRead);
            }
            out.flush();

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (out != null) {
                out.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
/*
    public static String convertFile2Base64(String filePath) throws Exception {
        InputStream inputStream = null;
        ByteArrayOutputStream out = null;
        try {
            URL url = new URL(filePath);
            inputStream = url.openStream();
            int count = 0;
            while (count == 0) {
                count = inputStream.available();
            }
            byte[] data = new byte[count];
            out = new ByteArrayOutputStream(data.length);
            int n;
            // long start=Calendar.getInstance().getTimeInMillis();
            while ((n = inputStream.read(data)) != -1) {
                out.write(data, 0, n);

            }

            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(out.toByteArray());

        } catch (Exception e) {
            throw new Exception("failed to conver file to base64：" + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
*/

    public static void zipFolder(String folderToZip, String saveToZipFile) throws Exception {
        zipFolder(folderToZip, saveToZipFile, false);
    }
    
    public static void zipFolder(String folderToZip, String saveToZipFile, boolean addDicomSuffix) throws Exception {
        File file = new File(folderToZip);
        File zipFile = new File(saveToZipFile);
        BufferedInputStream input;
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; ++i) {
                String fileName = files[i].getName();
                if(addDicomSuffix){
                    if(!fileName.endsWith(".dcm"))
                        fileName = fileName + ".dcm";
                }
                    
                input = new BufferedInputStream(new FileInputStream( files[i]));
                zipOut.putNextEntry(new ZipEntry(fileName));
                int len;
                byte[] b = new byte[1024 * 20];
                while ((len = input.read(b, 0, b.length)) != -1) {
                    zipOut.write(b, 0, len);
                }
                input.close();
            }
        }
        zipOut.close();
    }
    
    /**
     *
     * @param zip
     * @param path
     * @param srcFiles
     * @throws IOException
     * @author isea533
     */
    public static void ZipFiles(File zip, String path, File... srcFiles)
            throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
        ZipFiles(out, path, srcFiles);
        out.close();
    }

    public static void ZipFiles(ZipOutputStream out, String path, File... srcFiles) {
        path = path.replaceAll("\\*", "/");
        if (!path.endsWith("/")) {
            path += "/";
        }
        byte[] buf = new byte[1024];
        try {
            for (File srcFile : srcFiles) {
                if (srcFile.isDirectory()) {
                    File[] files = srcFile.listFiles();
                    String srcPath = srcFile.getName();
                    srcPath = srcPath.replaceAll("\\*", "/");
                    if (!srcPath.endsWith("/")) {
                        srcPath += "/";
                    }
                    out.putNextEntry(new ZipEntry(path + srcPath));
                    ZipFiles(out, path + srcPath, files);
                } else {
                    FileInputStream in = new FileInputStream(srcFile);

                    out.putNextEntry(new ZipEntry(path + srcFile.getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                    in.close();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void unZipFiles(String zipPath, String destDir)
            throws IOException {
        unZipFiles(new File(zipPath), destDir);
    }

    public static void unZipFiles(File zipFile, String destDir)
            throws IOException {
        File pathFile = new File(destDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (destDir + zipEntryName).replaceAll("\\*", "/");

            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            File outfile = new File(outPath);
            if (outfile.isDirectory()) continue;

            if (outfile.exists()) {
                boolean result = outfile.delete();
                if (!result) {
                    System.gc();
                    outfile.delete();
                }
            }
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public static void main(String args[]) {
        try {
            FileUtils.zipFolder("C:/GE", "C:/GUID/a.zip", true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
