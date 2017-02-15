package com.ge.apm.web.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/asset")
public class TestController {
	private Logger logger = LoggerFactory.getLogger(getClass());
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET )
    @ResponseBody
    public Object getData(UserDetails user,@PathVariable String id) {
    	logger.info("id is {}",id);
    	logger.info("current user is {}",user.getUsername());
        return "helloworld";
    }
}
