/*
 */
package com.ge.apm.service.analysis;

import com.ge.apm.dao.AssetClinicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212547631
 */
@Component
public class assetExamDataAggregator {
    @Autowired
    AssetClinicalRecordRepository examDao;
    
    public void aggregateExamData(){
        
    }
    
}
