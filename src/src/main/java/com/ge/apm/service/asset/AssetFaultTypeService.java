package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetFaultTypeRepository;
import com.ge.apm.domain.AssetFaultType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lsg on 27/7/2017.
 */
@Component
public class AssetFaultTypeService {

    private final Logger log = LoggerFactory.getLogger(AssetFaultTypeService.class);

    @Autowired
    AssetFaultTypeRepository assetFaultTypeRepository;
    public List<AssetFaultType> getFaultTypeList(int astypeId)  {
        List<AssetFaultType> byAssetGroupId = assetFaultTypeRepository.getByAssetGroupId(astypeId);
        if(CollectionUtils.isEmpty(byAssetGroupId)){
            //如果设备错误类型未定义过，默认取-1
            byAssetGroupId = assetFaultTypeRepository.getByAssetGroupId(-1);
        }
        return byAssetGroupId;
    }
}
