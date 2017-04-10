package com.ge.apm.service.utils;

import com.google.common.collect.ObjectArrays;
import javaslang.collection.List;
import javaslang.control.Option;
import javaslang.control.Try;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Arrays;
import java.util.function.Function;

import static javaslang.API.*;
import static javaslang.Patterns.Some;

public class AopAdvices {
  private final Logger log = LoggerFactory.getLogger(AopAdvices.class);

  public static class RequestUrlWrapper extends HttpServletRequestWrapper {
    final private HttpServletRequest originalRequest;

    RequestUrlWrapper(HttpServletRequest request) {
      super(request);
      this.originalRequest = request;
    }

    @Override
    public StringBuffer getRequestURL() {
      return Match(System.getenv()).of(
        Case($(m -> m.containsKey("VCAP_APPLICATION")), Option.of(originalRequest.getRequestURL()).map(url -> new StringBuffer(url.toString().replace("http://", "https://"))).getOrElse(new StringBuffer())),
        Case($(), originalRequest.getRequestURL())
      );
    }
  }

  public Object logTag(ProceedingJoinPoint point) throws Throwable {
    return Try.of(point::proceed)
      .onSuccess(response -> log.info("method: {}, result: {}", point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), response))
      .onFailure(throwable -> log.warn("method: {}, stackTrace: {}", point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), throwable.getStackTrace()))
      .getOrElseThrow(Function.identity());
  }

  public Object logRequest(ProceedingJoinPoint point) throws Throwable {
    return Try.of(point::proceed)
      .onSuccess(response -> log.info("method: {}, result: {}", point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), response))
      .onFailure(throwable -> log.warn("method: {}, stackTrace: {}", point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), throwable.getStackTrace()))
      .getOrElseThrow(Function.identity());
  }

  public Object fixUrl(ProceedingJoinPoint point) throws Throwable {
    return Match(Option.of(point.getArgs()).filter(args -> args.length > 0).filter(args -> args[0] instanceof HttpServletRequest && System.getenv().containsKey("VCAP_APPLICATION")).map(args -> ObjectArrays.concat(new RequestUrlWrapper((HttpServletRequest) args[0]), Arrays.copyOfRange(args, 1, args.length)))).of(
      Case(Some($()), (Object[] objects) -> Try.of(() -> point.proceed(objects))),
      Case($(), Try.of(point::proceed))
    ).getOrElseThrow(Function.identity());
  }
}
