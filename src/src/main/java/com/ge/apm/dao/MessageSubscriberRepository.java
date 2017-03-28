package com.ge.apm.dao;

import com.ge.apm.domain.MessageSubscriber;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface MessageSubscriberRepository extends GenericRepository<MessageSubscriber> {

    @Query("select o from MessageSubscriber o where o.assetId = ?1 and o.subscribeUserId = ?2")
    public MessageSubscriber getByAssetIdAndUserId(Integer assetId, Integer userId);
    
    public List<MessageSubscriber> getByAssetIdAndReceiveMsgModeIn(Integer assetId, Collection<Integer> c);
    
    public List<MessageSubscriber> getByAssetIdAndReceiveMsgMode(Integer assetId, Integer msgMode);
}
