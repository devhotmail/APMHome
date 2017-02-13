package com.ge.apm.dao;

import com.ge.apm.domain.ProcedureNameMaping;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface ProcedureNameMapingRepository extends GenericRepository<ProcedureNameMaping> {
    public List<ProcedureNameMaping> getBySiteIdAndHospitalIdAndRisProcedureName(int siteId, int hospitalId, String risProcedureName);
}
