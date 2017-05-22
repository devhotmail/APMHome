package com.ge.apm.dao;


import com.ge.apm.domain.AssetTag;
import com.ge.apm.domain.AssetTagMsgSubscriber;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.Date;
import java.util.List;

public interface AssetTagMsgSubscriberRepository extends GenericRepository<AssetTagMsgSubscriber> {


    @Query("select o from AssetTagMsgSubscriber o where o.subscribeUserId = ?1")
    public List<AssetTagMsgSubscriber> getAssetTagMsgSubscriber(Integer userId);



}

