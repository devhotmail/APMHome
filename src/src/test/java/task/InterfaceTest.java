package task;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.service.wo.WorkOrderService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lsg on 7/3/2017.
 */
public class InterfaceTest extends BaseJunit4Test{



    @Autowired
    WorkOrderRepository workOrderRepository;
@Autowired
    WorkOrderService workOrderService;
    @Autowired
    private UserAccountRepository userDao;
    @Test
    @Transactional
    @Rollback(false)
    public void testisReopenWorkOrder()throws Exception{
       /* List<WorkOrder> reopenWorkOrder = workOrderRepository.isReopenWorkOrder(2, 2, 2);
        System.out.println(reopenWorkOrder.size()+"-------->");*/

workOrderService.workWorderCreate();

        //user  科员 siteid 2 hospitalid 2 de header is who
       /* UserAccount userAccount = userDao.getAssetHead(2, 2).get(0);
        System.out.println(userAccount.getName());*/
    }


}
