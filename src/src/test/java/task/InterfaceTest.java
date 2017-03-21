package task;

import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.WorkOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lsg on 7/3/2017.
 */
public class InterfaceTest extends BaseJunit4Test{



    @Autowired
    WorkOrderRepository workOrderRepository;



    @Test
    @Transactional
    @Rollback(false)
    public void testisReopenWorkOrder()throws Exception{
        List<WorkOrder> reopenWorkOrder = workOrderRepository.isReopenWorkOrder(68, 2, 2);
        System.out.println(reopenWorkOrder.size()+"-------->");
    }


}
