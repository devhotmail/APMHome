package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.EdgeServerInfo;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface EdgeServerInfoRepository extends GenericRepository<EdgeServerInfo> {
    @Query("select o from EdgeServerInfo o where o.edgeServerKey=?1 and o.isEnabled=true")
    public EdgeServerInfo getByEdgeServerKey(String edgeServerKey);
}
