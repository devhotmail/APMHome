package com.ge.apm.service.utils;

import com.google.common.collect.ObjectArrays;
import javaslang.API;
import javaslang.Tuple;
import javaslang.collection.List;
import javaslang.control.Option;
import javaslang.control.Try;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.function.Function;

import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.Patterns.Some;

public class AopAdvices {
  private final Logger log = LoggerFactory.getLogger(AopAdvices.class);

  public static class RequestInterceptor {
    private final HttpServletRequest request;

    RequestInterceptor(HttpServletRequest request) {
      this.request = request;
    }

    public StringBuffer getRequestURL() {
      return API.Match(Tuple.of(System.getenv(), request)).of(
        Case($(t -> t._1.containsKey("VCAP_APPLICATION")), Option.of(request).map(req -> new StringBuffer(req.getRequestURL().toString().replace("http", "https"))).getOrElse(new StringBuffer())),
        Case($(), Option.of(request).map(HttpServletRequest::getRequestURL).getOrElse(new StringBuffer()))
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
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    return Try.of(point::proceed)
      .onSuccess(response -> log.info("remoteAddr:{} , url: {}, method: {}, response: {}", request.getRemoteAddr(), request.getRequestURL(), point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), response))
      .onFailure(throwable -> log.warn("remoteAddr:{} , url: {}, method: {}, stackTrace: {}", request.getRemoteAddr(), request.getRequestURL(), point.getTarget().getClass().getCanonicalName().concat(point.getSignature().getName().concat("(").concat(List.of(point.getArgs()).mkString(",")).concat(")")), throwable.getStackTrace()))
      .getOrElseThrow(Function.identity());
  }

  public Object fixUrl(ProceedingJoinPoint point) throws Throwable {
    Class<? extends HttpServletRequest> fixedReq = new ByteBuddy()
      .subclass(HttpServletRequest.class)
      .method(ElementMatchers.named("getRequestURL"))
      .intercept(MethodDelegation.to(new RequestInterceptor(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest())))
      .make().load(getClass().getClassLoader()).getLoaded();
    log.info("req.getRequestURL(): {}", fixedReq.newInstance().getRequestURL());
    return API.Match(Option.of(point.getArgs()).filter(args -> args.length > 0).filter(args -> args[0] instanceof HttpServletRequest).map(args -> ObjectArrays.concat(Try.of(() -> (Object) fixedReq.newInstance()).getOrElse(args[0]), Arrays.copyOfRange(args, 1, args.length)))).of(
      Case(Some($()), (Object[] objects) -> Try.of(() -> point.proceed(objects))),
      Case($(), Try.of(point::proceed))
    ).getOrElseThrow(Function.identity());
  }
}
