package com.ge.apm.service.api;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.Seq;
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

}
