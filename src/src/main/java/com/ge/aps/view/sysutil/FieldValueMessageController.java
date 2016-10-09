package com.ge.aps.view.sysutil;

import com.ge.aps.domain.I18nMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import org.apache.log4j.Logger;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.DbMessageSource;

@ManagedBean(name="fieldMsg")
@ApplicationScoped
public class FieldValueMessageController implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FieldValueMessageController.class);
    
    public String fieldValue(String fieldName, String msgKey){
        String key = fieldName+"-"+msgKey;
        String value = WebUtil.getMessage(key);

        if(key.equals(value))
            return "";  // this means the message not found.
        else
            return value;
    }
    
    public List<I18nMessage> getFieldValueList(String fieldName){
        Integer instId = -1;
        try{
            instId = UserContextService.getCurrentUserAccount().getInstitutionId();
        }
        catch(Exception ex){
        }
        
        return getFieldValueList(fieldName, instId);
    }
    
    public List<I18nMessage> getFieldValueList(String fieldName, int institutionId){
        List<I18nMessage> msgList = doGetFieldValueList(fieldName, institutionId);
        if(msgList.isEmpty() && institutionId!=-1){
            msgList = doGetFieldValueList(fieldName, -1);
        }
        
        return msgList;
    }
    
    private List<I18nMessage> doGetFieldValueList(String fieldName, int institutionId){
        List<I18nMessage> msgList = new ArrayList<I18nMessage>();
        
        Map<String, I18nMessage> cache = DbMessageSource.getMessageCache(institutionId);
        if(cache==null){
            logger.warn("message cache not found, institutionId="+institutionId);
            return msgList;
        }
        
        fieldName = fieldName.toLowerCase();
        for(Map.Entry<String, I18nMessage> item: cache.entrySet()){
            if(item.getValue().getMsgType().equals(fieldName)){
                msgList.add(item.getValue());
            }
        }

        Collections.sort(msgList);
        
        return msgList;
    }
    
    public void reloadMessages(){
        DbMessageSource.reLoadMessages();
        WebUtil.addSuccessMessage("Messages reloaded!");
    }
}
