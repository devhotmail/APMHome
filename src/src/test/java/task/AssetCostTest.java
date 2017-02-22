package task;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import com.ge.apm.service.analysis.AssetCostDataService;
import com.ge.apm.service.data.DataService;

public class AssetCostTest extends BaseJunit4Test{

	@Autowired
	AssetCostDataService assetCostDataService;
	
	@Autowired
	DataService dataService;
	
	@Test
	@Transactional
    @Rollback(true) 
	public void test(){
//		assetCostDataService.excuteTask();
		System.out.println("impl interface "+assetCostDataService);
		System.out.println("impl itself "+dataService);
	}
}
