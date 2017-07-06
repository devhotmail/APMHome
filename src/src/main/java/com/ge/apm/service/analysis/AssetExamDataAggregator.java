package com.ge.apm.service.analysis;

import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.dao.AssetSummitRepository;
import com.ge.apm.domain.AssetSummit;
import com.ge.apm.pojo.AssetClinicalRecordPojo;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.broker.SiBroker;
import webapp.framework.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    public String aggregateExamData(){
        List<AssetClinicalRecordPojo> acrpList = assetClinicalRecordRepository.getAssetExamDataAggregator();
        logger.info("Asset Clinical Record size {}",acrpList.size());

        return "success";
    }

    public void aggregateExamDataByAssetId(int assetId){
    }
    @Transactional
    public String aggregateExamDataByDay(Date date) throws Exception {
        List<AssetClinicalRecordPojo> acrpList = assetClinicalRecordRepository.getAssetExamDataAggregatorByDate(date);
        /*if(CollectionUtils.isEmpty(acrpList)){
            logger.error("acrpList is empty,today is {}",new DateTime());
            return "failure";
        }*/
        logger.info("gl: records from assetclincialRecords --->"+acrpList.size());
        List<AssetSummit> asmNewList= new ArrayList<AssetSummit>();
        List<AssetSummit> asmUpdateList= new ArrayList<AssetSummit>();
        for(AssetClinicalRecordPojo accrp:acrpList){
            AssetSummit asm1 =assetSummitRepository.getAssetSummitByAssetIdAndCreated(accrp.getAssetIds(),date);
            if(asm1!=null){
                //logger.info("-------"+accrp.getAssetIds()+"---"+date);
                logger.info(accrp.getAssetIds() +"---"+date + "need to be updated");
                asm1.setCreated(date);
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
                asmUpdateList.add(asm1);
            }else {
                AssetSummit asm = new AssetSummit();
                asm.setAssetId(accrp.getAssetIds());
                asm.setHospitalId(accrp.getHospitalIds());
                asm.setSiteId(accrp.getSiteIds());
                //logger.info("---insert----"+accrp.getAssetIds()+"---"+date);
                asm.setExposeCount(accrp.getExposeCounts());
                asm.setFilmCount(accrp.getFilmCounts());
                asm.setInjectCount(accrp.getInjectCounts());
                if(accrp.getExamCount()==null){
                    logger.info("gl: accrp.getExamCount() should not be null");
                }else {
                    asm.setExamCount(accrp.getExamCount().intValue());
                }
                if(accrp.getExamDurations()==null){
                    logger.info("gl: accrp.getExamDurations() should not be null");
                }else {
                    asm.setExamDuration(accrp.getExamDurations().intValue());
                }
                asm.setCreated(date);
                if(accrp.getPriceAmounts()==null){
                    logger.info("gl: accrp.getPriceAmounts() should not be null");
                }else {
                    asm.setRevenue(Math.round(accrp.getPriceAmounts() * 100.0) / 100.0);
                }
                asmNewList.add(asm);
            }
        }
        if(asmNewList.size()>0) {
            logger.info("gl: new records are inserted into, how many ?--> "+asmNewList.size());
             assetSummitRepository.save(asmNewList);
            /*for(AssetSummit asml :asmNewList) {
                assetSummitRepository.save(asml);
            }*/
        }

        if(asmUpdateList.size()>0){
            logger.info("gl: there are records to be updated, how many? --->"+asmUpdateList.size());
            assetSummitRepository.save(asmUpdateList);
            /*for(AssetSummit asm :asmUpdateList){
                logger.info(asm.getAssetId() +"--id-- "+asm.getCreated()+" --created-- "+asm.getRevenue() +" --- revenue --------ready to be updated");
                assetSummitRepository.updateAssetSummit(asm.getExposeCount(),asm.getInjectCount(),asm.getFilmCount(),asm.getId());
            }*/
        }
        return "success";

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