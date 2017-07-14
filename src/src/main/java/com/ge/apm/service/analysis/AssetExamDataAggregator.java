package com.ge.apm.service.analysis;

import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetSummitRepository;
import com.ge.apm.dao.ExamSummitRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetSummit;
import com.ge.apm.domain.ExamSummit;
import com.ge.apm.pojo.AssetClinicalRecordPojo;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.broker.SiBroker;
import webapp.framework.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author 212547631
 */
@Component
public class AssetExamDataAggregator {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final int DAY = 1;
    @Autowired
    AssetClinicalRecordRepository assetClinicalRecordRepository;
    @Autowired
    AssetSummitRepository assetSummitRepository;
    @Autowired
    ExamSummitRepository examSummitRepository;
    @Autowired
    AssetInfoRepository assetInfoRepository;

    /*该方法会由route id=aggregationAssetExamData来调用*/
    public String aggregateExamData() throws Exception{
        logger.info("start to aggregateExamData");
        Date currentDate = new Date();
        aggregateForAssetSumit(currentDate);
        return "success";
    }

    public String aggregateExamSummit() throws Exception{
        logger.info("start to aggregateForAssetSumit");
        Date currentDate = new Date();
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate= sdf.parse("2017-02-18");*/
        aggregateForExamSumit(currentDate);
        return "success";
    }

    @Transactional
    public String aggregateForExamSumit(Date date)throws Exception{

        List<AssetClinicalRecordPojo> acrpList = assetClinicalRecordRepository
                .aggregateForExamSummitByDate(date);
        List<ExamSummit> asmList= new ArrayList<ExamSummit>();
        List<ExamSummit> newAsmList= new ArrayList<ExamSummit>();
        for(AssetClinicalRecordPojo accrp:acrpList){
            ExamSummit asm1 =examSummitRepository.getExamSummitByAssetIdAndCreated(accrp.getAssetIds(),accrp.getHospitalIds(),accrp.getProcedureId(),accrp.getSiteIds(), date);
            if(asm1!=null){
                logger.info(accrp.getAssetIds() +"-already in the exam_summit,only updated for the time being on date "+date);
                extractorExamSummit(date, asmList, accrp, asm1);
            }else {
                ExamSummit newAsm = new ExamSummit();
                logger.info(accrp.getAssetIds() +" is new record that is to be inserted into exam_summit on date "+date);
                extractorExamSummit(date, newAsmList, accrp, newAsm);
            }
        }
        if(asmList.size()>0) {
            logger.info("how many records have already been in exam_summit--> "+asmList.size());
            examSummitRepository.save(asmList);
        }
        if(newAsmList.size()>0) {
            logger.info("how many new records to be inserted into exam_summit--> "+newAsmList.size());
            examSummitRepository.save(newAsmList);
        }
return "success";
    }

    @Transactional
    public String aggregateForAssetSumit(Date date) throws Exception {
        List<AssetClinicalRecordPojo> acrpList = assetClinicalRecordRepository.getAssetExamDataAggregatorByDate(date);
        logger.info("Asset Clinical Record Aggregator records size-->"+acrpList.size());
         List<AssetSummit> asmList= new ArrayList<AssetSummit>();
        List<AssetSummit> newAsmList= new ArrayList<AssetSummit>();
        for(AssetClinicalRecordPojo accrp:acrpList){
            AssetSummit asm1 =assetSummitRepository.getAssetSummitByAssetIdAndCreated(accrp.getAssetIds(),date);
            //如果某资产已经在assumit中录入过了 ，那就只要更新该资产相对应的inject_count等信息
            if(asm1!=null){
                logger.info(accrp.getAssetIds() +"-already in the asset_summit,only updated for the time being on date "+date);
                extractor(date, asmList, accrp, asm1);
            }else {
                AssetSummit newAsm = new AssetSummit();
                logger.info(accrp.getAssetIds() +" is new record that is to be inserted into asset_summit on date "+date);
                extractor(date, newAsmList, accrp, newAsm);
            }
        }
        if(asmList.size()>0) {
            logger.info("how many records  have already been asset_summit--> "+asmList.size());
             assetSummitRepository.save(asmList);
        }
        if(newAsmList.size()>0) {
            logger.info("how many new records to be inserted into asset_summit--> "+newAsmList.size());
            assetSummitRepository.save(newAsmList);
        }
        return "success";

    }
    //在将asset clininical中聚合的数据写到assetsummit中去，将数据放到列表里面
    private void extractor(Date date, List<AssetSummit> asmList, AssetClinicalRecordPojo accrp, AssetSummit asm1) {
        asm1.setCreated(date);//gl: created这个字段是做聚合那天的日期?
        asm1.setAssetId(accrp.getAssetIds());
        asm1.setSiteId(accrp.getSiteIds());
        asm1.setHospitalId(accrp.getHospitalIds());
        asm1.setExposeCount(accrp.getExposeCounts());
        asm1.setFilmCount(accrp.getFilmCounts());
        asm1.setInjectCount(accrp.getInjectCounts());
        if(accrp.getExamCount()==null){
            logger.info("gl: accrp.getExamCount() should not be null");
        }else {
            asm1.setExamCount(accrp.getExamCount().intValue());
        }
        if(accrp.getExamDurations()==null){
            logger.info("gl: accrp.getExamDurations() should not be null");
        }else {
            asm1.setExamDuration(accrp.getExamDurations().intValue());
        }
        if(accrp.getPriceAmounts()==null){
            logger.info("gl: accrp.getPriceAmounts() should not be null");
        }else {
            asm1.setRevenue(Math.round(accrp.getPriceAmounts() * 100.0) / 100.0);
        }
        asmList.add(asm1);
    }
    private void extractorExamSummit(Date date, List<ExamSummit> asmList, AssetClinicalRecordPojo accrp, ExamSummit asm1) {
     AssetInfo assinfo = assetInfoRepository.findById(accrp.getAssetIds());
        asm1.setCreated(accrp.getExamDate());
        asm1.setAssetId(accrp.getAssetIds());
        asm1.setSiteId(accrp.getSiteIds());
        asm1.setHospitalId(accrp.getHospitalIds());
        asm1.setPartId(accrp.getProcedureId());
        asm1.setSubPartId(1);
        asm1.setStepId(1);
        if(assinfo==null){
            System.out.println("gl: accrp.getAssetIds() is null not allowed"+accrp.getAssetIds());
        }
        asm1.setDeptId(assinfo.getClinicalDeptId());
        asm1.setAssetGroup(assinfo.getAssetGroup());
        if(accrp.getExamCount()==null){
            logger.info("gl: accrp.getExamCount() should not be null");
        }else {
            asm1.setExamCount(accrp.getExamCount().intValue());
        }

        asmList.add(asm1);
    }
    public void initAssetAggregationDataByDateRange(Date fromDate, Date toDate){
        HashMap<String, Object> params = new HashMap<String, Object>();
        DateTime dtFrom = TimeUtil.toJodaDate(fromDate);
        DateTime dtTo = TimeUtil.toJodaDate(toDate).plusDays(1);
       /* Date from = sdf.parse("2014-11-10");
        Date to = sdf.parse("2015-09-18");*/
        while(dtFrom.isBefore(dtTo)){
            params.put("theDate", TimeUtil.toString(dtFrom.toDate(), 0, "yyyy-MM-dd"));
            //gl:route_main.xml
            SiBroker.sendMessageWithHeaders("direct:initAssetAggregationDataByDateRange", null, params);
            dtFrom = dtFrom.plusDays(1);
        }

    }
}