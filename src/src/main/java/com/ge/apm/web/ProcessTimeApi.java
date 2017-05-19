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
        ImmutableMap<String, String> pageInfo = ptService.queryCount(user.getSiteId(), user.getHospitalId(), fromTime, toTime, groupby, typeId, deptId, supplier)
                .map(count -> ImmutableMap.of("total", count.toString(), "start", offset.toString(), "limit", String.valueOf(Math.min(count - offset, limit)))).toBlocking().single();

        Iterable<ImmutableMap<String, String>> result = ptService.queryListByType(user.getSiteId(), user.getHospitalId(), fromTime, toTime, orderBy, groupby, typeId, deptId, supplier, offset, limit)
                .map(t -> ImmutableMap.of("id", t._1, "name", groupby.contains("dept") ? t._2 : idMap.getOrDefault(t._1, ""), "respond", t._3, "arrived", t._4, "ETTR", t._5)).toBlocking().toIterable();

        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data", new ImmutableList.Builder<ImmutableMap<String, String>>().addAll(result).build())
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
        ImmutableMap<String, String> pageInfo = ptService.queryCount(user.getSiteId(), user.getHospitalId(), fromTime, toTime, "assetId", typeId, deptId, supplier)
                .map(count -> ImmutableMap.of("total", count.toString(), "start", offset.toString(), "limit", String.valueOf(Math.min(count - offset, limit)))).toBlocking().single();

        Iterable<ImmutableMap<String, String>> result = ptService.queryListByAsset(user.getSiteId(), user.getHospitalId(), fromTime, toTime, orderBy, typeId, deptId, supplier, offset, limit)
                .map(t -> new ImmutableMap.Builder<String, String>().put("id", t._1).put("name", t._2).put("respond", t._3).put("arrived", t._4).put("ETTR", t._5)
                .put("dispatchTime", t._6).put("workingTime", t._7).build()).toBlocking().toIterable();

        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data", new ImmutableList.Builder<ImmutableMap<String, String>>().addAll(result).build())
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

        ImmutableMap<String, String> result = ptService.queryGross(user.getSiteId(), user.getHospitalId(), fromTime, toTime, typeId, deptId, supplier)
                .map(t -> ImmutableMap.of("respond", String.valueOf(t._1), "arrived", String.valueOf(t._2), "ETTR", String.valueOf(t._3), "dispatchTime", String.valueOf(t._4), "workingTime", String.valueOf(t._5))).toBlocking().single();

        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data", result).build();
        return ResponseEntity.ok().body(body);
    }
}
