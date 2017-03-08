/*
 */
package com.ge.apm.service.analysis;

import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.dao.AssetSummitRepository;
import com.ge.apm.domain.AssetSummit;
import com.ge.apm.pojo.AssetClinicalRecordPojo;
import com.jcraft.jsch.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import org.joda.time.DateTime;
import webapp.framework.broker.SiBroker;
import webapp.framework.util.TimeUtil;

/**
 *
 * @author 212547631
 */
@Component
public class AssetExamDataAggregator {
    @Autowired
    AssetClinicalRecordRepository acrr;
    @Autowired
    AssetSummitRepository asmr;
    /*以examdate,siteid,hospitalid,assetid聚合clinical_record的priceAmount等数据，
    并更新至asset_summit的revenue
    PostgreSQL的update不支持双表语法, 使用两个hashmap来减少嵌套for循环的关联数据的查询
    */
    public void aggregateExamData(){
        {
            HashMap<String,AssetClinicalRecordPojo> hm1= new HashMap<String,AssetClinicalRecordPojo>();
            HashMap<String,AssetSummit> hm2= new HashMap<String,AssetSummit>();
            List<AssetClinicalRecordPojo> acrplist = acrr.getAssetExamDataAggregator();
            //初始化hm1 key
            for(AssetClinicalRecordPojo accr:acrr.getAssetExamDataAggregator()){
                hm1.put(accr.getAssetIds()+"-"+accr.getHospitalIds()+"-"+accr.getSiteIds(),accr);
            }
            //  List<AssetSummit> asmList = asmr.getAssetSummit();
            List<AssetSummit> asmupdateList= new ArrayList<AssetSummit>();
            //初始化hm2 key
            for(AssetSummit asm :asmr.getAssetSummit()){
                hm2.put(asm.getAssetId()+"-"+asm.getHospitalId()+"-"+asm.getSiteId(),asm);
            }
            //hm1 是 clinical_record表 hm2是_summit表  两个key都为assetid-hospitalid-siteid
            Iterator iter =hm1.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                if(hm2.containsKey(key)){
                    AssetClinicalRecordPojo accrp = hm1.get(key);
                    AssetSummit asm = hm2.get(key);
                    asm.setRevenue(accrp.getPriceAmounts());
                    asm.setExposeCount(accrp.getExposeCounts());
                    asm.setFilmCount(accrp.getFilmCounts());
                    asm.setInjectCount(accrp.getInjectCounts());
                    asmupdateList.add(asm);
                }
            }
            asmr.save(asmupdateList);
        }
    }

    public void aggregateExamDataByAssetId(int assetId){
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
            //params.put("theDate", TimeUtil.toString(dtFrom.toDate(), 0, "yyyy-MM-dd"));
            params.put("theDate", dtFrom.toDate());
            SiBroker.sendMessageWithHeaders("direct:initAssetAggregationDataByDateRange", null, params);
            dtFrom = dtFrom.plusDays(1);
        }
        
    }
}
