package com.get.apm.api.ut;

import com.ge.apm.service.utils.CNY;
import javaslang.Tuple;
import org.assertj.core.api.Assertions;
import org.javamoney.moneta.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Locale;


public class CnyUnitTest {
  private CurrencyUnit cny;

  @Before
  public void setUp() {
    cny = Monetary.getCurrency(Locale.CHINA);
  }

  @After
  public void tearDown() {

  }

  @Test
  public void testConstants() {
    Assertions.assertThat(Money.zero(cny)).isEqualTo(CNY.O);
    Assertions.assertThat(Money.of(1000.00d, cny)).isEqualTo(CNY.K);
    Assertions.assertThat(Money.of(10000.00d, cny)).isEqualTo(CNY.W);
    Assertions.assertThat(Money.of(1000_0.00d, cny)).isEqualTo(CNY.W);
    Assertions.assertThat(Money.of(1000_000.00d, cny)).isEqualTo(CNY.M);
    Assertions.assertThat(Money.of(100_000_000.00d, cny)).isEqualTo(CNY.Y);
    Assertions.assertThat(Money.of(1000_000_000.00d, cny)).isEqualTo(CNY.B);
    Assertions.assertThat(Money.of(1000_000_000.01d, cny)).isNotEqualTo(CNY.B);
  }

  @Test
  public void testFormat() {
    Assertions.assertThat(Money.of(1d, cny)).isEqualTo(CNY.money(1));
    Assertions.assertThat(Money.of(1.01d, cny)).isNotEqualTo(CNY.money(1));
    Assertions.assertThatThrownBy(() -> CNY.money(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testMoneyBigDecimal() {
    Assertions.assertThat(Money.of(1050_904_936.00d, cny)).isEqualTo(CNY.money(1050_904_936.00d));
    Assertions.assertThat(Money.of(1050_904_936.00d, cny)).isNotEqualTo(CNY.money(1050_904_936.01d));

  }

  @Test
  public void testDesc() {
    Assertions.assertThat(CNY.desc(Money.of(556.987d, cny)).equals(Tuple.of("556.987", "元"))).isFalse();
    Assertions.assertThat(CNY.desc(Money.of(461_987_556.987d, cny)).equals(Tuple.of("46198.7556987", "万"))).isFalse();
    Assertions.assertThat(CNY.desc(Money.of(556.8d, cny))).isEqualTo(Tuple.of("556.8", "元"));
    Assertions.assertThat(CNY.desc(Money.of(556.888d, cny))).isNotEqualTo(Tuple.of("556.888", "元"));
    Assertions.assertThat(CNY.desc(Money.of(461_987_556.9d, cny))).isNotEqualTo(Tuple.of("46198.75569", "万"));
    Assertions.assertThat(CNY.desc(Money.of(461_987_556.987d, cny))).isNotEqualTo(Tuple.of("46198.7556987", "万"));
    Assertions.assertThat(CNY.desc(Money.of(556.987d, cny))).isNotEqualTo(Tuple.of("556.907", "元"));
    Assertions.assertThat(CNY.desc(Money.of(461_987_556.987d, cny))).isNotEqualTo(Tuple.of("46198.7558987", "万"));
    Assertions.assertThatThrownBy(() -> CNY.desc(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testParse() {
    Assertions.assertThat(CNY.parse("356.81")).isEqualTo(Money.of(356.81d, cny));
    Assertions.assertThat(CNY.parse("98451356.81")).isEqualTo(Money.of(98_451_356.81d, cny));
    Assertions.assertThat(CNY.parse("356.81")).isNotEqualTo(Money.of(356.82d, cny));
    Assertions.assertThat(CNY.parse("98451356.81")).isNotEqualTo(Money.of(98_451_356.96d, cny));
    //Assertions.assertThatThrownBy(() -> CNY.parse("98451ab56.81")).isInstanceOf(MonetaryParseException.class);
    }
}
