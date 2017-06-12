package com.ge.apm.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ge.apm.domain.PmOrder;
import webapp.framework.dao.GenericRepository;

public interface PmOrderRepository extends GenericRepository<PmOrder> {

	Page<PmOrder> findByAssetId(Integer assetId, Pageable pr);

}
