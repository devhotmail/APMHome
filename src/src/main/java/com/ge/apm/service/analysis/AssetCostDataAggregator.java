/*
 */
package com.ge.apm.service.analysis;

import com.ge.apm.dao.AssetDepreciationRepository;
import com.ge.apm.dao.WorkOrderStepDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212547631
 */
@Component
public class AssetCostDataAggregator {
    @Autowired
    AssetDepreciationRepository depreciationDao;

    @Autowired
    WorkOrderStepDetailRepository woStepDetailDao;
    
    public void aggregateCostData(){
        
    }
    
    public void aggregateCostDataByAssetId(int assetId){
    }
}
