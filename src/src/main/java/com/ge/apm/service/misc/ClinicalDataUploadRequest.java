/*
 */
package com.ge.apm.service.misc;

import com.ge.apm.domain.AssetClinicalRecord;

/**
 *
 * @author 212547631
 */
public class ClinicalDataUploadRequest {
    private String edgeServerKey;
    private AssetClinicalRecord clinicalRecord;

    public String getEdgeServerKey() {
        return edgeServerKey;
    }

    public void setEdgeServerKey(String edgeServerKey) {
        this.edgeServerKey = edgeServerKey;
    }

    public AssetClinicalRecord getClinicalRecord() {
        return clinicalRecord;
    }

    public void setClinicalRecord(AssetClinicalRecord clinicalRecord) {
        this.clinicalRecord = clinicalRecord;
    }
    
}
