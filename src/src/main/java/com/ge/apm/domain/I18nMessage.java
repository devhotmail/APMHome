/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import webapp.framework.web.service.UserContext;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "i18n_message")
public class I18nMessage implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "msg_type")
    private String msgType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "msg_key")
    private String msgKey;
    @Size(max = 50)
    @Column(name = "value_zh")
    private String valueZh;
    @Size(max = 50)
    @Column(name = "value_en")
    private String valueEn;
    @Size(max = 50)
    @Column(name = "value_tw")
    private String valueTw;
    @Column(name = "site_id")
    private Integer siteId;

    public I18nMessage() {
    }

    public I18nMessage(Integer id) {
        this.id = id;
    }

    public I18nMessage(Integer id, String msgType, Object msgKey, String valueZh, String valueEn, String valueTw, Integer siteId) {
        this.id = id;
        this.msgType = msgType;
        this.msgKey = msgKey.toString();
        this.valueZh = valueZh;
        this.valueEn = valueEn;
        this.valueTw = valueTw;
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getValueZh() {
        return valueZh;
    }

    public void setValueZh(String valueZh) {
        if(valueZh!=null){
            valueZh = valueZh.trim();
            if("".equals(valueZh)) valueZh = null;
        }
        this.valueZh = valueZh;
    }

    public String getValueEn() {
        return valueEn;
    }

    public void setValueEn(String valueEn) {
        if(valueEn!=null){
            valueEn = valueEn.trim();
            if("".equals(valueEn)) valueEn = null;
        }
        this.valueEn = valueEn;
    }

    public String getValueTw() {
        return valueTw;
    }

    public void setValueTw(String valueTw) {
        if(valueTw!=null){
            valueTw = valueTw.trim();
            if("".equals(valueTw)) valueTw = null;
        }
        this.valueTw = valueTw;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getValue(String lang){
        if("zh".equals(lang) && valueZh!=null)
            return valueZh;
        else if("en".equals(lang) && valueEn!=null)
            return valueEn;
        else if("tw".equals(lang) && valueTw!=null)
            return valueTw;
        
        //so get Zh value if no message define for specific lang
        return valueZh;
    }
    
    public String getValue(){
        try{
            String lang = UserContext.getLocale().toString().substring(0, 2);
            return this.getValue(lang);
        }
        catch(Exception ex){
            return this.getValue("zh");
        }
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof I18nMessage)) {
            return false;
        }
        I18nMessage other = (I18nMessage) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "I18nMessage{" + "msgType=" + msgType + ", msgKey=" + msgKey + ", valueZh=" + valueZh + ", valueEn=" + valueEn + ", getSiteId=" + siteId + '}';
    }

    @Override
    public int compareTo(Object t) {
        I18nMessage otherMsg = (I18nMessage)t;
        return this.getMsgKey().compareTo(otherMsg.getMsgKey());
    }
    
}
