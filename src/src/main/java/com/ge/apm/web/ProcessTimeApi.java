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
import com.google.common.primitives.Ints;
import java.time.LocalDate;
import java.util.Map;
import javaslang.control.Option;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(path = "/{groupby}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> queryListByGroup(
            @RequestParam(name = "startTime", required = false, defaultValue = "2015-01-01") String fromTime,
            @RequestParam(name = "endTime", required = false, defaultValue = "2018-01-01") String toTime,
            @Pattern(regexp = "ETTR|arrived|respond") @RequestParam(name = "orderby", required = false, defaultValue = "ETTR") String orderBy,
            @Pattern(regexp = "type|dept") @PathVariable String groupby) {

        UserAccount user = UserContext.getCurrentLoginUser();
        Map<String, String> types = Observable.from(commonService.findFields(user.getSiteId(), "assetGroup").entrySet()).filter(e -> Option.of(Ints.tryParse(e.getKey())).isDefined()).toMap(e -> e.getKey(), Map.Entry::getValue).toBlocking().single();

        Iterable<ImmutableMap<String,String>> result = ptService.queryListByType(user.getSiteId(), user.getHospitalId(), LocalDate.parse(fromTime), LocalDate.parse(toTime), orderBy,groupby)
                .map(t -> ImmutableMap.of("id",t._1,"name",groupby.contains("type")?types.getOrDefault(t._1, ""):t._2,"respond", t._3,"arrived", t._4,"ETTR", t._5)).toBlocking().toIterable();

        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data",new ImmutableList.Builder<ImmutableMap<String,String>>().addAll(result).build()).build();

        return ResponseEntity.ok().body(body);
    }
    
    @RequestMapping(path = "/assets", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> queryAssetList(@RequestParam(name = "startTime", required = false, defaultValue = "2015-01-01") String fromTime,
            @RequestParam(name = "endTime", required = false, defaultValue = "2018-01-01") String toTime,
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(name = "deptId", required = false, defaultValue = "0") Integer deptId,
            @RequestParam(name = "typeId", required = false, defaultValue = "0") Integer typeId,
            @Pattern(regexp = "ETTR|arrived|respond") @RequestParam(name = "orderby", required = false, defaultValue = "ETTR") String orderBy){
        
        UserAccount user = UserContext.getCurrentLoginUser();
        Iterable<ImmutableMap<String,String>> result = ptService.queryListByAsset(user.getSiteId(), user.getHospitalId(), LocalDate.parse(fromTime), LocalDate.parse(toTime), orderBy, typeId, deptId, offset, limit)
                .map(t -> ImmutableMap.of("id",t._1,"name",t._2,"respond", t._3,"arrived", t._4,"ETTR", t._5)).toBlocking().toIterable();
        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data",new ImmutableList.Builder<ImmutableMap<String,String>>().addAll(result).build()).build();
        return ResponseEntity.ok().body(body);
    
    }
        
    @RequestMapping(path = "/gross", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> queryGross(@RequestParam(name = "startTime", required = false, defaultValue = "2015-01-01") String fromTime,
            @RequestParam(name = "endTime", required = false, defaultValue = "2018-01-01") String toTime,
            @RequestParam(name = "deptId", required = false, defaultValue = "0") Integer deptId,
            @RequestParam(name = "typeId", required = false, defaultValue = "0") Integer typeId){
        
        UserAccount user = UserContext.getCurrentLoginUser();
        
        ImmutableMap<String,String> result = ptService.queryGross(user.getSiteId(), user.getHospitalId(),LocalDate.parse(fromTime), LocalDate.parse(toTime),typeId, deptId)
                .map(t -> ImmutableMap.of("respond", t._1,"arrived", t._2,"ETTR", t._3)).toBlocking().single();
        
        Map<String, Object> body = new ImmutableMap.Builder<String, Object>()
                .put("data",result).build();
        return ResponseEntity.ok().body(body);
    }
}
