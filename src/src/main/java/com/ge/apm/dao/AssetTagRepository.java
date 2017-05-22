package com.ge.apm.dao;

import com.ge.apm.domain.AssetTag;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by 212605082 on 2017/5/12.
 */
public interface AssetTagRepository extends GenericRepository<AssetTag> {

    @Query(value = "select * from asset_tag where id not in (select tag_id from asset_tag_msg_subscriber where subscribe_user_id=?1)",nativeQuery =true)
    public List<AssetTag> getUnSbuscriberTag(Integer userId);
}
