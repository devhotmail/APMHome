package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrder;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

public interface V2_WorkOrderRepository extends GenericRepositoryUUID<V2_WorkOrder> {

    List<V2_WorkOrder> findBySrId(String srId);

    /*@Query("select distinct wo from V2_WorkOrder wo where wo.assetId in(select distinct(tr.assetId) from BiomedGroupUser bgu "
            + " join AssetTagBiomedGroup tbg on tbg.biomedGroupId=bgu.groupId join AssetTagRule tr on tbg.tagId=tr.tagId  where  bgu.userId=?1)"
            + " and wo.currentPersonId='-1' and wo.currentStepId=3 and wo.status=1 or wo.currentPersonId=?1 and wo.currentStepId=3 and wo.status=1")*/

    @Query(value = "select  wo.* from v2_work_order wo where wo.asset_id in  (select distinct(tr.asset_id) from biomed_group_user bgu,asset_tag_biomed_group tbg,asset_tag_rule tr where tbg.biomed_group_Id=bgu.group_Id and tbg.tag_id =tr.tag_id and bgu.user_id=?1) and wo.current_person_id='-1' and wo.current_step_id=3 and wo.status=1 or wo.current_person_id=?1 and wo.current_step_id=3 and wo.status=1 order by ?#{#pageable}",
            countQuery = "select  wo.* from v2_work_order wo where wo.asset_id in  (select distinct(tr.asset_id) from biomed_group_user bgu,asset_tag_biomed_group tbg,asset_tag_rule tr where tbg.biomed_group_Id=bgu.group_Id and tbg.tag_id =tr.tag_id and bgu.user_id=?1) and wo.current_person_id='-1' and wo.current_step_id=3 and wo.status=1 or wo.current_person_id=?1 and wo.current_step_id=3 and wo.status=1",nativeQuery = true)
    Page<V2_WorkOrder> fetchAvailableWorkOrderByUser(Integer userId, Pageable pageable);

    Page<V2_WorkOrder> findByHospitalIdAndStatusAndCurrentStepIdAndCurrentPersonIdIn(int hospitalId, int status, int currentStepId, Collection<Integer> c, Pageable pageable);


    @Query(value = "select  wo.* from V2_Work_Order wo where wo.current_Person_Id in (select distinct bgu2.user_Id from Biomed_Group_User bgu2 where bgu2.group_Id in (select  bgu3.group_Id from Biomed_Group_User bgu3 where bgu2.user_Id=?1)) and wo.current_Step_Id=3 and wo.status=1 order by ?#{#pageable}",countQuery ="select  wo.* from V2_Work_Order wo where wo.current_Person_Id in (select distinct bgu2.user_Id from Biomed_Group_User bgu2 where bgu2.group_Id in (select  bgu3.group_Id from Biomed_Group_User bgu3 where bgu2.user_Id=?1)) and wo.current_Step_Id=3 and wo.status=1" ,nativeQuery = true)
    Page<V2_WorkOrder> fetchAvailableSameGroupWorkOrderByUser(Integer userId,Pageable pageable);


    /* select distinct wo.* from v2_work_order wo where wo.asset_id in  (select distinct(tr.asset_id) from biomed_group_user bgu,asset_tag_biomed_group tbg,asset_tag_rule tr where tbg.biomed_group_Id=bgu.group_Id and tbg.tag_id =tr.tag_id and bgu.user_id=?1) and wo.current_person_id='-1' and wo.current_step_id=3 and wo.status=1 or wo.current_person_id=?1 and wo.current_step_id=3 and wo.status=1 */
}
