package com.ge.apm.service.analysis;

import com.ge.apm.domain.BatchAssetExam;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lsg on 7/3/2017.
 */
public class AssetExamDataService {


    @Autowired
    AssetExamDataAggregator assetExamDataAggregator;

    //全量聚合
    public String assetExamAggregator(){
        assetExamDataAggregator.aggregateExamData();
        return "success";
    }
    //按当天的聚合
    public String assetExamAggregatorByday(BatchAssetExam batchAssetExam){
        if(batchAssetExam == null){
            return "illegal param";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(batchAssetExam.getCalDay());
            assetExamDataAggregator.aggregateExamDataByDay(date);
        } catch (Exception e) {
            return "failue";
        }
        return "success";
    }

    //按选定的日期聚合
    public String aggrateExamebyRange(BatchAssetExam batchAssetExam){
        if(batchAssetExam == null){
            return "illegal param";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date from = null;
        Date to = null;

        try {
            from = sdf.parse(batchAssetExam.getFrom());
            to = sdf.parse(batchAssetExam.getTo());
            assetExamDataAggregator.aggregateExamDataByRangeDate(from,to);
        } catch (Exception e) {
            return "failue";
        }
        return "success";
    }



}
