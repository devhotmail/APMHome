package task;

import com.ge.apm.dao.AssetFaultTypeRepository;
import com.ge.apm.dao.WorkOrderMsgRepository;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.AssetFaultType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by lsg on 7/3/2017.
 */
public class InterfaceTest extends BaseJunit4Test{

    @Autowired
    WorkOrderRepository workOrderRepository;
    @Autowired
    WorkOrderMsgRepository workOrderMsgRepository;
    @Autowired
    AssetFaultTypeRepository assetFaultTypeRepository;
/*@Autowired
I18nMessageRepository i18nMessageRepository;*/
    /*故障分类*/
    @Test
    @Transactional
    @Rollback(true)
    public void testWorkOrder()throws Exception{
        List<AssetFaultType> byAssetGroupId = assetFaultTypeRepository.getByAssetGroupId(-1);
       // System.out.println(byAssetGroupId);
       /* List<Integer> groupId = assetFaultTypeRepository.getGroupId();
        System.out.println( groupId);*/

     //   System.out.println("-------------------"+assetFaultTypeRepository.typeIsReg(2));
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
