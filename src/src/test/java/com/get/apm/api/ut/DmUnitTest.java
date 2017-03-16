package com.get.apm.api.ut;


import com.google.common.math.Stats;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple4;
import javaslang.Tuple5;
import javaslang.collection.List;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.util.Map;

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

  @Test
  public void resolveObservable() {
    // averageUsage(p).forEach(kv -> System.out.println(kv));
    System.out.println(averageUsage(p));
  }

}
