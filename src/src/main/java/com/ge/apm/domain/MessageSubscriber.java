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

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "message_subscriber")
public class MessageSubscriber implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "site_id")
    private int siteId;
    @Basic(optional = false)
    @Column(name = "hospital_id")
    private int hospitalId;
    @Basic(optional = false)
    @Column(name = "asset_id")
    private int assetId;
    @Basic(optional = false)
    @Column(name = "subscribe_user_id")
    private int subscribeUserId;
    @Basic(optional = false)
    @Column(name = "receive_msg_mode")
    private int receiveMsgMode;
    @Basic(optional = false)
    @Column(name = "is_receive_timeout_msg")
    private boolean isReceiveTimeoutMsg;
    @Basic(optional = false)
    @Column(name = "is_receive_chat_msg")
    private boolean isReceiveChatMsg;

    public MessageSubscriber() {
    }

    public MessageSubscriber(Integer id) {
        this.id = id;
    }

    public MessageSubscriber(Integer id, int siteId, int hospitalId, int assetId, int subscribeUserId, int receiveMsgMode, boolean isReceiveTimeoutMsg, boolean isReceiveChatMsg) {
        this.id = id;
        this.siteId = siteId;
        this.hospitalId = hospitalId;
        this.assetId = assetId;
        this.subscribeUserId = subscribeUserId;
        this.receiveMsgMode = receiveMsgMode;
        this.isReceiveTimeoutMsg = isReceiveTimeoutMsg;
        this.isReceiveChatMsg = isReceiveChatMsg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public int getSubscribeUserId() {
        return subscribeUserId;
    }

    public void setSubscribeUserId(int subscribeUserId) {
        this.subscribeUserId = subscribeUserId;
    }

    public int getReceiveMsgMode() {
        return receiveMsgMode;
    }

    public void setReceiveMsgMode(int receiveMsgMode) {
        this.receiveMsgMode = receiveMsgMode;
    }

    public boolean getIsReceiveTimeoutMsg() {
        return isReceiveTimeoutMsg;
    }

    public void setIsReceiveTimeoutMsg(boolean isReceiveTimeoutMsg) {
        this.isReceiveTimeoutMsg = isReceiveTimeoutMsg;
    }

    public boolean getIsReceiveChatMsg() {
        return isReceiveChatMsg;
    }

    public void setIsReceiveChatMsg(boolean isReceiveChatMsg) {
        this.isReceiveChatMsg = isReceiveChatMsg;
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
        if (!(object instanceof MessageSubscriber)) {
            return false;
        }
        MessageSubscriber other = (MessageSubscriber) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.MessageSubscriber[ id=" + id + " ]";
    }
    
}
