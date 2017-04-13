package com.get.apm.api.ut;


import com.ge.apm.service.api.ProfitService;
import com.ge.apm.service.utils.CNY;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.UrlEscapers;
import javaslang.Tuple;
import javaslang.control.Option;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Strings;
import org.junit.Test;
import rx.Observable;
import rx.observables.JoinObservable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ProfitApiUnitTest {

  @Test
  public void testJoin() {
    Observable<ImmutableMap<Integer, String>> left = Observable.just(ImmutableMap.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e"));
    Observable<Integer> right = Observable.just(1, 2, 3);
    Assertions.assertThat(JoinObservable.when(JoinObservable.from(left.repeat(right.countLong().toBlocking().single())).and(right).then(ImmutableMap::get)).toObservable().toBlocking().firstOrDefault("")).isEqualTo("a");
  }

  @Test
  public void testSorted() {
    Assertions.assertThat(Observable.just(Tuple.of("a", 101d, 203d), Tuple.of("a", 121d, 313d), Tuple.of("a", 501d, 33d), Tuple.of("a", 21d, 63d), Tuple.of("a", 6d, 23d))
      .sorted((l, r) -> l._3.intValue() - r._3.intValue()).toBlocking().last()).isEqualTo(Tuple.of("a", 121d, 313d));
  }

  @Test
  public void testUrlEscape() {
    Assertions.assertThat(UrlEscapers.urlPathSegmentEscaper().escape("放射科")).isEqualToIgnoringCase("%E6%94%BE%E5%B0%84%E7%A7%91");
  }

  @Test
  public void testSmallerString() {
    Assertions.assertThat(Option.of(Tuple.of("abc", "a")).map(t -> !Strings.isNullOrEmpty(t._2) && t._2.length() > t._1.length() ? t._2 : t._1).getOrElse("")).isEqualToIgnoringCase("abc");
  }

  @Test
  public void testPredictRevenue() {
    Assertions.assertThat(Option.of(LocalDate.parse("2017-12-31")).map(y -> CNY.money(15284.13D * ChronoUnit.DAYS.between(LocalDate.of(2000, 1, 1), y) - 70223735.95D)).getOrElse(CNY.O).getNumber().longValue()).isEqualTo(Double.valueOf(30254134.67D).longValue());
    Assertions.assertThat(ProfitService.predictRevenue()).isGreaterThan(CNY.money(30254134.67D));
  }

}
