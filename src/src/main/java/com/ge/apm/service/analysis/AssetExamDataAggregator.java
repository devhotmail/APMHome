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
    @Autowired
    AssetClinicalRecordRepository assetClinicalRecordRepository;
    @Autowired
    AssetSummitRepository assetSummitRepository;

    /*以examdate,siteid,hospitalid,assetid聚合clinical_record的priceAmount等数据，
      并更新至asset_summit的revenue
      PostgreSQL的update不支持双表语法, 使用两个hashmap来减少嵌套for循环的关联数据的查询
      */
    public String operatorAggregator(List<AssetClinicalRecordPojo> acrpList){
        HashMap<String,AssetClinicalRecordPojo> hmRepo= new HashMap<String,AssetClinicalRecordPojo>();
        HashMap<String,AssetSummit> hmSumit= new HashMap<String,AssetSummit>();
        //初始化hmRepo key
        List<AssetSummit> summitList=assetSummitRepository.getAssetSummit();
        for(AssetClinicalRecordPojo accr:acrpList){
            hmRepo.put(accr.getAssetIds()+"-"+accr.getHospitalIds()+"-"+accr.getSiteIds()+":"+accr.getExamDate(),accr);
        }

        //初始化hm2 key
        for(AssetSummit asm :summitList){
            hmSumit.put(asm.getAssetId()+"-"+asm.getHospitalId()+"-"+asm.getSiteId()+":"+asm.getCreated(),asm);
        }

        //hmRepo 是 clinical_record表 hmSumit是_summit表索引  两个key都为assetid-hospitalid-siteid
        try {
            List<AssetSummit> asmupdateList= new ArrayList<AssetSummit>();
            Iterator iter =hmRepo.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                if(hmSumit.containsKey(key)){
                    AssetClinicalRecordPojo accrp = hmRepo.get(key);
                    AssetSummit asm = hmSumit.get(key);
                    asm.setRevenue(accrp.getPriceAmounts());
                    asm.setExposeCount(accrp.getExposeCounts());
                    asm.setFilmCount(accrp.getFilmCounts());
                    asm.setInjectCount(accrp.getInjectCounts());
                    asmupdateList.add(asm);
                }
            }
            assetSummitRepository.save(asmupdateList);
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
        return "success";
    }


    public String aggregateExamData(){
            List<AssetClinicalRecordPojo> acrpList = assetClinicalRecordRepository.getAssetExamDataAggregator();
            logger.info("Asset Clinical Record size {}",acrpList.size());
            operatorAggregator(acrpList);
        return "success";
    }

    public void aggregateExamDataByAssetId(int assetId){
    }

    public void aggregateExamDataByRangeDate(Date from,Date to) {
        List<AssetClinicalRecordPojo> acrpList = assetClinicalRecordRepository.aggreateAssetByRange(from,to);
    }

    public void aggregateExamDataByDay(Date date) {
        List<AssetClinicalRecordPojo> acrpList = assetClinicalRecordRepository.getAssetExamDataAggregatorByDate(date);
        operatorAggregator(acrpList);

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

        while(dtFrom.isBefore(dtTo)){
            params.put("theDate", TimeUtil.toString(dtFrom.toDate(), 0, "yyyy-MM-dd"));
            SiBroker.sendMessageWithHeaders("direct:initAssetAggregationDataByDateRange", null, params);
            dtFrom = dtFrom.plusDays(1);
        }

    }
}
