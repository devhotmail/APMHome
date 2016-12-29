package com.ge.apm.service.asset;

import java.math.BigDecimal;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.AssetDepreciationRepository;
import com.ge.apm.domain.AssetDepreciation;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.utils.TimeUtils;
import java.math.RoundingMode;

import webapp.framework.web.WebUtil;

@Component
public class AssetDepreciationService {
	public static final int AVERAGE = 1;//平均
	public static final int DOUBLE = 2;//加速双倍余额
	public static final int YEAR = 3;//加速年限
	public static final String FORMAT="yyyy-MM";
	private static final Logger logger = LoggerFactory.getLogger(AssetDepreciationService.class);
	private AssetDepreciationRepository assetDepreciationRepository ;
	/***
	 * 计算资产的月平均成本
	 */
	public Double calAssetDepreciation(AssetInfo assetInfo,Integer whichYear){
		BigDecimal  result = new BigDecimal(0);
		if(assetInfo == null || whichYear == null){
			return result.doubleValue();
		}
		if(assetInfo.getPurchasePrice() != null && assetInfo.getLifecycle() != null && assetInfo.getDepreciationMethod() != null){
			BigDecimal purchasePrice = new BigDecimal(assetInfo.getPurchasePrice());
			BigDecimal salvageValue;
			if(assetInfo.getSalvageValue() != null){
				salvageValue = new BigDecimal(assetInfo.getSalvageValue());
			}else{
				salvageValue = new BigDecimal(0);
			}
			int lifeCycleFromYear = convertMonthToYear(assetInfo.getLifecycle());
			BigDecimal lifeCycle = new BigDecimal(lifeCycleFromYear);//以年为单位
			BigDecimal unit = new BigDecimal(2d).divide(lifeCycle,2, RoundingMode.HALF_UP);
			int depreciationMethod = assetInfo.getDepreciationMethod();
			if(depreciationMethod == AVERAGE ){
				result = (purchasePrice.subtract(salvageValue)).divide(lifeCycle, 2, RoundingMode.HALF_UP);
			}else if(depreciationMethod == DOUBLE ){
				if(whichYear != null){
					/***
					 * 加速折旧法分为双倍余额递减法和年数总和法。
						双倍余额递减法的公式：设备入账帐面价值为X，预计使用N（N足够大）年，残值为Y。 　　
						则第一年折旧C<1>=X*2/N； 　　
						第二年折旧 C<2>=(X-C<1>)*2/N 　　
						第三年折旧 C<3>=(X-C<1>-C<2>)*2/N 　　
						··· ··· 　
						最后两年需改为直线法折旧
					 */
					if((lifeCycle.intValue() - whichYear < 2) && (lifeCycle.intValue() - whichYear >= 0)){
						result = purchasePrice.subtract(salvageValue).divide(lifeCycle,2, RoundingMode.HALF_UP);
					}else{
						BigDecimal temp = new BigDecimal(1d);
						temp = temp.subtract(unit);
						temp = temp.pow(whichYear-1);
						result = purchasePrice.multiply(unit).multiply(temp);
					}
				}
			}else if(depreciationMethod == YEAR ){
				//年数总和法公式： 设备入账帐面价值为X，预计使用N年，残值为Y，则第M年计提折旧为(X-Y)*(N-M+1)/[(N+1)*N/2]。
				result = (purchasePrice.subtract(salvageValue)).multiply(new BigDecimal(lifeCycle.intValue()-whichYear+1))
						.divide(new BigDecimal((((lifeCycle.intValue()+1)*lifeCycle.intValue())/2)), 2, RoundingMode.HALF_UP);
			}
		}
		if(result.doubleValue() >= 0){
			result = result.divide(new BigDecimal(12),2,BigDecimal.ROUND_HALF_DOWN);
		}
		return result.doubleValue();
	}
	
	private int convertMonthToYear(Integer lifecycle) {
		if(lifecycle <= 12){
			return 1;
		}
		return lifecycle/12 ;
	}
	


	@Transactional
	public void saveAssetDerpeciation(AssetInfo assetInfo) {
		assetDepreciationRepository = WebUtil.getBean(AssetDepreciationRepository.class);
		if (assetInfo == null) {
			return;
		}

		if (assetInfo.getPurchasePrice() == null || assetInfo.getLifecycle() == null
				|| assetInfo.getDepreciationMethod() == null || assetInfo.getSalvageValue() == null
				|| assetInfo.getPurchaseDate() == null) {
			assetDepreciationRepository.deleteByAssetIdAndContractType(assetInfo.getId(), -1);
		}

		if (assetInfo.getPurchasePrice() != null && assetInfo.getSalvageValue() != null
				&& assetInfo.getLifecycle() != null && assetInfo.getPurchaseDate() != null) {
			assetDepreciationRepository.deleteByAssetIdAndContractType(assetInfo.getId(), -1);

			int lifecycle = assetInfo.getLifecycle() - 1;// 以月为单位，不足1年的默认为1年
			Date purchaseDate = assetInfo.getPurchaseDate();
			DateTime first = new DateTime(purchaseDate);
			if (first.isAfterNow()) {// 未来时间暂不录入
				return;
			}
			DateTime last = first.plusMonths(lifecycle);
			String yyyyMM = null;
			Double depreciationAmount = null;
			AssetDepreciation ad = null;
			for (DateTime dateTime = first; dateTime.isBefore(last); dateTime = dateTime.plusMonths(1)) {
				ad = new AssetDepreciation();
				yyyyMM = dateTime.toString(FORMAT) + "-01";
				depreciationAmount = calAssetDepreciation(assetInfo, betweenYear(first, dateTime));
				ad.setAssetId(assetInfo.getId());
				ad.setSiteId(assetInfo.getSiteId());
				ad.setContractId(-1);
				ad.setDeprecateDate(TimeUtils.getDateFromStr(yyyyMM, "yyyy-MM-dd"));
				ad.setDeprecateAmount(depreciationAmount);
				assetDepreciationRepository.save(ad);
			}
		}
	}
	
	public int betweenYear(DateTime dt1,DateTime dt2){
		int year = Months.monthsBetween(dt1, dt2).getMonths()/12;
		return year +1;
	}
	

	public static void main(String[] args) {
		AssetInfo assetInfo = new AssetInfo();
		assetInfo.setPurchasePrice(80000d);
		assetInfo.setLifecycle(5);
//		assetInfo.setDepreciationMethod(AVERAGE);
		assetInfo.setDepreciationMethod(DOUBLE);
//		assetInfo.setDepreciationMethod(YEAR);
		assetInfo.setSalvageValue(5000d);
//		saveAssetDerpeciation(assetInfo);
		System.out.println();
	}
	
 
}
