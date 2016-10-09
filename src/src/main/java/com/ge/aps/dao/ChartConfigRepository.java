package com.ge.aps.dao;

import com.ge.aps.domain.ChartConfig;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface ChartConfigRepository extends GenericRepository<ChartConfig> {
    @Query("select o from ChartConfig o")
    public List<ChartConfig> loadChartConfigData();
}
