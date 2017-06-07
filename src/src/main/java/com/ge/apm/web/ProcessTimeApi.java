/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.ProcessTimeService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javaslang.control.Option;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rx.Observable;
import webapp.framework.web.service.UserContext;

/**
 *
 * @author 212579464
 */
@RestController
@RequestMapping("/process")
@Validated
public class ProcessTimeApi {

    @Autowired
    private ProcessTimeService ptService;

    @Autowired
    private CommonService commonService;

    @RequestMapping(path = "/brief", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> queryListByGroup(
            @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime fromTime,
            @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime toTime,
            @RequestParam(name = "start", required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "dept", required = false, defaultValue = "0") Integer deptId,
            @RequestParam(name = "type", required = false, defaultValue = "0") Integer typeId,
            @RequestParam(name = "supplier", required = false, defaultValue = "0") Integer supplier,
            @Pattern(regexp = "ETTR|arrived|respond") @RequestParam(name = "orderby", required = false, defaultValue = "ETTR") String orderBy,
            @Pattern(regexp = "type|dept|supplier") @RequestParam(name = "groupby", required = false, defaultValue = "type") String groupby) {

        UserAccount user = UserContext.getCurrentLoginUser();

        Map<String, String> idMap = groupby.contains("type") ? Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(e.getKey()).isDefined()).toMap(e -> e.getKey(), Map.Entry::getValue).toBlocking().single()
                : (groupby.contains("supplier") ? Observable.from(commonService.findSuppliers(user.getSiteId()).entrySet()).toMap(e -> e.getKey().toString(), Map.Entry::getValue).toBlocking().single() : null);
        ImmutableMap<String, Integer> pageInfo = ptService.queryCount(user.getSiteId(), user.getHospitalId(), fromTime, toTime, groupby, typeId, deptId, supplier)
                .map(count -> ImmutableMap.of("total", count, "start", offset, "limit", Math.min(count - offset, limit))).toBlocking().single();

        Iterable<ImmutableMap<String, Object>> result = ptService.queryListByType(user.getSiteId(), user.getHospitalId(), fromTime, toTime, orderBy, groupby, typeId, deptId, supplier, offset, limit)
                .map(t -> new ImmutableMap.Builder<String, Object>().put("id", t._1).put("name", groupby.contains("dept") ? t._2 : idMap.getOrDefault(t._1, "")).put("respond", t._3).put("arrived", t._4).put("ETTR", t._5).build()).toBlocking().toIterable();

        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data", new ImmutableList.Builder<ImmutableMap<String, Object>>().addAll(result).build())
                .put("page", pageInfo).build();

        return ResponseEntity.ok().body(body);
    }

    @RequestMapping(path = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> queryListByAsset(
            @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime fromTime,
            @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime toTime,
            @RequestParam(name = "start", required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "dept", required = false, defaultValue = "0") Integer deptId,
            @RequestParam(name = "type", required = false, defaultValue = "0") Integer typeId,
            @RequestParam(name = "supplier", required = false, defaultValue = "0") Integer supplier,
            @Pattern(regexp = "ETTR|arrived|respond") @RequestParam(name = "orderby", required = false, defaultValue = "ETTR") String orderBy) {

        UserAccount user = UserContext.getCurrentLoginUser();
        ImmutableMap<String, Integer> pageInfo = ptService.queryCount(user.getSiteId(), user.getHospitalId(), fromTime, toTime, "assetId", typeId, deptId, supplier)
                .map(count -> ImmutableMap.of("total", count, "start", offset, "limit", Math.min(count - offset, limit))).toBlocking().single();

        Iterable<ImmutableMap<String, Object>> result = ptService.queryListByAsset(user.getSiteId(), user.getHospitalId(), fromTime, toTime, orderBy, typeId, deptId, supplier, offset, limit)
                .map(t -> new ImmutableMap.Builder<String, Object>().put("id", t._1).put("name", t._2).put("respond", t._3).put("arrived", t._4).put("ETTR", t._5)
                .put("dispatchTime", t._6).put("workingTime", t._7).build()).toBlocking().toIterable();

        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data", new ImmutableList.Builder<ImmutableMap<String, Object>>().addAll(result).build())
                .put("page", pageInfo).build();
        return ResponseEntity.ok().body(body);

    }

    @RequestMapping(path = "/gross", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> queryGross(
            @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime fromTime,
            @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime toTime,
            @RequestParam(name = "dept", required = false, defaultValue = "0") Integer deptId,
            @RequestParam(name = "supplier", required = false, defaultValue = "0") Integer supplier,
            @RequestParam(name = "type", required = false, defaultValue = "0") Integer typeId) {

        UserAccount user = UserContext.getCurrentLoginUser();
        
        Integer count = ptService.queryCount(user.getSiteId(), user.getHospitalId(), fromTime, toTime, "WorkOrder", typeId, deptId, supplier).toBlocking().single();
        
        if(count==0){
            Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data",new ImmutableMap.Builder<String, Object>().put("respond", 0).put("arrived", 0).put("ETTR", 0).put("dispatchTime", 0)
                        .put("workingTime", 0).put("ETTR75", 0).put("ETTR95", 0).build()).build();
            return ResponseEntity.ok().body(body);
        }
            
        Integer ettr75 = ptService.queryDistribution(user.getSiteId(), user.getHospitalId(), fromTime, toTime, typeId, deptId, supplier, count*75/100).toBlocking().single();
        Integer ettr95 = ptService.queryDistribution(user.getSiteId(), user.getHospitalId(), fromTime, toTime, typeId, deptId, supplier, count*95/100).toBlocking().single();

        ImmutableMap<String, Integer> result = ptService.queryGross(user.getSiteId(), user.getHospitalId(), fromTime, toTime, typeId, deptId, supplier)
                .map(t -> ImmutableMap.of("respond", t._1, "arrived", t._2, "ETTR", t._3, "dispatchTime", t._4, "workingTime", t._5)).toBlocking().single();

        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data",new ImmutableMap.Builder<String, Object>().putAll(result).put("ETTR75", ettr75).put("ETTR95", ettr95).build()).build();
        return ResponseEntity.ok().body(body);
        
    }

    @RequestMapping(path = "/phase", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> queryCountsOfPhase(
            @Past @RequestParam(name = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime fromTime,
            @Past @RequestParam(name = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime toTime,
            @RequestParam(name = "dept", required = false, defaultValue = "0") Integer deptId,
            @RequestParam(name = "supplier", required = false, defaultValue = "0") Integer supplier,
            @RequestParam(name = "type", required = false, defaultValue = "0") Integer typeId,
            @RequestParam(name = "asset", required = false, defaultValue = "0") Integer assetId,
            @RequestParam(name = "t1", required = false, defaultValue = "0") Integer t1,
            @RequestParam(name = "t2", required = false, defaultValue = "0") Integer t2,
            @RequestParam(name = "tmax", required = false, defaultValue = "0") Integer tmax,
            @Pattern(regexp = "ETTR|arrived|respond") @RequestParam(name = "phase", required = false, defaultValue = "ETTR") String phase) {

        UserAccount user = UserContext.getCurrentLoginUser();

        ImmutableMap<String, Integer> result = ptService.queryCountsOfPhase(user.getSiteId(), user.getHospitalId(), fromTime, toTime, typeId, deptId, supplier,assetId,phase,t1,t2,tmax)
                .map(t -> ImmutableMap.of("phase1", t._1, "phase2", t._2, "phase3", t._3, "phase4", t._4)).toBlocking().single();

        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data", result).build();
        return ResponseEntity.ok().body(body);
    }

}
