package com.get.apm.api.ut;


import com.google.common.collect.ImmutableMap;
import com.google.common.net.UrlEscapers;
import javaslang.Tuple;
import javaslang.control.Option;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Strings;
import org.junit.Test;
import rx.Observable;
import rx.observables.JoinObservable;

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

}
