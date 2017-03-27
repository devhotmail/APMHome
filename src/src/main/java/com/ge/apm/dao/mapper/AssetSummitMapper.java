package com.ge.apm.dao.mapper;

import com.ge.apm.dao.mapper.provider.AssetSummitProvider;
import com.ge.apm.domain.AssetSummitMaxMinPojo;
import org.apache.ibatis.annotations.SelectProvider;

public interface AssetSummitMapper {

	@SelectProvider(type = AssetSummitProvider.class,method="fetchAssetSummit")
	public AssetSummitMaxMinPojo fetchAssetSummit();
	

	
}
