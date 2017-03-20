package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.Select;

public interface AssetDepreciationMapper {
	@Select("select sum(deprecate_amount) from asset_depreciation where asset_id=#{assetId} "
			+ "and deprecate_date=date(date_trunc('month',now())) group by deprecate_date")
	public Double fetchAssetDepreciationByAssetId(Integer assetId);
}