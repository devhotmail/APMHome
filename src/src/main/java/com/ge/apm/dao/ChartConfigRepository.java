package com.ge.apm.dao;

import com.ge.apm.domain.ChartConfig;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface ChartConfigRepository extends GenericRepository<ChartConfig> {
    @Query("select o from ChartConfig o")
    public List<ChartConfig> loadChartConfigData();
}
