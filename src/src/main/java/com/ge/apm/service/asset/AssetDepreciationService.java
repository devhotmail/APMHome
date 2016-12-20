package com.ge.apm.service.asset;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ge.apm.domain.AssetInfo;

@Component
public class AssetDepreciationService {
	public static final int AVERAGE = 1;//平均
	public static final int DOUBLE = 2;//加速双倍余额
	public static final int YEAR = 3;//加速年限
	
	private static final Logger logger = LoggerFactory.getLogger(AssetDepreciationService.class);
	
	/***
	 * 计算资产的月平均成本
	 */
	public static Double calAssetDepreciation(AssetInfo assetInfo,Integer whichYear){
		BigDecimal  result = new BigDecimal(0);
		if(assetInfo == null || whichYear == null){
			return result.doubleValue();
		}
//		if(assetId == null || assetId <= 0){
//			logger.error("assetId 不合法!");
//			return result.doubleValue();
//		}
//		AssetInfo assetInfo = assetInfoDao.findById(assetId);
//		if(assetInfo == null){
//			logger.error("找不到对应的资产!");
//			return result.doubleValue();
//		}
		if(assetInfo.getPurchasePrice() != null && assetInfo.getLifecycle() != null && assetInfo.getDepreciationMethod() != null){
			BigDecimal purchasePrice = new BigDecimal(assetInfo.getPurchasePrice());
			BigDecimal salvageValue;
			if(assetInfo.getSalvageValue() != null){
				salvageValue = new BigDecimal(assetInfo.getSalvageValue());
			}else{
				salvageValue = new BigDecimal(0);
			}
			BigDecimal lifeCycle = new BigDecimal(assetInfo.getLifecycle());//以年为单位
			BigDecimal  unit = new BigDecimal(2d).divide(lifeCycle);
			int depreciationMethod = assetInfo.getDepreciationMethod();
			if(depreciationMethod == AVERAGE ){
				result = (purchasePrice.subtract(salvageValue)).divide(lifeCycle);
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
						result = purchasePrice.subtract(salvageValue).divide(lifeCycle);
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
						.divide(new BigDecimal((((lifeCycle.intValue()+1)*lifeCycle.intValue())/2)));
			}
		
		}
		if(result.doubleValue() >= 0){
			result = result.divide(new BigDecimal(12),5,BigDecimal.ROUND_HALF_DOWN);
		}
		return result.doubleValue();
	}
	
	public static Double test(int i,Integer whichYear){
		BigDecimal  result = new BigDecimal(0);
		AssetInfo assetInfo = new AssetInfo();
		assetInfo.setPurchasePrice(80000d);
		assetInfo.setLifecycle(5);
		assetInfo.setDepreciationMethod(i);
		assetInfo.setSalvageValue(5000d);
		if(assetInfo.getPurchasePrice() != null && assetInfo.getLifecycle() != null && assetInfo.getDepreciationMethod() != null){
			BigDecimal purchasePrice = new BigDecimal(assetInfo.getPurchasePrice());
			BigDecimal salvageValue;
			if(assetInfo.getSalvageValue() != null){
				salvageValue = new BigDecimal(assetInfo.getSalvageValue());
			}else{
				salvageValue = new BigDecimal(0);
			}
			BigDecimal lifeCycle = new BigDecimal(assetInfo.getLifecycle());//以年为单位
			BigDecimal  unit = new BigDecimal(2d).divide(lifeCycle);
			int depreciationMethod = assetInfo.getDepreciationMethod();
			if(depreciationMethod == AVERAGE ){
				result = (purchasePrice.subtract(salvageValue)).divide(lifeCycle);
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
						result = purchasePrice.subtract(salvageValue).divide(lifeCycle);
					}else{
//						result = getNextYear(purchasePrice,unit,whichYear);
//						BigDecimal everyYearLeavings = new BigDecimal(1-unit.doubleValue()).pow(whichYear);
//						result = purchasePrice.subtract(purchasePrice.multiply(everyYearLeavings));
						BigDecimal temp = new BigDecimal(1d);
						temp = temp.subtract(unit);
						temp = temp.pow(whichYear-1);
						result = purchasePrice.multiply(unit).multiply(temp);
					}
				}
			}else if(depreciationMethod == YEAR ){
				//年数总和法公式： 设备入账帐面价值为X，预计使用N年，残值为Y，则第M年计提折旧为(X-Y)*(N-M+1)/[(N+1)*N/2]。
				result = (purchasePrice.subtract(salvageValue)).multiply(new BigDecimal(lifeCycle.intValue()-whichYear+1))
						.divide(new BigDecimal((((lifeCycle.intValue()+1)*lifeCycle.intValue())/2)));
			}
		}
		if(result.doubleValue() >= 0){
			result = result.divide(new BigDecimal(12),5,BigDecimal.ROUND_HALF_DOWN);
		}
		return result.doubleValue();
	
	}
	
	public static BigDecimal getNextYear(BigDecimal purchasePrice,BigDecimal unit ,int thisYear){
		if(thisYear == 1){
			return purchasePrice.multiply(unit);
		}
		if(thisYear > 1){
			return purchasePrice.multiply(unit).subtract((getNextYear(purchasePrice,unit,thisYear-1)).multiply(unit));
		}
		return new BigDecimal(0);
	}
	
	public static void main(String[] args) {
		AssetInfo assetInfo = new AssetInfo();
		assetInfo.setPurchasePrice(80000d);
		assetInfo.setLifecycle(5);
//		assetInfo.setDepreciationMethod(AVERAGE);
//		assetInfo.setDepreciationMethod(DOUBLE);
		assetInfo.setDepreciationMethod(YEAR);
		assetInfo.setSalvageValue(5000d);
		System.out.println(calAssetDepreciation(assetInfo,3));
	}
	
 
}
