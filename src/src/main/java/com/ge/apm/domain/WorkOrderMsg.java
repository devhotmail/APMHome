/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "work_order_msg")
public class WorkOrderMsg implements Serializable {

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
    @Column(name = "work_order_id")
    private int workOrderId;
    @Basic(optional = false)
    @Column(name = "sender_user_id")
    private int senderUserId;
    @Column(name = "sender_user_name")
    private String senderUserName;
    @Column(name = "send_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendTime;
    @Column(name = "message")
    private String message;

    public WorkOrderMsg() {
    }

    public WorkOrderMsg(Integer id) {
        this.id = id;
    }

    public WorkOrderMsg(Integer id, int siteId, int workOrderId, int senderUserId) {
        this.id = id;
        this.siteId = siteId;
        this.workOrderId = workOrderId;
        this.senderUserId = senderUserId;
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

    public int getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }

    public int getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(int senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        if (!(object instanceof WorkOrderMsg)) {
            return false;
        }
        WorkOrderMsg other = (WorkOrderMsg) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.WorkOrderMsg[ id=" + id + " ]";
    }
    
}
