/*
 */
package com.ge.apm.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 *
 * @author 212601104
 */
@Entity
@Table(name = "asset_tag_msg_subscriber")
public class AssetTagMsgSubscriber  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_receive_chat_msg")
    private boolean isReceiveChatMsg;
    @Basic(optional = false)
    @NotNull
    @Column(name = "receive_msg_mode")
    private int receiveMsgMode;

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_receive_timeout_msg")
    private boolean isReceiveTimeoutMsg;

    @Basic(optional = false)
    @NotNull
    @Column(name = "subscribe_user_id")
    private Integer subscribeUserId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "tag_id")
    private Integer tagId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isReceiveChatMsg() {
        return isReceiveChatMsg;
    }

    public void setReceiveChatMsg(boolean receiveChatMsg) {
        isReceiveChatMsg = receiveChatMsg;
    }

    public int getReceiveMsgMode() {
        return receiveMsgMode;
    }

    public void setReceiveMsgMode(int receiveMsgMode) {
        this.receiveMsgMode = receiveMsgMode;
    }

    public Integer getSubscribeUserId() {
        return subscribeUserId;
    }

    public void setSubscribeUserId(Integer subscribeUserId) {
        this.subscribeUserId = subscribeUserId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public boolean isReceiveTimeoutMsg() {
        return isReceiveTimeoutMsg;
    }

    public void setReceiveTimeoutMsg(boolean receiveTimeoutMsg) {
        isReceiveTimeoutMsg = receiveTimeoutMsg;
    }
}
