package com.ge.aps.dao;

import com.ge.aps.domain.DataTableConfig;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface DataTableConfigRepository extends GenericRepository<DataTableConfig> {
    @Query("select o from DataTableConfig o")
    public List<DataTableConfig> loadDataTableConfigData();
}
