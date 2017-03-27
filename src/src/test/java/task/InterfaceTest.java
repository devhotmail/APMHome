package task;

import com.ge.apm.dao.AssetSummitRepository;
import com.ge.apm.dao.WorkOrderMsgRepository;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.service.analysis.AssetExamDataService;
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
    WorkOrderMsgRepository workOrderMsgRepository;
@Autowired
    AssetSummitRepository assetSummitRepository;



    @Test
    @Transactional
    @Rollback(false)
    public void testWorkOrder()throws Exception{
       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      //  System.out.println("-------" +workOrderRepository.findByStatus(1).size()) ;

    }

    @Test
    @Transactional
    @Rollback(false)
    public void testfindByWorkOrderId()throws Exception{
        System.out.println("---x----" +workOrderMsgRepository.findByWorkOrderId(2).size()) ;



    }

    @Autowired
AssetExamDataService assetExamDataService;

    @Test
    @Transactional
    @Rollback(false)
    public String testAssetSummit()throws Exception{
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //  System.out.println("-------" +workOrderRepository.findByStatus(1).size()) ;
        assetExamDataService.calRating();
return null;

    }


}
