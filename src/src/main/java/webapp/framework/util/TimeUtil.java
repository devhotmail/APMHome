package webapp.framework.util;

import java.util.Date;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

public class TimeUtil {
    protected static final Logger logger = Logger.getLogger(TimeUtil.class);

    public static int DEFAULT_TIME_ZONE=8;
    
    public static Date now(){
        return timeNow().toDate();
    }

    public static DateTime timeNow(){
        return org.joda.time.DateTime.now().withZone(DateTimeZone.UTC);
    }

    public static DateTime timeNowInDefaultTimeZone(){
        return org.joda.time.DateTime.now().withZone(DateTimeZone.forOffsetHours(DEFAULT_TIME_ZONE));
    }
        
    public static DateTime fromString (String formatString, String dateString){
        try{
            return DateTimeFormat.forPattern(formatString).parseDateTime(dateString);
        }
        catch(Exception ex){
            //logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static DateTime fromString (String dateString){
        DateTime dt;
        try{
            dt = fromString("yyyy/MM/dd HH:mm:ss", dateString);
            if(dt!=null) return dt;
        }
        catch(Exception ex){
            logger.error("DateTime from string failed, using format 'yyyy/MM/dd HH:mm:ss': "+ex.getMessage(), ex);
        }
        
        try{
            dt = fromString("yyyy/MM/dd", dateString);
            if(dt!=null) return dt;
        }
        catch(Exception ex){
            logger.error("DateTime from string failed, using format 'yyyy/MM/dd': "+ex.getMessage(), ex);
        }

        try{
            dt = fromString("yyyy-MM-dd HH:mm:ss", dateString);
            if(dt!=null) return dt;
        }
        catch(Exception ex){
            logger.error("DateTime from string failed, using format 'yyyy/MM/dd': "+ex.getMessage(), ex);
        }
        try{

        dt = fromString("yyyy-MM-dd", dateString);
        if(dt!=null) return dt;
        }
        catch(Exception ex){
            logger.error("DateTime from string failed, using format 'yyyy/MM/dd': "+ex.getMessage(), ex);
        }

        return null;
    }

    public static String toString(Date dt, int timeZoneOffset, String dateFormat){
        DateTime jodaDate = new DateTime(dt);
        int hours = timeZoneOffset/60;
        int minutes = Math.abs(timeZoneOffset%60);
        return jodaDate.toDateTime(DateTimeZone.forOffsetHoursMinutes(hours, minutes)).toString(dateFormat);
    }
    
    public static String toString(Date dt, int timeZoneOffset){
        DateTime jodaDate = new DateTime(dt);
        int hours = timeZoneOffset/60;
        int minutes = Math.abs(timeZoneOffset%60);
        return jodaDate.toDateTime(DateTimeZone.forOffsetHoursMinutes(hours, minutes)).toString("MM/dd/yyyy HH:mma");
    }
    
    public static DateTime toJodaDate(Date dt){
        return new DateTime(dt);
    }
    
    public static Date nowInZone(int timeZone){
        return org.joda.time.DateTime.now().withZone(DateTimeZone.forOffsetHours(timeZone)).toDate();
    }

    public static String getTimeZoneString(String prefix, int timeZoneOffset){
        String timeZoneString;
        if (timeZoneOffset>0)
            timeZoneString = prefix+"+";
        else{
            timeZoneOffset = -timeZoneOffset;
            timeZoneString = prefix+"-";
        }
        
        int hours = timeZoneOffset/60;
        timeZoneString = timeZoneString + hours;
        
        int minutes = timeZoneOffset%60;
        if(minutes!=0)
            timeZoneString = timeZoneString + ":" + minutes;
        
        return timeZoneString;
    }

    public static Date toTimeZone(Date dt, int timeZone){
        DateTime jodaDate = new DateTime(dt);
        return jodaDate.toDateTime(DateTimeZone.forOffsetHours(timeZone)).toDate();
    }
}
