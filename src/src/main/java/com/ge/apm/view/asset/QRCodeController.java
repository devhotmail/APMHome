/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class QRCodeController {

    private List<String> qrCodeList;

    @PostConstruct
    protected void init() {
        createBatchCode();
    }

    public void createBatchCode() {
        if (null == qrCodeList) {
            qrCodeList = new ArrayList();
        } else {
            qrCodeList.clear();
        }
        for (int i = 1; i < 20; i++) {
            qrCodeList.add(UUID.randomUUID().toString());
        }

    }

    public List<String> getQrCodeList() {
        return qrCodeList;
    }
    
    
}
