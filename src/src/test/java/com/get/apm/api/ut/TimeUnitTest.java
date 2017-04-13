package com.get.apm.api.ut;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TimeUnitTest {
  @Test
  public void testDaysBetween() {
    Assertions.assertThat(ChronoUnit.DAYS.between(LocalDate.of(2017, 1, 1), LocalDate.ofYearDay(2017, 365))).isEqualTo(364L);
    Assertions.assertThat(ChronoUnit.DAYS.between(LocalDate.of(2000, 1, 1), LocalDate.ofYearDay(2017, 365))).isEqualTo(6574);
    Assertions.assertThat(ChronoUnit.DAYS.between(LocalDate.parse("2000-01-01"), LocalDate.parse("2017-12-31"))).isEqualTo(6574);
  }

  @Test
  public void testPeriod() {
    Assertions.assertThat(LocalDate.now()).isAfterOrEqualTo(LocalDate.ofYearDay(LocalDate.now().getYear(), 1));
    Assertions.assertThat(LocalDate.now()).isBeforeOrEqualTo(LocalDate.ofYearDay(LocalDate.now().getYear(), 365));
  }

}
