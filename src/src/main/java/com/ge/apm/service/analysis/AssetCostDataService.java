package com.ge.apm.service.analysis;

import com.ge.apm.dao.mapper.*;
import com.ge.apm.domain.AssetCostStatistics;
import com.ge.apm.domain.BatchAssetCost;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
	private static final int DAY = 1;
	
	@Autowired
	AssetInfoMapper assetInfoMapper;
	
	@Autowired
	WorkOrderMapper workOrderMapper;
	
	@Autowired
	AssetDepreciationMapper assetDepreciationMapper;
	
	@Autowired
	AssetCostStatisticsMapper assetCostStatisticsMapper;
	
	@Autowired
	AssetExamDataAggregator assetExamDataAggregator;

	/***
	 * 批量执行任务
	 */
	@Transactional
	public String aggregateCostData(){
		List<AssetCostStatistics> acss =  assetInfoMapper.fetchAssetInfo();
		if(CollectionUtils.isEmpty(acss)){
			logger.error("acss is empty,today is {}",new DateTime());
			return "failure";
		}
		logger.info("assetInfos size is {}",acss.size());
		try{
			for (AssetCostStatistics acs : acss) {
				AssetCostStatistics assetCostStatistics = assetInfoMapper.fetchAssetCostStatisticsByAssetId(acs.getAssetId(),new Date());
				assetCostStatistics.setDay(new Date());
				excuteTaskByAsset(assetCostStatistics);
			}
			return "success";
		}catch(Exception e){
			logger.error("aggregateCostData error,msg is {}",e.getMessage());
			return "failure";
		}
	}
	
	/***
	 * 	按天批量执行任务
	 */
	@Transactional
	public void aggregateCostDataByDay(Date day){
		if(day == null){
			day = new Date();
		}
		List<AssetCostStatistics> acss =  assetInfoMapper.fetchAssetInfoByDay(day);
		if(CollectionUtils.isEmpty(acss)){
			logger.error("acss is empty,today is {}",new SimpleDateFormat("yyyy-MM-dd").format(day));
			return;
		}
		logger.info("assetInfos size is {}",acss.size());
		for (AssetCostStatistics acs : acss) {
			//1、查询资产基本信息
			AssetCostStatistics assetCostStatistics = assetInfoMapper.fetchAssetCostStatisticsByAssetId(acs.getAssetId(),day);
			assetCostStatistics.setDay(day);
			excuteTaskByAsset(assetCostStatistics);
		}
	}
	
	/***
	 * 根据assetId获取执行单元
	 * @param assetId
	 */
	@Transactional
	public void aggregateCostDataByAssetId(Integer assetId,Date day){
		AssetCostStatistics assetCostStatistics = 	assetInfoMapper.fetchAssetCostStatisticsByAssetId(assetId,day);
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
		//2、计算宕机时间
		Date confirmDownTime = assetCostStatistics.getConfirmedDownTime();
		Date requestTime =assetCostStatistics.getRequestTime();
		if(assetCostStatistics.getStatus().intValue() == ASSET_STATUS_DOWN){
			DateTime nowTime =new DateTime();
			DateTime endOfDay = nowTime.secondOfDay().withMaximumValue();
			if(confirmDownTime != null){
				DateTime dt = new DateTime(confirmDownTime);
				if(Days.daysBetween(dt, endOfDay).getDays() >= 1){
					assetCostStatistics.setDownTime(ONE_DAY);
				}else{
					assetCostStatistics.setDownTime(Seconds.secondsBetween(dt, endOfDay).getSeconds());
				}
			}else{
				DateTime dt = new DateTime(requestTime);
				if(Days.daysBetween(dt, endOfDay).getDays() >= 1){
					assetCostStatistics.setDownTime(ONE_DAY);
				}else{
					assetCostStatistics.setDownTime(Seconds.secondsBetween(dt, endOfDay).getSeconds());
				}
			}
		}else{
			assetCostStatistics.setDownTime(0);
		}
		//当天的新建工单数
		assetCostStatistics.setWorkOrderCount(workOrderMapper.fetchWorkOrdersByAssetId(assetCostStatistics.getAssetId(),assetCostStatistics.getDay()));
		
    	//3、计算维修费用
		assetCostStatistics.setMaintenanceCost(workOrderMapper.fetchWorkOrderCost(assetCostStatistics.getAssetId(),assetCostStatistics.getDay()));
		
    	//4、计算折旧费用
		assetCostStatistics.setDeprecationCost(calAssetDepreciation(assetCostStatistics.getAssetId()));
		if(logger.isDebugEnabled()){
			logger.debug("current assetCostStatistics is {}",assetCostStatistics);
		}
		assetCostStatisticsMapper.updateAssetCostStatistics(assetCostStatistics);
	}

	private Double calAssetDepreciation(Integer assetId) {
		Double sum = assetDepreciationMapper.fetchAssetDepreciationByAssetId(assetId);
		DateTime first = new DateTime(new Date());
		int days = first.dayOfMonth().getMaximumValue();
		return sum == null ? 0 : sum/days;
	}
	
	@Transactional
	public String calByDay(BatchAssetCost bac){
		try{
			if(bac == null){
				return "illegal param";
			}
			if(bac.getIsAll()){
				Date day =new SimpleDateFormat("yyyy-MM-dd").parse(bac.getCalDay());
				aggregateCostDataByDay(day);
				return "success";
			}
			List<Integer> assetIds = bac.getAssetIds();
			Date day =new SimpleDateFormat("yyyy-MM-dd").parse(bac.getCalDay());
			for(int assetId : assetIds){
				aggregateCostDataByAssetId(assetId,day);
			}
			return "success";
		}catch(Exception e){
			logger.error("calByDay error,param is {}",bac);
			logger.error("calByDay error,message is {}",e.getMessage());
			return "failue";
		}
	}
	
	@Transactional
	public String calByFromTo(BatchAssetCost bac){
		if(bac == null){
			return "illegal param";
		}
		if(StringUtils.isEmpty(bac.getFrom()) || StringUtils.isEmpty(bac.getTo())){
			return "from or to is empty !";
		}
		try{
			DateTime from = new DateTime(bac.getFrom());
			DateTime to = new DateTime(bac.getTo());
			assetExamDataAggregator.initAssetAggregationDataByDateRange(from.toDate(), to.toDate());
			while(from.isBefore(to)||from.isEqual(to)){
				logger.info("calByFromTo begin ,current day is {}",from.toString());
				aggregateCostDataByDay(from.toDate());
				from = from.plusDays(DAY);
			}
			return "success";
		}catch(Exception e){
			logger.error("calByFromTo error,param is {}",bac);
			logger.error("calByFromTo error,message is {}",e.getMessage());
			throw e;
		}
	}
}
