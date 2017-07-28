package com.ge.apm.dao;

import com.ge.apm.domain.V2_ServiceRequest;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import webapp.framework.dao.GenericRepositoryUUID;

public interface ServiceRequestRepository extends GenericRepositoryUUID<V2_ServiceRequest> {

    V2_ServiceRequest findByIdAndHospitalId(String id, Integer hospitalId);

    public List<V2_ServiceRequest> findByAssetId(int assetId);

    public Page<V2_ServiceRequest> findByAssetIdAndHospitalIdAndStatusOrderByRequestTimeDesc(int assetId, int hospitalId, int status, Pageable pageable);

    public Page<V2_ServiceRequest> findByAssetIdAndHospitalIdOrderByRequestTimeDesc(int assetId, int hospitalId, Pageable pageable);

    public Page<V2_ServiceRequest> findByRequestorIdAndStatusOrderByRequestTimeDesc(Integer userId, int status, Pageable pageable);

    public Page<V2_ServiceRequest> findByFromDeptIdAndStatusOrderByRequestTimeDesc(Integer orgId, int status, Pageable pageable);

    public Page<V2_ServiceRequest> findByAssetIdAndStatusOrderByRequestTimeDesc(int assetId, int status, Pageable pageable);

    public Page<V2_ServiceRequest> findByAssetIdOrderByRequestTimeDesc(int assetId, Pageable pageable);

    public Page<V2_ServiceRequest> findByAssetId(Integer assetId, Pageable pr);

    public List<V2_ServiceRequest> findByAssetIdAndStatus(int assetId, int srStatus);



}
