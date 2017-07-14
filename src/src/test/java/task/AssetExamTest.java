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
    @Rollback(false) //ok
    public void testExamSummit()throws Exception{
        //by range day
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BatchAssetExam batchAssetExam=new BatchAssetExam();
        batchAssetExam.setFrom("2016-08-01");
        batchAssetExam.setTo("2016-09-01");
        assetExamDataService.aggregateExamSumitRangeDay(batchAssetExam);
        //by specific day
        Date date = sdf.parse("2017-01-21");
        assetExamDataAggregator.aggregateForExamSumit(date);
    }


    @Test
    @Transactional
    @Rollback(false)
    public void testAssetSummit()throws Exception{
        //by current day
        assetExamDataAggregator.aggregateExamSummit();
        // by specific day
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate= sdf.parse("2017-02-18");
        assetExamDataAggregator.aggregateForAssetSumit(currentDate);
        //by range
        BatchAssetExam batchAssetExam=new BatchAssetExam();
        batchAssetExam.setFrom("2016-08-01");
        batchAssetExam.setTo("2016-09-01");
        assetExamDataService.aggregateAssetSummitRangeDay(batchAssetExam);
    }

    @Autowired
    AssetExamDataService assetExamDataService;
    @Test
    @Transactional
    @Rollback(false)
    public void testRating()throws Exception{
        assetExamDataService.calRating();

    }

}