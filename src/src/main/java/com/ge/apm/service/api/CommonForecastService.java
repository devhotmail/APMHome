package com.ge.apm.service.api;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple4;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.control.Option;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Common methods for Forecast service of other APIs.
 */
public class CommonForecastService {
  //transfer between X axis value and Date
  public Integer localDateToX(LocalDate input) {
    return (int) LocalDate.ofYearDay(2000, 1).until(input, ChronoUnit.DAYS);
  }

  public LocalDate xToLocaldate(Integer x) {
    return LocalDate.ofYearDay(2000, 1).plusDays(x);
  }

  /**
   * generate predicted data for a one type of asset
   *
   * @param monthlyData historical data
   * @param year        next year or the year after
   * @return X axis and the corresponding predicted data for a year
   */
  public Seq<Tuple2<Integer, Double>> predictOneType(Seq<Tuple2<Integer, Double>> monthlyData, int year) {
    monthlyData = monthlyData.removeLast(v -> true);
    LocalDate lastHisDay = xToLocaldate(monthlyData.last()._1);
    for (int i = 0; i < (int) lastHisDay.until(LocalDate.now().withDayOfMonth(1).minusMonths(1), ChronoUnit.MONTHS); i++) {
      monthlyData = monthlyData.append(Tuple.of(localDateToX(lastHisDay.plusMonths(i + 1)), 0D));
    }
    SimpleRegression simpleRegression = new SimpleRegression();
    monthlyData.forEach(v -> simpleRegression.addData(v._1, v._2));
    lastHisDay = xToLocaldate(monthlyData.last()._1);
    for (int i = 0; i < (int) lastHisDay.until(LocalDate.of(LocalDate.now().getYear() + 1, 12, 1), ChronoUnit.MONTHS); i++) {
      monthlyData = monthlyData.append(Tuple.of(localDateToX(lastHisDay.plusMonths(i + 1)), simpleRegression.predict(localDateToX(lastHisDay.plusMonths(i + 1)))));
    }
    return monthlyData.filter(v -> xToLocaldate(v._1).getYear() == year);
  }

  /**
   * return ratios used when mapping predicted data to single asset
   * currently use data of December last year
   *
   * @param items Asset info(id, time, group_id(usual asset type), measurement)
   * @return ratios for each group and asset. (group_id, id, ratio)
   */
  public Map<Integer, Seq<Tuple2<Integer, Double>>> ratios(Seq<Tuple4<Integer, LocalDate, Integer, Double>> items) {
    return items.filter(v -> LocalDate.of(LocalDate.now().getYear() - 1, 12, 1).equals(v._2))
      .groupBy(v -> v._3)
      .map((k, v) -> Tuple.of(Tuple.of(k, v.map(sub -> sub._4).sum().doubleValue()), v))
      .map((k, v) -> Tuple.of(k._1, v.map(sub -> Tuple.of(sub._1, Option.when(k._2.equals(0D), 0D).getOrElse(sub._4 / k._2)))));
  }


}
