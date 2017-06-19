/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author 212579464
 */
@Component
public class MicroServiceUtil {

    @Autowired
    private RestTemplate template;

    @Value("#{urlProperties.url_getAuthenticateByUserPassword}")
    private String url_getAuthenticateByUserPassword;
    @Value("#{urlProperties.url_uploadSingleFileByUrl}")
    private String url_uploadSingleFileByUrl;

    public Map<String, Object> uploadSingleFileByUrl(String token, String url,String fileName) {
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
}
