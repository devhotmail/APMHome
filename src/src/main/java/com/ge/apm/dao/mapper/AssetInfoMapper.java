package com.ge.apm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;

import com.ge.apm.dao.mapper.provider.AssetInfoProvider;
import com.ge.apm.domain.AssetCostStatistics;
import com.ge.apm.domain.DownTimeAsset;

public interface AssetInfoMapper {

	@SelectProvider(type = AssetInfoProvider.class,method="fetchAssetInfo")
	public List<AssetCostStatistics> fetchAssetInfo();

	@SelectProvider(type = AssetInfoProvider.class,method="fetchDownTimeAsset")
	public List<DownTimeAsset> fetchDownTimeAsset();
	
	@SelectProvider(type = AssetInfoProvider.class,method="fetchAssetCostStatisticsByAssetId")
	public AssetCostStatistics fetchAssetCostStatisticsByAssetId(Integer assetId);
	
}
