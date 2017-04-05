package com.ge.apm.web.chart;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.service.utils.ConfigUtils;
import com.ge.apm.service.wechat.CoreService;
import com.ge.apm.service.wo.WorkflowService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import webapp.framework.util.TimeUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/asset")
public class TestController {

	@Autowired
	private Environment env;

	@Autowired
	ConfigUtils configUtils;

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




    		String _openId = "otf1us5X8vbPlgpSK2C7aXKPxu6Q";
    		System.out.println("env is "+env);
    		String _templateId = configUtils.fetchProperties("timeout_template_id");
    		Map<String ,Object> map = new HashMap<String,Object>();
    		map.put("first", "领工超时");
    		map.put("_assetName", "CT MR");
    		map.put("_status", "维修中");
    		map.put("_currentPerson", "科员");
    		map.put("_requestTime", "2017-03-28");
    		map.put("_urgency", "重要");
    		map.put("_linkUrl", "/web/menu/35");
    		map.put("remark", "备注信息");
    		coreService.sendWxTemplateMessage(_openId, _templateId,map);
         return "success";

    }
    public void datecompare() throws Exception{

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date d1 =df.parse("2016-05-09");//2001-01-09 for var
        Date d2 =df.parse("2016-07-12");
    if(d1.compareTo(d2) ==0){ /**/  }
    /*is before or after*/
    DateTime dtFrom = TimeUtil.toJodaDate(d1);
        DateTime dtTo = TimeUtil.toJodaDate(d2).plusDays(1);
        while(dtFrom.isBefore(dtTo)){
            dtFrom = dtFrom.plusDays(1);
        }
     }
}
