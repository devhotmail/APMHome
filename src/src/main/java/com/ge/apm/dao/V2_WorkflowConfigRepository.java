package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkflowConfig;

import webapp.framework.dao.GenericRepository;

public interface V2_WorkflowConfigRepository extends GenericRepository<V2_WorkflowConfig> {

	V2_WorkflowConfig findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(String tenantUID, String institutionUID,String hospitalUID, String siteUID);
	
}
