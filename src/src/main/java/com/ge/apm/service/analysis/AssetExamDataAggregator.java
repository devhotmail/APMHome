/*
 */
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
        List<AssetSummit> asmUpdateList= new ArrayList<AssetSummit>();
        for(AssetClinicalRecordPojo accrp:acrpList){
            AssetSummit asm = new AssetSummit();
            asm.setCreated(date);
            asm.setHospitalId(accrp.getHospitalIds());
            asm.setAssetId(accrp.getAssetIds());
            asm.setExposeCount(accrp.getExposeCounts());
            asm.setFilmCount(accrp.getFilmCounts());
            asm.setInjectCount(accrp.getInjectCounts());
            asm.setExamCount(accrp.getExamCount().intValue());
            asmUpdateList.add(asm);
        }
       // assetSummitRepository.save(asmUpdateList);
        return "success";

    }


    public void test(){
        DateTime dtFrom = TimeUtil.timeNow().minusDays(100);
        DateTime dtTo = dtFrom.plusDays(5);

        initAssetAggregationDataByDateRange(dtFrom.toDate(), dtTo.toDate());
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
