package com.ge.apm.service.wo;

import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.dao.V2_WorkOrderStepRepository;
import com.ge.apm.dao.WorkOrderDetailRepository;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2017/7/25.
 */
@Component
@Scope("prototype")
public class WorkOrderImportService {

    private static final String SHEET_WORKORDER = "WorkOrder List";

    @Autowired
    ServiceRequestRepository serviceRequestDao;
    @Autowired
    private V2_WorkOrderRepository v2WorkOrderDao;
    @Autowired
    private V2_WorkOrderStepRepository v2WorkOrderStepDao;
    @Autowired
    WorkOrderDetailRepository workOrderDetailDao;


    public List<Map<String,Object>> createData(String institutionUID, String hospitalUID, String siteUID, Date startTime, Date endTime){

        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> workOrderMap;

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        if(institutionUID != null && !institutionUID.equals("")){
            SearchFilter institutionFilter = new SearchFilter("institutionUID", SearchFilter.Operator.EQ, institutionUID);
            searchFilters.add(institutionFilter);
        }
        if(hospitalUID != null && !hospitalUID.equals("")){
            SearchFilter hospitalFilter = new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, hospitalUID);
            searchFilters.add(hospitalFilter);
        }
        if(siteUID != null && !siteUID.equals("")){
            SearchFilter siteFilter = new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUID);
            searchFilters.add(siteFilter);
        }

        ZoneId zone = ZoneId.systemDefault();
        LocalDate startDate = LocalDateTime.ofInstant(startTime.toInstant(), zone).toLocalDate();
        LocalDate endDate = LocalDateTime.ofInstant(endTime.toInstant(), zone).toLocalDate();

        if(startTime != null){
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, startTime));
        }
        if(endTime != null){
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.LT, Date.from(endDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }

        List<V2_WorkOrder> tempWorkOrderList = v2WorkOrderDao.findBySearchFilter(searchFilters);
        //List<V2_WorkOrder> tempWorkOrderList = v2WorkOrderDao.find();

        for (V2_WorkOrder workOrder: tempWorkOrderList) {
            V2_ServiceRequest serviceRequest = serviceRequestDao.findOne(workOrder.getSrId());
            List<V2_WorkOrder_Detail> workOrderDetailList = workOrderDetailDao.findByWoId(workOrder.getId());
            V2_WorkOrder_Step sendStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 2);
            V2_WorkOrder_Step acceptStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 4);
            V2_WorkOrder_Step evaluateStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 5);
            V2_WorkOrder_Step closeStep = v2WorkOrderStepDao.findFirstByWoIdAndStepIdOrderByStartTimeDesc(workOrder.getId(), 6);
            workOrderMap = new HashedMap();
            workOrderMap.put("报修单号", "");
            workOrderMap.put("设备编号", "");
            workOrderMap.put("设备名称", serviceRequest.getAssetName());
            workOrderMap.put("院区", serviceRequest.getHospitalName());
            workOrderMap.put("科室", serviceRequest.getFromDeptName());
            workOrderMap.put("报修人", serviceRequest.getRequestorName());
            workOrderMap.put("报修时间", serviceRequest.getRequestTime());
            workOrderMap.put("故障描述", serviceRequest.getRequestReason());
            if(sendStep != null){
                workOrderMap.put("派工人", sendStep.getOwnerName());
                workOrderMap.put("派工时间", sendStep.getEndTime());
            }
            workOrderMap.put("工单类型", workOrder.getIntExtType());
            workOrderMap.put("上门维修/送修", workOrder.getRepairType());
            if(acceptStep != null){
                workOrderMap.put("接单人", acceptStep.getOwnerName());
                workOrderMap.put("接单时间", acceptStep.getEndTime());
            }
            workOrderMap.put("签到时间", workOrder.getCheckin());
            workOrderMap.put("维修工时",workOrder.getTotalManHour());
            String info = "";
            for (V2_WorkOrder_Detail workOrderDetail:workOrderDetailList) {
                info +=workOrderDetail.getParts()+",所用工时："+workOrderDetail.getManHours()+",费用:"+workOrderDetail.getPartsPrice()+";";
            }
            workOrderMap.put("维修备件", info);
            workOrderMap.put("价格", workOrder.getTotalPrice());
            if(evaluateStep != null){
                workOrderMap.put("验收人", evaluateStep.getOwnerName());
                workOrderMap.put("验收时间", evaluateStep.getEndTime());
            }
            if(closeStep != null){
                workOrderMap.put("关单人", closeStep.getOwnerName());
                workOrderMap.put("关单时间", closeStep.getEndTime());
            }
            workOrderMap.put("最终评价", workOrder.getFeedbackRating());
            workOrderMap.put("故障类型", workOrder.getCaseType());

            resultList.add(workOrderMap);
        }

        return resultList;
    }

    public Boolean exportData(ExcelDocument doc, List<Map<String,Object>> data){

        Integer keyRow = 1;
        Integer dataRow = 2;

        for (Map<String, Object> item : data) {
            doc.writeRowData(SHEET_WORKORDER, keyRow, dataRow++, item);
        }

        return Boolean.TRUE;
    }

}
