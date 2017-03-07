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
@RequestMapping("/asset")
public class AssetCostController {

	@Autowired
	AssetCostDataService assetCostDataService;

	@RequestMapping(value = "/excute", method = RequestMethod.GET)
	@ResponseBody
	public String excuteTask() {
		return assetCostDataService.aggregateCostData();
	}

	@RequestMapping(value = "/calByDay", method = RequestMethod.POST)
	@ResponseBody
	public String calByDay(@RequestBody BatchAssetCost bac) {
		return assetCostDataService.calByDay(bac);
	}
	
	@RequestMapping(value = "/calByFromTo", method = RequestMethod.POST)
	@ResponseBody
	public String calByFromTo(@RequestBody BatchAssetCost bac) {
		return assetCostDataService.calByFromTo(bac);
	}
}
