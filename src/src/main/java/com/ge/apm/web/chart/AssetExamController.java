package com.ge.apm.web.chart;

import com.ge.apm.domain.BatchAssetExam;
import com.ge.apm.service.analysis.AssetExamDataAggregator;
import com.ge.apm.service.analysis.AssetExamDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lsg on 7/3/2017.
 */
@Controller
@RequestMapping("/assetExam")
public class AssetExamController {

    @Autowired
    AssetExamDataService assetExamDataService;
    @Autowired
    AssetExamDataAggregator assetExamDataAggregator;

    @RequestMapping(value = "/excute", method = RequestMethod.GET)
    @ResponseBody
    public String excuteTask() {
        assetExamDataAggregator.aggregateExamData();
        return "success";
    }

    @RequestMapping(value = "/aggreFromTo", method = RequestMethod.GET)
    @ResponseBody
    public String aggreFromTo(@RequestBody BatchAssetExam batchAssetExam) {
        assetExamDataService.aggrateExamebyRange(batchAssetExam);
        return "success";
    }

    @RequestMapping(value = "/aggreByDay", method = RequestMethod.GET)
    @ResponseBody
    public String aggreByDay(@RequestBody BatchAssetExam batchAssetExam) {
        assetExamDataService.assetExamAggregatorByday(batchAssetExam);
        return "success";
    }
}
