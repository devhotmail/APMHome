package webapp.framework.broker;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.log4j.Logger;
import org.json.XML;
import webapp.framework.util.TimeUtil;

public class WebServiceUtil {

    private static final Logger logger = Logger.getLogger(WebServiceUtil.class);

    private String soapAction;
    private String soapHost;
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }
    public void setSoapHost(String soapHost) {
        this.soapHost = soapHost;
    }

    public void setSOAPHeaders(@Headers Map messageHeaders) {
        messageHeaders.put("SOAPAction", soapAction);
        messageHeaders.put("Host", soapHost);
        messageHeaders.put("Content-Type", "text/xml;charset=UTF-8");
        messageHeaders.put("CamelHttpMethod", "POST");
    }

    private String[] paramNames;
    private String[] paramTypes;
    private String[] optionalParams;
    private String[] optionalSqlWheres;

    public void setParamNames(String paramNames) {
        this.paramNames = paramNames.split(",");
        this.paramTypes = new String[this.paramNames.length];

        for(int i=0; i<this.paramNames.length; i++){
            String[] typeValues = this.paramNames[i].split(":");
            if(typeValues.length>1){
                this.paramNames[i] = typeValues[0];
                this.paramTypes[i] = typeValues[1];
            }
            else
                this.paramTypes[i] = "str";
        }
    }

    public void setOptionalParams(String optionalParams) {
        this.optionalParams = optionalParams.split(",");
    }

    public void setOptionalSqlWheres(String optionalSqlWheres) {
        this.optionalSqlWheres = optionalSqlWheres.split(",");
    }

    public String getUrlRequestParams(@Body String messageBody, @Headers Map messageHeaders) throws Exception {
        for (int i=0; i<this.paramNames.length; i++) {
            String paramName = this.paramNames[i];
            Object value = messageHeaders.get(paramName);
            if(value!=null){
                if("int".equals(this.paramTypes[i])){
                    try{
                        Integer intValue = Integer.parseInt(value.toString());
                        messageHeaders.put(paramName, intValue);
                    }
                    catch(Exception ex){
                        throw new Exception("parseInt error: paramName="+paramName+", value="+value, ex);
                    }
                }
                else if("double".equals(this.paramTypes[i])){
                    try{
                        Double doubleValue = Double.parseDouble(value.toString());
                        messageHeaders.put(paramName, doubleValue);
                    }
                    catch(Exception ex){
                        throw new Exception("parseDouble error: paramName="+paramName+", value="+value, ex);
                    }
                }
                else if("date".equals(this.paramTypes[i])){
                    try{
                        Date dateValue = TimeUtil.fromString("yyyy-MM-dd", value.toString()).toDate();
                        messageHeaders.put(paramName, dateValue);
                    }
                    catch(Exception ex){
                        throw new Exception("parseDate error: paramName="+paramName+", value="+value, ex);
                    }
                }
                else if("dateTime".equals(this.paramTypes[i])){
                    try{
                        Date dateValue = TimeUtil.fromString("yyyy-MM-dd hh:mm:ss", value.toString()).toDate();
                        messageHeaders.put(paramName, dateValue);
                    }
                    catch(Exception ex){
                        throw new Exception("parseDateTime error: paramName="+paramName+", value="+value, ex);
                    }
                }
                else  //String type param
                    messageHeaders.put(paramName, value);
            }
        }
        
        return messageBody;
    }
    
    public static String getSoapParamValue(String inputXml, String paramName) {
        int len = paramName.length();
        int indexFrom = inputXml.indexOf(paramName+">");
        if (indexFrom >= 0) {
            indexFrom += len + 1;
            int indexTo = inputXml.indexOf("</", indexFrom);
            return inputXml.substring(indexFrom, indexTo);
        } else {
            return null;
        }
    }

    public String getSoapRequestParams(@Body String messageBody, @Headers Map messageHeaders) throws Exception {
        if(messageHeaders!=null){
            Object httpMethod = messageHeaders.get("CamelHttpMethod");
            if(httpMethod!=null && !"POST".equals(httpMethod.toString()))
                throw new Exception("Please use HTTP POST method to call this web service.");
        }
        if(messageBody==null || "".equals(messageBody.trim())){
            throw new Exception("MessageBody is empty, please make sure you are using HTTP POST method to call this web service.");
        }
        
        messageBody = messageBody.replaceAll("\n", "").replaceAll("\r", "");
        for (int i=0; i<this.paramNames.length; i++) {
            String paramName = this.paramNames[i];
            String value = getSoapParamValue(messageBody, paramName);
            if(value!=null){
                if("int".equals(this.paramTypes[i])){
                    try{
                        Integer intValue = Integer.parseInt(value);
                        messageHeaders.put(paramName, intValue);
                    }
                    catch(Exception ex){
                        throw new Exception("parseInt error: paramName="+paramName+", value="+value, ex);
                    }
                }
                else if("double".equals(this.paramTypes[i])){
                    try{
                        Double doubleValue = Double.parseDouble(value);
                        messageHeaders.put(paramName, doubleValue);
                    }
                    catch(Exception ex){
                        throw new Exception("parseDouble error: paramName="+paramName+", value="+value, ex);
                    }
                }
                else  //String type param
                    messageHeaders.put(paramName, value);
            }
            else{
                logger.warn("getSoapRequestParams: param value not found in request, param="+paramName);
            }
        }

        buildOptionalSqlWhere(messageBody, messageHeaders);
        
        return messageBody;
    }

    public void buildOptionalSqlWhere(@Body String messageBody, @Headers Map messageHeaders) throws Exception{
        int sizeOptionalParams;
        if(this.optionalParams!=null ) 
            sizeOptionalParams = this.optionalParams.length;
        else
            return;
        
        int sizeOptionalSqlWheres = 0;
        if(this.optionalSqlWheres!=null ) sizeOptionalSqlWheres = this.optionalSqlWheres.length;

        if(sizeOptionalParams!=sizeOptionalSqlWheres)
            throw new Exception("OptionalParams & OptionalSqlWheres not matched!, sizeof OptionalParams="+sizeOptionalParams+
                    ", size of OptionalSqlWheres="+sizeOptionalSqlWheres);
        
        String paramOptionalSqlWheres = "(1=1)";
        for (int i=0; i<this.optionalParams.length; i++) {
            String paramName = this.optionalParams[i];
            if(messageHeaders.get(paramName)!=null){
                paramOptionalSqlWheres += " and (" + this.optionalSqlWheres[i] + ")";
            }
        }
        messageHeaders.put("optionalSqlWheres", paramOptionalSqlWheres);
    }
    
    public String convertSqlResultToJson(@Body List<Map<String, Object>> sqlResult){
        JsonMapper mapper = JsonMapper.nonEmptyMapper();
        return mapper.toJson(sqlResult);
    }
    
    public String convertXmlToJson(String xml){
        xml = xml.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
        return XML.toJSONObject(xml).toString();
    }
}
