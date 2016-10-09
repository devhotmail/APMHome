package com.ge.aps.service.utils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import webapp.framework.broker.JsonMapper;

/**
 *
 * @author 212547631
 */
public class PinyinUtil {

    private static Map<String, String> specialFirstNameMap1;
    public void setSpecialFirstNames1(String specialFirstNames1) {
        specialFirstNameMap1 = JsonMapper.nonEmptyMapper().fromJson(specialFirstNames1.replace("'", "\""), Map.class);
    }

    private static Map<String, String> specialFirstNameMap2;
    public void setSpecialFirstNames2(String specialFirstNames2) {
        specialFirstNameMap2 = JsonMapper.nonEmptyMapper().fromJson(specialFirstNames2.replace("'", "\""), Map.class);
    }
    
    public static boolean hasMultiPinyin(String str){
        for(int i=0; i<str.length(); i++){
            if(PinyinHelper.hasMultiPinyin(str.charAt(i))) 
                return true;
        }
        
        return false;
    }
    
    public static String getNamePinyin(String name){
        if(name==null) return null;
        name = name.trim();
        if("".equals(name)) return "";
        
        String firtName;
        String pinyinFirstName;
        String lastName;
        if(name.length()>2){
            firtName = name.substring(0, 2);
            
            pinyinFirstName = specialFirstNameMap2.get(firtName);
            if(pinyinFirstName!=null){
                lastName = name.substring(2, name.length());
                try {
                    return pinyinFirstName + " " + PinyinHelper.convertToPinyinString(lastName, " ", PinyinFormat.WITHOUT_TONE);
                } catch (PinyinException ex) {
                    Logger.getLogger(PinyinUtil.class.getName()).log(Level.SEVERE, null, ex);
                    return pinyinFirstName;
                }
            }
        }
        
        firtName = name.substring(0, 1);
        pinyinFirstName = specialFirstNameMap1.get(firtName);
        if(pinyinFirstName!=null){
            lastName = name.substring(1, name.length());
            try {
                return pinyinFirstName + " " + PinyinHelper.convertToPinyinString(lastName, " ", PinyinFormat.WITHOUT_TONE);
            } catch (PinyinException ex) {
                Logger.getLogger(PinyinUtil.class.getName()).log(Level.SEVERE, null, ex);
                return pinyinFirstName;
            }
        }
        
        try {
            return PinyinHelper.convertToPinyinString(name, " ", PinyinFormat.WITHOUT_TONE);
        } catch (PinyinException ex) {
            Logger.getLogger(PinyinUtil.class.getName()).log(Level.SEVERE, null, ex);
            return pinyinFirstName;
        }
    }

    public String getShortPinyin(String str){
        try {
            return PinyinHelper.getShortPinyin(str);
        } catch (PinyinException ex) {
            Logger.getLogger(PinyinUtil.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
