package com.ge.apm.edgeserver.dataupload.fromsite;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.ge.apm.edgeserver.dataupload.entity.AssetClinicalRecordDelta;
import javax.jws.WebParam;

@WebService(name="ClinicalDataService", targetNamespace="com.ge.dh.apm")
@SOAPBinding(style=SOAPBinding.Style.RPC,use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public interface DataUploadService {

    @WebResult(name = "soapResult")
    @WebMethod(operationName = "uploadAssetClinicalRecord", action = "uploadAssetClinicalRecord")
    public SoapResult uploadAssetClinicalRecord(@WebParam(name="assetClinicalRecord") AssetClinicalRecordDelta assetClinicalRecordDelta);
    
}
