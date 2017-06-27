/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import org.primefaces.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author 212579464
 */
@Component
public class MicroServiceUtil {

    @Autowired
    private RestTemplate template;

    @Value("#{urlProperties.url_getAuthenticateByOpenId}")
    private String url_getAuthenticateByOpenId;
    @Value("#{urlProperties.url_getAuthenticateByUserPassword}")
    private String url_getAuthenticateByUserPassword;
    @Value("#{urlProperties.url_uploadSingleFileByUrl}")
    private String url_uploadSingleFileByUrl;
    @Value("#{urlProperties.url_uploadSingleFile}")
    private String url_uploadSingleFile;
    @Value("#{urlProperties.url_deleteFile}")
    private String url_deleteFile;
    @Value("#{urlProperties.url_downloadFile}")
    private String url_downloadFile;

    public StreamedContent downloadSingleFile(String fileId) {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<byte[]> response = template.exchange(url_downloadFile.concat("/").concat(fileId), HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);
        HttpHeaders responseHeader = response.getHeaders();
        byte[] result = response.getBody();
        InputStream ins = new ByteArrayInputStream(result);
        DefaultStreamedContent fileStream = new DefaultStreamedContent(ins);
        fileStream.setContentType(responseHeader.getContentType().toString());
        List<String> dispositions = responseHeader.get("content-disposition");
        String disposition = (String) dispositions.get(0);
        String fileName = disposition.substring(disposition.lastIndexOf("name=") + 5);
        fileStream.setName(fileName);
        return fileStream;
    }

    public Boolean deleteFileObject(String token, String objectId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        headers.add("Authorization", token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

        String url = url_deleteFile.concat("/").concat(objectId);
        ResponseEntity<? extends LinkedHashMap<String, Object>> response = template.exchange(url_deleteFile.concat("/").concat(objectId), HttpMethod.DELETE, requestEntity, LinkedHashMap.class);
        return isOkay(response);
    }

    public Map<String, Object> uploadSingleFile(String token, File file) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "multipart/form-data");
        headers.add("Authorization", token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        FileSystemResource resource = new FileSystemResource(file);
        body.add("file", resource);

        HttpEntity<Map> requestEntity = new HttpEntity<Map>(body, headers);
        ResponseEntity<? extends LinkedHashMap<String, Object>> response = template.postForEntity(url_uploadSingleFile, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            return data;
        }

        return null;
    }

    public Map<String, Object> uploadSingleFileByUrl(String token, String url, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        headers.add("Authorization", token);

        Map<String, String> postParameters = new HashMap<String, String>();
        postParameters.put("urlFile", url);
        postParameters.put("fileName", fileName);
        String requestBody = JSONObject.valueToString(postParameters);
        HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, headers);
        ResponseEntity<? extends LinkedHashMap<String, Object>> response = template.postForEntity(url_uploadSingleFileByUrl, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            return data;
        }
        return null;
    }

    private Boolean isOkay(ResponseEntity res) {

        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            return false;
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) res.getBody();

        return body.containsKey("bizStatusCode") && body.get("bizStatusCode").equals("OK");
    }

    public String getAuthenticateByOpenId(String code, String weChatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Map<String, String> postParameters = new HashMap<String, String>();
        postParameters.put("code", code);
        postParameters.put("weChatId", weChatId);
        String requestBody = JSONObject.valueToString(postParameters);

        HttpEntity<String> requestEntity = new HttpEntity<String>(
                requestBody, headers);

        ResponseEntity<? extends LinkedHashMap<String, Object>> response = template.postForEntity(url_getAuthenticateByOpenId, requestEntity, LinkedHashMap.class);

        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            if (null != data && data.containsKey("jwtToken")) {
                LinkedHashMap<String, Object> jwtToken = (LinkedHashMap<String, Object>) data.get("jwtToken");
                if (null != jwtToken && jwtToken.containsKey("id_token")) {
                    return (String) jwtToken.get("id_token");
                }
            }
        }

        return null;

    }

    public String getAuthenticateByUserPassword(String userName, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");

        Map<String, String> postParameters = new HashMap<String, String>();
        postParameters.put("loginName", userName);
        postParameters.put("password", password);
        String requestBody = JSONObject.valueToString(postParameters);

        HttpEntity<String> requestEntity = new HttpEntity<String>(
                requestBody, headers);

        ResponseEntity<? extends LinkedHashMap<String, Object>> response = template.postForEntity(url_getAuthenticateByUserPassword, requestEntity, LinkedHashMap.class);

        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            if (null != data && data.containsKey("jwtToken")) {
                LinkedHashMap<String, Object> jwtToken = (LinkedHashMap<String, Object>) data.get("jwtToken");
                if (null != jwtToken && jwtToken.containsKey("id_token")) {
                    return (String) jwtToken.get("id_token");
                }
            }
        }

        return null;

    }

    public String getUrl_downloadFile() {
        return url_downloadFile;
    }

}
