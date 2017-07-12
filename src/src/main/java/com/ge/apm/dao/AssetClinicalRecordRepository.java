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


@Query("select new com.ge.apm.pojo.AssetClinicalRecordPojo(siteId as siteIds,hospitalId as hospitalIds,assetId as assetIds, examDate as examDate, procedureId as procedureId,count(examDuration) as examCount) from AssetClinicalRecord group by examDate,siteId,hospitalId,assetId, procedureId having exam_date = :date")
public List<AssetClinicalRecordPojo> aggregateForExamSummitByDate(@Param("date") Date date);


}

/* native sql for  aggregateForExamSummitByDate:
 select exam_Date,site_Id,hospital_Id,asset_Id, procedure_Id,count(exam_duration) as exam_count from asset_clinical_record group by exam_Date,site_Id,hospital_Id,asset_Id, procedure_Id having exam_date='2016-01-18'*/

/*navtive sql for getAssetExamDataAggregatorByDate: select site_Id as siteIds,hospital_Id as hospitalIds,asst_Id as assetIds, exam_Date as examDate,
            count(exam_Duration) as examCount,
            sum(exam_Duration) as examDurations ,
            sum(price_Amount) as priceAmounts ,
            sum(inject_Count) as injectsCounts ,
            sum(expose_Count) as exposeCounts,
            sum(film_Count) as filmCounts
            from Asset_Clinical_Record  acr
            GROUP BY acr.exam_Date,acr.site_Id,acr.hospital_Id,acr.asset_Id
            having exam_date = '2017-07-06'*/



