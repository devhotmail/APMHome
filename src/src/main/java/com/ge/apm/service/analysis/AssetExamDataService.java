package com.ge.apm.service.analysis;

import com.ge.apm.dao.AssetClinicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by lsg on 7/3/2017.
 */
public class AssetExamDataService {
    @Autowired
    AssetClinicalRecordRepository acrr;
    //按当天的聚合


    //按选定的日期聚合
    public void aggrateExamebyDate(Date date1 ,Date date2){
       //acrr
    }


    //全量聚合
    public void aggrateExame(){
        acrr.getAssetExamDataAggregator();
    }
}
