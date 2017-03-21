package task;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkOrderMsgRepository;
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

    @Autowired
    WorkOrderMsgRepository workOrderMsgRepository;
    @Test
    @Transactional
    @Rollback(false)
    public void testisReopenWorkOrder()throws Exception{
       /* List<WorkOrder> reopenWorkOrder = workOrderRepository.isReopenWorkOrder(2, 2, 2);
        System.out.println(reopenWorkOrder.size()+"-------->");*/

workOrderService.workWorderCreate(2);

        //user  科员 siteid 2 hospitalid 2 de header is who
       /* UserAccount userAccount = userDao.getAssetHead(2, 2).get(0);
        System.out.println(userAccount.getName());*/
    }

    @Test
    @Transactional
    @Rollback(false)
    public void assignWorkOrder()throws Exception{
        //20006:workorder id, 3:userid the guy who is assgend  3: stepid
       // workOrderRepository.updateWorkderOrder(20006,3 ,"wangtian",3);
        //test service

        workOrderService.assignWorkOrder(20006,5,"Desc");
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testWorkOrder()throws Exception{
       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("-------" +workOrderRepository.findByStatus(1).size()) ;
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testfindByWorkOrderId()throws Exception{
        System.out.println("---x----" +workOrderMsgRepository.findByWorkOrderId(2).size()) ;
    }

}
