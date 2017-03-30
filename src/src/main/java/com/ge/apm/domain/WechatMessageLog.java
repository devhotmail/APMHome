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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "wechat_message_log")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WechatMessageLog.findAll", query = "SELECT w FROM WechatMessageLog w"),
    @NamedQuery(name = "WechatMessageLog.findById", query = "SELECT w FROM WechatMessageLog w WHERE w.id = :id"),
    @NamedQuery(name = "WechatMessageLog.findByWechatid", query = "SELECT w FROM WechatMessageLog w WHERE w.wechatid = :wechatid"),
    @NamedQuery(name = "WechatMessageLog.findByWoId", query = "SELECT w FROM WechatMessageLog w WHERE w.woId = :woId"),
    @NamedQuery(name = "WechatMessageLog.findByWoStepId", query = "SELECT w FROM WechatMessageLog w WHERE w.woStepId = :woStepId"),
    @NamedQuery(name = "WechatMessageLog.findByMessageCount", query = "SELECT w FROM WechatMessageLog w WHERE w.messageCount = :messageCount"),
    @NamedQuery(name = "WechatMessageLog.findByMessageType", query = "SELECT w FROM WechatMessageLog w WHERE w.messageType = :messageType")})
public class WechatMessageLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 64)
    @Column(name = "wechatid")
    private String wechatid;
    @Column(name = "wo_id")
    private Integer woId;
    @Column(name = "wo_step_id")
    private Integer woStepId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "message_count")
    private int messageCount;
    @Basic(optional = false)
    @NotNull
    @Column(name = "message_type")
    private int messageType;

    public WechatMessageLog() {
    }

    public WechatMessageLog(Integer id) {
        this.id = id;
    }

    public WechatMessageLog(Integer id, int messageCount, int messageType) {
        this.id = id;
        this.messageCount = messageCount;
        this.messageType = messageType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWechatid() {
        return wechatid;
    }

    public void setWechatid(String wechatid) {
        this.wechatid = wechatid;
    }

    public Integer getWoId() {
        return woId;
    }

    public void setWoId(Integer woId) {
        this.woId = woId;
    }

    public Integer getWoStepId() {
        return woStepId;
    }

    public void setWoStepId(Integer woStepId) {
        this.woStepId = woStepId;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
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
        if (!(object instanceof WechatMessageLog)) {
            return false;
        }
        WechatMessageLog other = (WechatMessageLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.WechatMessageLog[ id=" + id + " ]";
    }
    
}
