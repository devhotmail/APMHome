/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.LinkedHashMap;

/**
 *
 * @author 212579464
 */
@Component
public class MicroServiceInvoker {
    
    @Autowired
    protected RestTemplate template;
    
    
    protected Boolean isOkay(ResponseEntity res) {

        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            return false;
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) res.getBody();

        return body.containsKey("bizStatusCode") && body.get("bizStatusCode").equals("OK");
    }
}
