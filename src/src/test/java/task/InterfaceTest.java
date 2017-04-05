package task;

import com.ge.apm.dao.WorkOrderMsgRepository;
import com.ge.apm.dao.WorkOrderRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;

/**
 * Created by lsg on 7/3/2017.
 */
public class InterfaceTest extends BaseJunit4Test{




    @Autowired
    WorkOrderRepository workOrderRepository;
    @Autowired
    WorkOrderMsgRepository workOrderMsgRepository;

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
    @Test
    @Transactional
    public void t1(){
 Double d =1.274577;// d= Math.round(d*100.0)/100.0;
        System.out.println(d);
        DecimalFormat df = new DecimalFormat("###.##");
        System.out.println("---->"+df.format(d));

    }



}
