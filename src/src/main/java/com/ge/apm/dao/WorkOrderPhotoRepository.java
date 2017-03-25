package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrderPhoto;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface WorkOrderPhotoRepository extends GenericRepository<WorkOrderPhoto> {
    
    public List<WorkOrderPhoto> findByWorkOrderId(Integer workOrderId);


}
