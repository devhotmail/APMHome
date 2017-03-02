package com.ge.apm.web;

import com.ge.apm.service.api.CommonService;
import com.google.common.collect.ImmutableMap;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/msg")
@Validated
public class MessageApi {
  private static final Logger log = LoggerFactory.getLogger(AssetsApi.class);
  @Autowired
  private CommonService commonService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, String>> requestByType(HttpServletRequest request,
                                                           @RequestParam(value = "type", required = true) String type) {
    return Try.of(() -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(commonService.findFields(UserContext.getCurrentLoginUser().getSiteId(), type))).getOrElseGet(e -> ResponseEntity.badRequest().body(ImmutableMap.of("error", Arrays.toString(e.getStackTrace()))));
  }
}
