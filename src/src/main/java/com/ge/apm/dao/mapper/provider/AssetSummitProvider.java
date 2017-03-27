package com.ge.apm.dao.mapper.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetSummitProvider {
	Logger logger = LoggerFactory.getLogger(getClass());

	public String fetchAssetSummit(){
		StringBuilder sb = new StringBuilder();
		sb.append("select max(sub.np) as npMax,min(sub.np) as npMin ,max(sub.ic) as icMax,min(sub.ic) as icMin, max(sub.ec) as ecMax, min(sub.ec) as ecMin,max(sub.fm) as fmMax,min(sub.fm) as fmMin from(select sum(revenue-maintenance_cost-deprecation_cost) as np, sum(inject_count) as ic,sum(expose_count) as ec ,sum(film_count) as fm from asset_summit group by asset_Id,created) as sub");

		return sb.toString();
	}

	
}
