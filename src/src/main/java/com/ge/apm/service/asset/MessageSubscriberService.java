/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.MessageSubscriberRepository;
import com.ge.apm.domain.MessageSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212579464
 */
@Component
public class MessageSubscriberService {
    
    @Autowired
    private MessageSubscriberRepository msDao;

    public MessageSubscriber getMessageSubscriber(Integer assetId, Integer userId) {
        return msDao.getByAssetIdAndUserId(assetId,userId );
    }

    public boolean saveOrUpdate(MessageSubscriber messageSubscriber) {
        MessageSubscriber ms = msDao.save(messageSubscriber);
        return ms!=null;
    }
    
    
}
