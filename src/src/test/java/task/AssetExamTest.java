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

import com.ge.apm.dao.AssetClinicalRecordRepository;
import com.ge.apm.domain.BatchAssetExam;
import com.ge.apm.service.analysis.AssetExamDataAggregator;
import com.ge.apm.service.analysis.AssetExamDataService;
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
    @Rollback(false)
    public void testAggregateExamDataByDate()throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BatchAssetExam batchAssetExam=new BatchAssetExam();
        batchAssetExam.setFrom("2014-11-10");
        batchAssetExam.setTo("2015-09-18");
        assetExamDataService.aggrateExambyRange(batchAssetExam);
    }
    @Test
    @Transactional
    @Rollback(false)
    public void testAggregateExamDataByDay()throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2017-01-06");
        assetExamDataAggregator.aggregateExamDataByDay(date);

    }

    @Test
    @Transactional
    @Rollback(false)
    public void testAggregateExamData()throws Exception{

        //  assetExamDataAggregator.aggregateExamData();
        //    assetExamDataAggregator.aggregateExamData__bak();

    }
    @Autowired
    AssetExamDataService assetExamDataService;
    @Test
    @Transactional
    @Rollback(false)
    public void testAssetSummit()throws Exception{
        assetExamDataService.calRating();

    }

}