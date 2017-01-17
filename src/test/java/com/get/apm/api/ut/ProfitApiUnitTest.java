package com.get.apm.api.ut;


import com.ge.apm.service.utils.CNY;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.UrlEscapers;
import javaslang.Tuple;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import rx.Observable;
import rx.observables.JoinObservable;

public class ProfitApiUnitTest {

  @Test
  public void testJoin() {
    Observable<ImmutableMap<Integer, String>> left = Observable.just(ImmutableMap.of(1, "a", 2, "b", 3, "c", 4, "d", 5, "e"));
    Observable<Integer> right = Observable.just(1, 2, 3);
    JoinObservable.when(JoinObservable.from(left.repeat(right.countLong().toBlocking().single())).and(right).then(ImmutableMap::get)).toObservable().subscribe(System.out::println);
  }

  @Test
  public void testSorted() {
    Observable.just(Tuple.of("a", 101d, 203d), Tuple.of("a", 121d, 313d), Tuple.of("a", 501d, 33d), Tuple.of("a", 21d, 63d), Tuple.of("a", 6d, 23d))
      .sorted((l, r) -> l._3.intValue() - r._3.intValue())
      .subscribe(System.out::println);
  }

  @Test
  public void testCny() {
    System.out.println(CNY.fmt.format(CNY.fmt.parse("10240.025")));
    Assertions.assertThat(CNY.O).isEqualTo(CNY.fmt.parse("0.00"));
  }

  @Test
  public void testUrlEscape() {
    System.out.println(UrlEscapers.urlPathSegmentEscaper().escape("放射科"));
  }
}
