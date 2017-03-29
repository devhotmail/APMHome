package com.ge.apm.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource({"classpath:wx.properties"})  
public class ConfigUtils {
	
	@Autowired
    private  Environment env;
	
	public String fetchProperties(String key){
		return env.getProperty(key);
	}
}
