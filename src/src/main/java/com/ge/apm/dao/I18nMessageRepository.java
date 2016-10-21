package com.ge.apm.dao;

import com.ge.apm.domain.I18nMessage;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface I18nMessageRepository extends GenericRepository<I18nMessage> {
    @Query("select o from I18nMessage o")
    public List<I18nMessage> getI18nMessages();
}
