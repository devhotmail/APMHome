package com.ge.apm.dao;

import com.ge.apm.domain.InspectionChecklist;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface InspectionChecklistRepository extends GenericRepository<InspectionChecklist> {
    @Query("select i from InspectionChecklist i, AssetInfo a where a.id=i.assetId and a.hospitalId=?1 and i.checklistType=?2")
    public List<InspectionChecklist> getByHospitalIdAndChecklistType(int hospitalId, int checklistType);

}
