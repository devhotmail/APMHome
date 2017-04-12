package com.ge.apm.dao;

import com.ge.apm.domain.I18nMessage;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface I18nMessageRepository extends GenericRepository<I18nMessage> {
    @Query("select o from I18nMessage o")
    public List<I18nMessage> getI18nMessages();

    public List<I18nMessage> getByMsgTypeAndSiteIdAndMsgKey(String msgType, Integer siteId, String msgKey);

    public List<I18nMessage> getByMsgType(String msgType);

    public I18nMessage getByMsgTypeAndMsgKey(String msgType, String msgKey);

    @Query(value="select im.* from i18n_message im where exists (select * from asset_type_faulty where asset_type_id = ?1 and im.id= fault_id)", nativeQuery = true)
    public List<I18nMessage> getFaultTypeByAssetType(Integer astypeId);
}
