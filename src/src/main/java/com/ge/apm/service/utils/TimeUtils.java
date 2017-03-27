package com.ge.apm.service.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils {
	static Logger logger = LoggerFactory.getLogger(TimeUtils.class);
	public static Date getDateFromStr(String installDate,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(installDate);
		} catch (ParseException e) {
			logger.error("time format exception ,str is "+installDate);
		}
		return null;
	}
	
	public static String getStrDate(Date date,String format){
		if(format == null){
			format = "yyyy-MM-dd";
		}
		DateTime dt = new DateTime(date);
		return dt.toString(format);
	}
	
}
