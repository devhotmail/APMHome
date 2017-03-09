package com.ge.apm.web.chart;

import com.ge.apm.domain.BatchAssetExam;
import com.ge.apm.service.analysis.AssetExamDataAggregator;
import com.ge.apm.service.analysis.AssetExamDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lsg on 7/3/2017.
 *
 */
@Controller
@RequestMapping("/assetExam")
public class AssetExamController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    AssetExamDataService assetExamDataService;
    @Autowired
    AssetExamDataAggregator assetExamDataAggregator;
    //http://localhost:8087/geapm/web/assetExam/excute
    @RequestMapping(value = "/excute", method = RequestMethod.GET)
    @ResponseBody
    public String excute() {

        assetExamDataAggregator.aggregateExamData();
        return "success";
    }

    @RequestMapping(value = "/aggreFromTo", method = RequestMethod.POST)
    @ResponseBody
    public String aggreFromTo(@RequestBody BatchAssetExam batchAssetExam) {
        logger.info("----aggreFromTo--batchAssetExam--bODY->"+ batchAssetExam.getFrom());
        assetExamDataService.aggrateExamebyRange(batchAssetExam);
        return "success";
    }

    @RequestMapping(value = "/aggreByDay", method = RequestMethod.POST)
    @ResponseBody
    public String aggreByDay(@RequestBody BatchAssetExam batchAssetExam) {

        assetExamDataService.assetExamAggregatorByday(batchAssetExam);
        return "success";
    }


}
