package com.ge.apm.web;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.OrgService;
import com.google.common.collect.ImmutableMap;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/org")
@Validated
public class OrgApi {
  private static final Logger log = LoggerFactory.getLogger(AssetsApi.class);

  @Autowired
  private OrgService orgService;

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<? extends Map<String, Object>> requestOne(HttpServletRequest request,
                                                                  @Min(1) @PathVariable Integer id) {
    return Try.of(UserContext::getCurrentLoginUser).filter(UserAccount::getIsSuperAdmin).map(u ->
      ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
        .body(new ImmutableMap.Builder<String, Object>()
          .put("orgInfo", Try.of(() -> orgService.find(id)).map(t -> new ImmutableMap.Builder<String, Object>().
            put("id", t._1)
            .put("parent_id", t._2)
            .put("site_id", t._3)
            .put("hospital_id", t._4)
            .put("name", t._5)
            .put("name_en", t._6).build()).getOrElse(ImmutableMap.of())).build()))
      .getOrElse(ResponseEntity.status(HttpStatus.FORBIDDEN).body(ImmutableMap.of("error", "Only admin can have access")));
  }


  @RequestMapping(path = "/all", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<? extends Map<String, Object>> requestAll(HttpServletRequest request,
                                                                  @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(value = "limit", required = false, defaultValue = "30") Integer limit,
                                                                  @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
    return Try.of(UserContext::getCurrentLoginUser).map(u ->
      ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
        .body(new ImmutableMap.Builder<String, Object>()
          .put("orgInfos", StreamSupport.stream(orgService.find(u.getSiteId(), u.getHospitalId(), start, limit).spliterator(), false)
            .map(t -> new ImmutableMap.Builder<String, Object>().
              put("id", t._1)
              .put("parent_id", t._2)
              .put("site_id", t._3)
              .put("hospital_id", t._4)
              .put("name", t._5)
              .put("name_en", t._6).build())
            .collect(Collectors.toList())).build()))
      .getOrElse(ResponseEntity.badRequest().body(ImmutableMap.of("error", "on getting login user profile")));
  }

}
