package task;

import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.service.analysis.AssetExamDataAggregator;
import com.ge.apm.service.data.DataService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lsg on 7/3/2017.
 */
public class AssetExamTest extends BaseJunit4Test{



    @Autowired
    AssetClinicalRecordRepository assetClinicalRecordRepository;

    @Autowired
    AssetExamDataAggregator assetExamDataAggregator;

    @Autowired
    DataService dataService;

    @Test
    @Transactional
    @Rollback(true)
    public void testAggregateExamDataByDate()throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse("2014-11-10");
        Date date2 = sdf.parse("2015-09-18");
        System.out.println("assetClinicalRecordRepository.getAssetExamDataAggregatorByDate(date1,date2)-- "+assetClinicalRecordRepository.getAssetExamDataAggregatorByDate(date1,date2).size());

    }
    @Test
    @Transactional
    @Rollback(true)
    public void testAggregateExamData()throws Exception{
        System.out.println(" assetClinicalRecordRepository.getAssetExamDataAggregator()  "+ assetClinicalRecordRepository.getAssetExamDataAggregator().size());


    }

}
