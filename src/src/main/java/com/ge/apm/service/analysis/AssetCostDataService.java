package com.ge.apm.service.analysis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ge.apm.dao.mapper.AssetDepreciationMapper;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.WorkOrderMapper;
import com.ge.apm.domain.AssetCostStatistics;
import com.ge.apm.domain.DownTimeAsset;

/***
 * 计算资产的宕机时间和成本
 * @author 212593079
 *
 */
@Component
public class AssetCostDataService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final int ASSET_STATUS_DOWN = 2;
	private static final int ONE_DAY = 24 * 60 * 60;
	
	@Autowired
	AssetInfoMapper assetInfoMapper;
	
	@Autowired
	WorkOrderMapper workOrderMapper;
	
	@Autowired
	AssetDepreciationMapper assetDepreciationMapper;
	
	/***
	 * 批量执行任务
	 */
	public void aggregateCostData(){
		// 1、获取资产名称
		List<AssetCostStatistics> assetInfos =  assetInfoMapper.fetchAssetInfo();
    	if(CollectionUtils.isEmpty(assetInfos)){
			logger.error("assetInfos is empty");
			return;
    	}
    	logger.info("assetInfos size is {}",assetInfos.size());
    	// 查出当天所有宕机资产，找出最早的一条，然后以该条记录的时间计算宕机时间
    	List<DownTimeAsset> downTimeAsset = assetInfoMapper.fetchDownTimeAsset();
    	Map<Integer,DownTimeAsset> map = new HashMap<Integer,DownTimeAsset>();
    	if(CollectionUtils.isNotEmpty(downTimeAsset)){
    		for (DownTimeAsset dta : downTimeAsset) {
				map.put(dta.getAssetId(), dta);
			}
    	}
    	for (AssetCostStatistics acs : assetInfos) {
    		if(map.containsKey(acs.getAssetId())){
    			if(map.get(acs.getAssetId()).getIsCal()){
    				continue;
    			}else{
    				acs.setRequestTime(map.get(acs.getAssetId()).getRequestTime());
    				acs.setConfirmedDownTime(map.get(acs.getAssetId()).getDownTime());
    			}
    		}
    		excuteTaskByAsset(acs);
    		if(map.containsKey(acs.getAssetId())){
    			DownTimeAsset dta = map.get(acs.getAssetId());
    			dta.setIsCal(true);
    			map.put(acs.getAssetId(), dta);
    		}
    	}
	}
	
	/***
	 * 根据assetId获取执行单元
	 * @param assetId
	 */
	public void aggregateCostDataByAssetId(Integer assetId){
		AssetCostStatistics assetCostStatistics = 	assetInfoMapper.fetchAssetCostStatisticsByAssetId(assetId);
		if(assetCostStatistics == null){
			logger.error("can not find AssetCostStatistics by assetId,assetId is {}",assetId);
			return;
		}
		excuteTaskByAsset(assetCostStatistics);
	}
	
	/***
	 * 计算单个资产
	 * @param assetCostStatistics
	 */
	public void excuteTaskByAsset(AssetCostStatistics assetCostStatistics){
    	//1、查询资产
    	//2、计算宕机时间
		
		Date confirmDownTime = assetCostStatistics.getConfirmedDownTime();
		Date requestTime =assetCostStatistics.getRequestTime();
		if(assetCostStatistics.getStatus().intValue() == ASSET_STATUS_DOWN){
			DateTime nowTime =new DateTime();
			DateTime endOfDay = nowTime.secondOfDay().withMaximumValue();
			if(confirmDownTime != null){
				DateTime dt = new DateTime(confirmDownTime);
				if(Days.daysBetween(endOfDay, dt).getDays()>1){
					assetCostStatistics.setDownTime(ONE_DAY);
				}else{
					assetCostStatistics.setDownTime(Seconds.secondsBetween(endOfDay, dt).getSeconds());
				}
			}else{
				DateTime dt = new DateTime(requestTime);
				if(Days.daysBetween(endOfDay, dt).getDays()>1){
					assetCostStatistics.setDownTime(ONE_DAY);
				}else{
					assetCostStatistics.setDownTime(Seconds.secondsBetween(endOfDay, dt).getSeconds());
				}
			}
		}else{
			assetCostStatistics.setDownTime(0);
		}
		//当天的新建工单数
		assetCostStatistics.setWorkOrderCount(workOrderMapper.fetchWorkOrdersByAssetId(assetCostStatistics.getAssetId()));
		
    	//3、计算维修费用
		assetCostStatistics.setMaintenanceCost(workOrderMapper.fetchWorkOrderCost(assetCostStatistics.getAssetId()));
		
    	//4、计算折旧费用
		assetCostStatistics.setDeprecationCost(calAssetDepreciation(assetCostStatistics.getAssetId()));
	}

	private Double calAssetDepreciation(Integer assetId) {
		Double sum = assetDepreciationMapper.fetchAssetDepreciationByAssetId(assetId);
		DateTime first = new DateTime(new Date());
		int days = first.dayOfMonth().getMaximumValue();
		return sum == null ? 0 : sum/days;
	}
	
}
