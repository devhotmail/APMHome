package com.ge.apm.service.utils;

import javaslang.collection.List;
import javaslang.control.Try;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

public class AopAdvices {
  private final Logger log = LoggerFactory.getLogger(AopAdvices.class);

  public Object logTag(ProceedingJoinPoint point) throws Throwable {
    return Try.of(point::proceed)
      .onSuccess(response -> log.info("method: {}, result: {}", point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), response))
      .onFailure(throwable -> log.warn("method: {}, stackTrace: {}", point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), throwable.getStackTrace()))
      .getOrElseThrow(Function.identity());
  }

  public Object logRequest(ProceedingJoinPoint point) throws Throwable {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    return Try.of(point::proceed)
      .onSuccess(response -> log.info("remoteAddr:{} , url: {}, method: {}, response: {}", request.getRemoteAddr(), request.getRequestURL(), point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), response))
      .onFailure(throwable -> log.warn("remoteAddr:{} , url: {}, method: {}, stackTrace: {}", request.getRemoteAddr(), request.getRequestURL(), point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), throwable.getStackTrace()))
      .getOrElseThrow(Function.identity());
  }
}
