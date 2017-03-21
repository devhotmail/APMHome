package com.ge.apm.dao;

import com.ge.apm.domain.WorkOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface WorkOrderRepository extends GenericRepository<WorkOrder> {
    
    public WorkOrder getByIdAndCurrentPersonId(int id, int currentPersonId);
    
    public List<WorkOrder> findByAssetId(Integer assetId);

    public List<WorkOrder> findByAssetIdAndIntExtType(Integer assetId, int intExtType);

//    @Query("select wo from WorkOrder wo ")
//    public List<WorkOrder> isReopenWorkOrder();


    @Query("select wo from WorkOrder wo where  (wo.closeTime::timestamp)::date > (select now() - interval \'7 day\')::date  and wo.assetId =:assetId and wo.hospitalId=:hospitalId and wo.siteId=:siteId")
    public List<WorkOrder> isReopenWorkOrder(@Param("assetId") Integer assetId, @Param("hospitalId") Integer hospitalId, @Param("siteId")Integer siteId);

    /*@Query("select wo from WorkOrder wo where  (wo.closeTime::timestamp)::date > (select now() - interval '7 day')::date  and  wo.assetId =:assetId and wo.hospitalId=:hospitalId and wo.siteId=:siteId")
    public List<WorkOrder> isReopenWorkOrder(@Param("assetId") Integer assetId,@Param("hospitalId") Integer hospitalId,@Param("siteId")Integer siteId);
*/

/*select * from work_order where  (close_time::timestamp)::date > (select now() - interval '7 day')::date  and  asset_id =68 and hospital_id=2 and site_id=2
*/


}
