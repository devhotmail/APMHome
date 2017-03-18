package com.ge.apm.service.wo;

import com.ge.apm.dao.WorkOrderMsgRepository;
import com.ge.apm.domain.WorkOrderMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author lsg
 */
@Component
public class WorkOrderMsgService {
    private static final Logger logger = Logger.getLogger(WorkOrderMsgService.class);

    @Autowired
    WorkOrderMsgRepository workOrderMsgRepository;

    public List<WorkOrderMsg> msgByWorkOrderId(int workOrderId)throws Exception{
        List<WorkOrderMsg> byStatus = workOrderMsgRepository.findByWorkOrderId(workOrderId);
        return byStatus;
    }


    
}
