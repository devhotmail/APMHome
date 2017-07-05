package com.ge.apm.dao;

import com.ge.apm.domain.AssetClinicalRecord;
import com.ge.apm.pojo.AssetClinicalRecordPojo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.Date;
import java.util.List;

public interface AssetClinicalRecordRepository extends GenericRepository<AssetClinicalRecord> {

    //全量聚合
    @Query("select new com.ge.apm.pojo.AssetClinicalRecordPojo(" +
            "siteId as siteIds,hospitalId as hospitalIds,assetId as assetIds, examDate as examDate, " +
            "count(examDuration) as examCount, " +
            "sum(examDuration) as examDurations ," +
            "sum(priceAmount) as priceAmounts ," +
            "sum(injectCount) as injectsCounts ," +
            "sum(exposeCount) as exposeCounts," +
            "sum(filmCount) as filmCounts  ) " +
            "from AssetClinicalRecord  acr " +
            "GROUP BY acr.examDate,acr.siteId,acr.hospitalId,acr.assetId ")
    public List<AssetClinicalRecordPojo> getAssetExamDataAggregator();

    //按指定日期聚合
    @Query("select new com.ge.apm.pojo.AssetClinicalRecordPojo(" +
            "siteId as siteIds,hospitalId as hospitalIds,assetId as assetIds, examDate as examDate," +
            "count(examDuration) as examCount," +
            "sum(examDuration) as examDurations ," +
            "sum(priceAmount) as priceAmounts ," +
            "sum(injectCount) as injectsCounts ," +
            "sum(exposeCount) as exposeCounts," +
            "sum(filmCount) as filmCounts  ) " +
            "from AssetClinicalRecord  acr " +
            "GROUP BY acr.examDate,acr.siteId,acr.hospitalId,acr.assetId "+
            "having exam_date >= :from and exam_date <= :to"
    )
    public List<AssetClinicalRecordPojo> aggreateAssetByRange(@Param("from") Date date1, @Param("to") Date date2);


    //按当天聚合
    @Query("select new com.ge.apm.pojo.AssetClinicalRecordPojo(" +
            "siteId as siteIds,hospitalId as hospitalIds,assetId as assetIds, examDate as examDate, " +
            "count(examDuration) as examCount," +
            "sum(examDuration) as examDurations ," +
            "sum(priceAmount) as priceAmounts ," +
            "sum(injectCount) as injectsCounts ," +
            "sum(exposeCount) as exposeCounts," +
            "sum(filmCount) as filmCounts  ) " +
            "from AssetClinicalRecord  acr " +
            "GROUP BY acr.examDate,acr.siteId,acr.hospitalId,acr.assetId "+
            "having exam_date = :date "
    )
    public List<AssetClinicalRecordPojo> getAssetExamDataAggregatorByDate(@Param("date") Date date);



}

/*navtive sql for getAssetExamDataAggregatorByDate
* select site_Id as siteIds,hospital_Id as hospitalIds,asset_Id as assetIds, exam_Date as examDate,
            count(exam_Duration) as examCount,
            sum(exam_Duration) as examDurations ,
            sum(price_Amount) as priceAmounts ,
            sum(inject_Count) as injectsCounts ,
            sum(expose_Count) as exposeCounts,
            sum(film_Count) as filmCounts
            from Asset_Clinical_Record  acr
            GROUP BY acr.exam_Date,acr.site_Id,acr.hospital_Id,acr.asset_Id
            having exam_date = '2017-07-06'*/

//    @Query("select asm from AssetSummit asm where asm.siteId = :siteId and " +
//            "asm.hospitalId = :hospitalId and asm.assetId = :assetId")
//    public List<AssetSummit> getAssetSummit(@Param("assetId") int assetId, @Param("hospitalId") int hospitalId, @Param("siteId") int siteId);

/*sql
* select count(*),acr.exam_date,
 sum(price_amount) as priceamounts,
 sum(inject_Count) as injectsCountssum, sum(film_Count) as filmCounts
from asset_clinical_record acr GROUP BY acr.exam_date,acr.site_id,acr.hospital_id,acr.asset_id
* */


