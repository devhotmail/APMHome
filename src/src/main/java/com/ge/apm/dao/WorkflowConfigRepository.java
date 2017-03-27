package com.ge.apm.dao;

import com.ge.apm.domain.WorkflowConfig;
import webapp.framework.dao.GenericRepository;

public interface WorkflowConfigRepository extends GenericRepository<WorkflowConfig> {
    public WorkflowConfig getBySiteIdAndHospitalId(Integer siteId,Integer hospitalId);
    public WorkflowConfig getByHospitalId(int hospitalId);
}
