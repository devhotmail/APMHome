package com.ge.apm.dao;

import com.ge.apm.domain.InspectionOrderDetail;
import webapp.framework.dao.GenericRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface InspectionOrderDetailRepository extends GenericRepository<InspectionOrderDetail> {
 @Query("select d,c from InspectionOrderDetail d,InspectionChecklist c where  d.itemId=c.id and d.orderId=?1 order by c.displayOrder")
    List<InspectionOrderDetail> searchByOrder(Integer orderId);

    public List<InspectionOrderDetail> findByOrderId(Integer orderId);
}
