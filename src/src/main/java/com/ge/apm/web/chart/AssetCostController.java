package com.ge.apm.web.chart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ge.apm.domain.BatchAssetCost;
import com.ge.apm.service.analysis.AssetCostDataService;

@Controller
@RequestMapping("/assetCost")
public class AssetCostController {

	@Autowired
	AssetCostDataService assetCostDataService;

	@RequestMapping(value = "/today", method = RequestMethod.PUT)
	@ResponseBody
	public String excuteTask() {
		return assetCostDataService.aggregateCostData();
	}

	@RequestMapping(value = "/day", method = RequestMethod.PUT)
	@ResponseBody
	public String calByDay(@RequestBody BatchAssetCost bac) {
		return assetCostDataService.calByDay(bac);
	}
	
	@RequestMapping(value = "/fromTo", method = RequestMethod.PUT)
	@ResponseBody
	public String calByFromTo(@RequestBody BatchAssetCost bac) {
		return assetCostDataService.calByFromTo(bac);
	}
}
