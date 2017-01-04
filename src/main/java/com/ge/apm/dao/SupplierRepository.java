package com.ge.apm.dao;

import com.ge.apm.domain.Supplier;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface SupplierRepository extends GenericRepository<Supplier> {
    public List<Supplier> getBySiteId(int siteId);

}
