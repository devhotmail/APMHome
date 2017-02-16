package webapp.framework.web.service;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.view.sysutil.UserContextService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import webapp.framework.broker.SiBroker;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212547631
 */
public final class DbMessageSource extends ResourceBundleMessageSource {

    private static final Logger logger = Logger.getLogger(DbMessageSource.class);

    private static Map<Integer, Map<String, I18nMessage>> msgCache = null;

    private static final String TYPE_LABEL = "label";
    private static final String TYPE_MESSAGE = "message";
    private static final String TYPE_FIELD_NAME = "field_name";

    public static Map<String, I18nMessage> getMessageCache(int siteId){
        Map<String, I18nMessage> cache = msgCache.get(siteId);
        if(cache!=null) return cache; 
        
        return msgCache.get(-1);
    }
    
    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        loadMessages();
        
        String msg = getText(code, locale);
        MessageFormat result = createMessageFormat(msg, locale);
        return result;
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        loadMessages();
        return getText(code, locale);
    }

    private String getText(String code, Locale locale) {
        loadMessages();
        
        String lang = locale.toString().substring(0, 2);
        Integer siteId = -1;
        try{
            siteId = UserContextService.getCurrentUserAccount().getSiteId();
        }
        catch(Exception ex){
        }
        
        String msgValue = getText(code, lang, siteId);
        if(msgValue!=null) return msgValue;
        
        if(siteId!=-1){
            msgValue = getText(code, lang, -1);
            if(msgValue!=null) return msgValue;
        }
        
        //logger.warn("****** failed to get message, msgKey="+code);
        return code; //failed to get message, then return the code
    }

    private String getText(String code, String lang, Integer siteId) {
        code = code.toLowerCase();
        
        Map<String, I18nMessage> siteMsg = msgCache.get(siteId);
        if(siteMsg!=null){
            I18nMessage msg = siteMsg.get(code);
            if(msg!=null) 
                return msg.getValue(lang);
        }
        return null;
    }
    
    protected void loadMessages() {
        if(msgCache!=null) return;
        
        reLoadMessages();
    }
    
    public static void reLoadMessages() {
        I18nMessageRepository msgDao = WebUtil.getBean(I18nMessageRepository.class);
        if(msgDao == null) return;

        if(msgCache == null)
            msgCache = new HashMap<Integer, Map<String, I18nMessage>>();
        else
            msgCache.clear();
        
        loadLabelMessages(msgDao);
        loadExtraDbExtraMessages();
        
/*        
        loadModalityDeptNames(msgDao);
        loadModalityNames(msgDao);
        loadProcedureNames(msgDao);
        loadProcedureStepNames(msgDao);
*/
    }

    public static String safeGetFieldValue(Map<String, Object> map, String fieldName, String defaultValue){
        Object obj = map.get(fieldName);
        return (obj==null)?defaultValue:obj.toString();
    }
    
    public static void loadExtraDbExtraMessages(){
        Map<String, Object> sqlParams = new HashMap<String, Object>();
        for(Map.Entry<String, String> item: ExtraDbMessageSource.getExtraMessages().entrySet()){
            sqlParams.put("_sql", item.getValue());
            List<Map<String, Object>> data = (List<Map<String, Object>>)SiBroker.sendRequestWithHeaders("direct:executeNativeSQL", null, sqlParams);

            List<I18nMessage> msgList = new ArrayList<I18nMessage>();
            for(Map<String, Object> row: data){
                I18nMessage msg = new I18nMessage();
                msgList.add(msg);

                msg.setMsgType(item.getKey());
                msg.setId(Integer.parseInt(row.get("msg_key").toString()));
                msg.setMsgKey(safeGetFieldValue(row, "msg_key", null));
                msg.setValueZh(safeGetFieldValue(row, "value_zh", null));
                msg.setValueEn(safeGetFieldValue(row, "value_en", null));
                msg.setValueTw(safeGetFieldValue(row, "value_tw", null));
                msg.setSiteId(Integer.parseInt(safeGetFieldValue(row, "siteId", "-1")));
            }
            cacheI18nMessages(msgList);
        }
    }

    public static void loadLabelMessages(I18nMessageRepository msgDao) {
        List<I18nMessage> messages = msgDao.getI18nMessages();
        cacheI18nMessages(messages);
    }

    protected  static void cacheI18nMessages(List<I18nMessage> messages) {
        for(I18nMessage msg: messages) {
            Integer siteId = msg.getSiteId();
            if(siteId==null){
                logger.warn("*** WARNING: siteId is NULL while Loading i18n Messages, msgType="+msg.getMsgType()+", msgKey="+msg.getMsgKey());
                siteId = -1;
            }
            
            Map<String, I18nMessage> siteMsg = msgCache.get(siteId);
            if(siteMsg==null){
                siteMsg = new HashMap<String, I18nMessage>();
                msgCache.put(siteId, siteMsg);
            }
            
            String msgType = msg.getMsgType();
            String msgKey = msg.getMsgKey();
            
            if(!TYPE_LABEL.equals(msgType) && !TYPE_FIELD_NAME.equals(msgType) && !TYPE_MESSAGE.equals(msgType))
                msgKey = String.format("%s-%s", msgType, msgKey);

            msgKey = msgKey.toLowerCase();
            siteMsg.put(msgKey, msg);
        }
    }
}
