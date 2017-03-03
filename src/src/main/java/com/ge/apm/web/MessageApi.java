package com.ge.apm.web;

import com.ge.apm.domain.I18nMessage;
import com.ge.apm.service.api.CommonService;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import javaslang.control.Option;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webapp.framework.web.service.DbMessageSource;
import webapp.framework.web.service.UserContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/msg")
@Validated
public class MessageApi {
  private static final Logger log = LoggerFactory.getLogger(AssetsApi.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @Autowired
  private CommonService commonService;


  @RequestMapping(path = "/all", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> requestAll(HttpServletRequest request,
                                                        @Min(1) @Max(Integer.MAX_VALUE) @RequestParam(value = "limit", required = false, defaultValue = "30") Integer limit,
                                                        @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
      .body(new ImmutableMap.Builder<String, Object>()
        .put("pages", new ImmutableMap.Builder<String, Object>().put("total", StreamSupport.stream(commonService.findAllMessages().spliterator(), true).count()).put("limit", limit).put("start", start).build())
        .put("messages", StreamSupport.stream(commonService.findAllMessages().spliterator(), false).skip(start).limit(limit)
          .map(t -> new ImmutableMap.Builder<String, Object>()
            .put("id", Option.of(t._1()).getOrElse(0))
            .put("msg_type", Option.of(t._2()).getOrElse(""))
            .put("msg_key", Option.of(t._3()).getOrElse(""))
            .put("value_zh", Option.of(t._4()).getOrElse(""))
            .put("value_en", Option.of(t._5()).getOrElse(""))
            .put("value_tw", Option.of(t._6()).getOrElse(""))
            .put("site_id", Option.of(t._7()).getOrElse(0)).build())
          .sorted(Comparator.comparingInt(m -> Option.of(Ints.tryParse(m.get("id").toString())).getOrElse(0)))
          .collect(Collectors.toList())).build());
  }

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, String>> requestByType(HttpServletRequest request,
                                                           @RequestParam(value = "type", required = true) String type) {
    return Try.of(() -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(commonService.findFields(UserContext.getCurrentLoginUser().getSiteId(), type))).getOrElseGet(e -> ResponseEntity.badRequest().body(ImmutableMap.of("error", Arrays.toString(e.getStackTrace()))));
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<I18nMessage> requestById(HttpServletRequest request,
                                                 @Min(1) @PathVariable Integer id) {
    return Option.of(DbMessageSource.getMessageCache(UserContext.getCurrentLoginUser().getSiteId()).values().stream().parallel().filter(m -> m.getId().equals(id)).findFirst().orElse(null)).map(m -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).body(m)).getOrElse(ResponseEntity.badRequest().body(new I18nMessage()));
  }

}
