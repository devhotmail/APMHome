package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.UpdateProvider;

import com.ge.apm.dao.mapper.provider.AssetCostStatisticsProvider;
import com.ge.apm.domain.AssetCostStatistics;

public interface AssetCostStatisticsMapper {
	
	@UpdateProvider(type = AssetCostStatisticsProvider.class,method = "updateAssetCostStatistics")
	public Integer updateAssetCostStatistics(AssetCostStatistics assetCostStatistics);
}
