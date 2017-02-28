package com.ge.apm.web.chart;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ge.apm.domain.BatchAssetCost;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.analysis.AssetCostDataService;
import com.ge.apm.service.uaa.UserAccountService;

@Controller
@RequestMapping("/asset")
public class TestController {
	
	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	AssetCostDataService assetCostDataService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET )
    @ResponseBody
    public List<UserAccount> getData(@PathVariable String id) {
    	logger.info("id is {}",id);
        return userAccountService.getUserAccount();
    }
    
    @RequestMapping(value = "/getUsers", method = RequestMethod.GET )
    @ResponseBody
    public List<UserAccount> getUsers() {
        return userAccountService.getUserAccount();
    }
    
    @RequestMapping(value = "/excute", method = RequestMethod.GET )
    @ResponseBody
    public String excuteTask(){
    	assetCostDataService.aggregateCostData();
    	return "success";
    }
    
    @RequestMapping(value = "/calByDay", method = RequestMethod.POST )
    @ResponseBody
    public String calByDay(@RequestBody BatchAssetCost bac){
    	assetCostDataService.calByDay(bac);
    	return "success";
    }
}
