package com.get.apm.api.ut;


import com.google.common.math.Stats;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple4;
import javaslang.Tuple5;
import javaslang.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.util.Map;

import static javaslang.API.*;

public class DmUnitTest {
  private Observable<Tuple5<Integer, Double, String, Integer, Double>> p;

  @Before
  public void setUp() {
    p = Observable.just(Tuple.of(77, 12345d, "as0", 1, 0.6d),
      Tuple.of(78, 12345d, "as1", 1, 0.6d),
      Tuple.of(79, 12345d, "as2", 2, 0.6d),
      Tuple.of(80, 12345d, "as3", 2, 0.6d),
      Tuple.of(81, 12345d, "as4", 3, 0.6d),
      Tuple.of(82, 12345d, "as5", 3, 0.6d),
      Tuple.of(83, 12345d, "as6", 5, 0.6d));
  }


  private Map<Integer, Tuple2<Double, List<Tuple4<Integer, Double, String, Double>>>> averageUsage(Observable<Tuple5<Integer, Double, String, Integer, Double>> p) {
    return List.ofAll(p.toBlocking().toIterable()).groupBy(t -> t._4).mapValues(lst -> Tuple.of(Stats.of(lst.map(t -> t._5).toJavaList()).mean(), lst.map(t -> Tuple.of(t._1, t._2, t._3, t._5)))).toJavaMap();
  }

  private String dm(Double maxUsage, Double averageUsage) {
    return Match(Tuple.of(maxUsage, averageUsage)).of(
      Case($(t -> t._2 > 1D), "建议购买新设备"),
      Case($(t -> t._1 > 1D), "建议合理安排使用"),
      Case($(t -> t._2 <= 0.3D), "建议提高使用率"),
      Case($(), "")
    );
  }

  @Test
  public void resolveObservable() {
    Assertions.assertThat(averageUsage(p)).containsKeys(1, 2, 3, 5);
  }

  @Test
  public void testDm() {
    Assertions.assertThat(dm(1.03D, 0.25D)).isEqualToIgnoringCase("建议合理安排使用");
    Assertions.assertThat(dm(1.23D, 1.05D)).isEqualToIgnoringCase("建议购买新设备");
    Assertions.assertThat(dm(0.53D, 0.25D)).isEqualToIgnoringCase("建议提高使用率");
    Assertions.assertThat(dm(0.53D, 0.35D)).isEqualToIgnoringCase("");

  }

}
