/*
 */
package com.ge.apm.edgeserver.sysutil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;

/**
 *
 * @author 212547631
 */
public class RisDeltaDataTool {
    private static final Logger logger = Logger.getLogger(RisDeltaDataTool.class);

    private String deltaFileName;
    private String deltaSQL;

    private String deltaValueField;
    private String deltaValueType;
    private String defaultDeltaValue;
    
    private String dateFormat;
    private String timeFormat;

    public RisDeltaDataTool() {
        deltaValueType = "";
        dateFormat = "yyyy-MM-dd";
        timeFormat = "HH:mm:ss";
        deltaFileName = "risDeltaValue.txt";
    }

    public String getDeltaSQL() {
        return deltaSQL;
    }

    public void setDeltaSQL(String deltaSQL) {
        this.deltaSQL = deltaSQL;
    }

    public String getDeltaFileName() {
        return deltaFileName;
    }

    public void setDeltaFileName(String deltaFileName) {
        this.deltaFileName = deltaFileName;
    }

    public String getDeltaValueField() {
        return deltaValueField;
    }

    public void setDeltaValueField(String deltaValueField) {
        this.deltaValueField = deltaValueField;
    }

    public String getDeltaValueType() {
        return deltaValueType;
    }

    public void setDeltaValueType(String deltaValueType) {
        this.deltaValueType = deltaValueType;
    }

    public String getDefaultDeltaValue() {
        return defaultDeltaValue;
    }

    public void setDefaultDeltaValue(String defaultDeltaValue) {
        this.defaultDeltaValue = defaultDeltaValue;
    }

    public String getDateTimeFormat() {
        return dateFormat + " " + timeFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }
    
    public void readDeltaValue(@Headers Map<String, Object> headers) throws FileNotFoundException, IOException, ParseException{
        if("datetime".equals(deltaValueType.toLowerCase()))
            readDateTimeValue(headers);
        else if("int".equals(deltaValueType.toLowerCase()))
            readIntValue(headers);
        else
            readValue(headers);
    }

    public void readValue(@Headers Map<String, Object> headers) throws FileNotFoundException, IOException{
        headers.put("_risSQL", deltaSQL);

        try{
            Properties prop = new Properties();

            prop.load(new FileInputStream(deltaFileName));
            headers.put(deltaValueField, prop.getProperty(deltaValueField));
        }
        catch(Exception ex){
            headers.put(deltaValueField, defaultDeltaValue);
        }
    }
    
    public void readIntValue(@Headers Map<String, Object> headers) throws FileNotFoundException, IOException{
        readValue(headers);
        
        String value = headers.get(deltaValueField).toString();
        headers.put(deltaValueField, Integer.parseInt(value));
    }

    public void readDateTimeValue(@Headers Map<String, Object> headers) throws FileNotFoundException, IOException, ParseException{
        readValue(headers);

        SimpleDateFormat sdf = new SimpleDateFormat(getDateTimeFormat());
        Date date = sdf.parse(headers.get(deltaValueField).toString());
        headers.put(deltaValueField, date);
    }
/*
    public void readDateAndTimeValue(@Headers Map<String, Object> headers) throws FileNotFoundException, IOException, ParseException{
        Properties prop = new Properties();
        prop.load(new FileInputStream(fileName));
        
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = sdf.parse(prop.getProperty(dateFieldName));
        headers.put(dateFieldName, date);
        
        sdf = new SimpleDateFormat(timeFormat);
        date = sdf.parse(prop.getProperty(timeFieldName));
        headers.put(timeFieldName, date);
    }

    public void updateDateAndTimeValue(@Body Map<String, Object> body) throws IOException{
        String strDate, strTime;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            strDate = sdf.format(body.get(dateFieldName));
        }
        catch(Exception ex){
            strDate = body.get(dateFieldName).toString();
        }
        
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
            strTime = sdf.format(body.get(timeFieldName));
        }
        catch(Exception ex){
            strTime = body.get(timeFieldName).toString();
        }
        
        Properties prop = new Properties();
        prop.setProperty(dateFieldName, strDate);
        prop.setProperty(timeFieldName, strTime);
        prop.store(new FileOutputStream(fileName), null);
    }
*/

    public void updateDeltaValue(@Body Map<String, Object> body) throws IOException{
        if("datetime".equals(deltaValueType.toLowerCase()))
            updateDateTimeValue(body);
        else
            updateValue(body);
    }
    
    public void updateValue(@Body Map<String, Object> body) throws IOException{
        Properties prop = new Properties();
        prop.setProperty(deltaValueField, body.get(deltaValueField).toString());
        prop.store(new FileOutputStream(deltaFileName), null);
    }

    public void updateDateTimeValue(@Body Map<String, Object> body) throws IOException{
        String strDate;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(getDateTimeFormat());
            strDate = sdf.format(body.get(deltaValueField));
        }
        catch(Exception ex){
            strDate = body.get(deltaValueField).toString();
        }
        
        Properties prop = new Properties();
        prop.setProperty(deltaValueField, strDate);
        prop.store(new FileOutputStream(deltaFileName), null);
    }

}
