package com.ge.apm.web;


import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.api.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Map;

@RestController
@RequestMapping("/assets")
@Validated
public class AssetsApi {
  private static final Logger log = LoggerFactory.getLogger(AssetsApi.class);
  @Autowired
  private CommonService commonService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> handleRequest(HttpServletRequest request,
                                                           @Pattern(regexp = "type|dept|supplier|revenue|yoa") @RequestParam(value = "orderby", required = false) String orderBy,
                                                           @Min(1) @Max(50) @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                                           @Min(0) @RequestParam(value = "start", required = false, defaultValue = "0") Integer start) {
    log.info("orderBy:{}, limit:{}, start:{}", orderBy, limit, start);
    UserAccount user = UserContext.getCurrentLoginUser();
    Map<Integer, String> groups = commonService.findFields(user.getSiteId(), "assetGroup");
    Map<Integer, String> depts = commonService.findDepts(user.getSiteId(), user.getHospitalId());


    return null;
  }

}
