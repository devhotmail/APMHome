package com.ge.apm.web;

import com.ge.apm.service.api.CommonService;
import com.ge.apm.service.api.ExamSummitService;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/examsummit")
@Validated
public class ExamSummitApi {
  @Autowired
  private CommonService commonService;

  @Autowired
  private ExamSummitService examSummitService;

  @RequestMapping(path = "/generate", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<? extends Map<String, Object>> brief(HttpServletRequest request) {
    examSummitService.testGenerate();
    return ResponseEntity.accepted().body(ImmutableMap.of("status", "in progress"));
  }

}
