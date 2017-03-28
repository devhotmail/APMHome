package com.ge.apm.web.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.service.wechat.CoreService;
import com.ge.apm.service.wo.WorkflowService;

@Controller
@RequestMapping("/asset")
public class TestController {
	
	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	WorkflowService workflowService;
	
	@Autowired
	CoreService coreService;
	
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
    
    @RequestMapping(value = "/timeout", method = RequestMethod.GET )
    @ResponseBody
    public String calTimeout() {
         workflowService.execute();
         return "success";
    }
    
    @RequestMapping(value = "/push_wx", method = RequestMethod.GET )
    @ResponseBody
    public String pushWX() {
    		String openId = "otf1us5X8vbPlgpSK2C7aXKPxu6Q";
    		String templateId = "N57cA1JvT1KMsmNLmWh5_nOM6rag-QHGQ4zWicwnzGI";
    		Map<String ,Object> map = new HashMap<String,Object>();
    		map.put("_assetName", "CT MR");
    		map.put("_status", "维修中");
    		map.put("_currentPerson", "科员");
    		map.put("_linkUrl", "www.baidu.com");
    		coreService.sendWxTemplateMessage(openId, templateId,map);
         return "success";
         
    }
    
}
