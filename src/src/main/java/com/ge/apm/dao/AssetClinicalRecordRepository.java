package com.ge.apm.dao;

import com.ge.apm.domain.AssetClinicalRecord;
import com.ge.apm.pojo.AssetClinicalRecordPojo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface AssetClinicalRecordRepository extends GenericRepository<AssetClinicalRecord> {

    @Query("select new com.ge.apm.pojo.AssetClinicalRecordPojo(" +
            "siteId as siteIds,hospitalId as hospitalIds,assetId as assetIds," +
            "sum(examDuration) as examDurations ," +
            "sum(priceAmount) as priceAmounts ," +
            "sum(injectCount) as injectsCounts ," +
            "sum(exposeCount) as exposeCounts," +
            "sum(filmCount) as filmCounts  ) " +
            "from AssetClinicalRecord  acr " +
            "GROUP BY acr.examDate,acr.siteId,acr.hospitalId,acr.assetId ")
    public List<AssetClinicalRecordPojo> getAssetExamDataAggregator();

}


/*sql
* select count(*),acr.exam_date,
 sum(price_amount) as priceamounts,
 sum(inject_Count) as injectsCountssum, sum(film_Count) as filmCounts
from asset_clinical_record acr GROUP BY acr.exam_date,acr.site_id,acr.hospital_id,acr.asset_id
* */


