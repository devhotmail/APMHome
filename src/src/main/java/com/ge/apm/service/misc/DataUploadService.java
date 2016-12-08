package com.ge.apm.service.misc;

import com.ge.apm.domain.AssetClinicalRecord;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(name="ClinicalDataService", targetNamespace="com.ge.dh.apm")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT,use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public interface DataUploadService {

    @WebResult(name = "soapResult")
    @WebMethod(operationName = "uploadAssetClinicalRecord", action = "uploadAssetClinicalRecord")
    public String uploadAssetClinicalRecord(AssetClinicalRecord assetClinicalRecord);
    
}
