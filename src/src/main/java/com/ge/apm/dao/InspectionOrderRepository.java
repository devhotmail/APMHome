package com.ge.apm.dao;

import com.ge.apm.domain.InspectionOrder;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface InspectionOrderRepository extends GenericRepository<InspectionOrder> {

//     @Query("select  distinct o from InspectionOrder o,InspectionOrderDetail d where o.id=d.orderId and d.assetId=?1 and o.orderType=?2 and o.startTime>current_date group by o.id order by o.startTime ")
     @Query("select  distinct o from InspectionOrder o,InspectionOrderDetail d where o.id=d.orderId and d.assetId=?1 and o.orderType=?2 and o.isFinished=false  group by o.id order by o.startTime ")
    public List<InspectionOrder> getRecentlyInspectionOrder(Integer assetId,Integer orderType);




}
