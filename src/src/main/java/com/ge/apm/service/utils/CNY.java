package com.ge.apm.service.utils;

import javaslang.Tuple;
import javaslang.Tuple2;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.MonetaryAmountDecimalFormatBuilder;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import java.math.BigDecimal;
import java.util.Locale;

public interface CNY {
  final static MonetaryAmountFormat fmt = MonetaryAmountDecimalFormatBuilder.of("###.#", Locale.CHINA).build();
  final static Money O = Money.zero(Monetary.getCurrency(Locale.CHINA));
  final static Money K = Money.of(1_000, Monetary.getCurrency(Locale.CHINA));
  final static Money W = Money.of(10_000, Monetary.getCurrency(Locale.CHINA));
  final static Money M = Money.of(1_000_000, Monetary.getCurrency(Locale.CHINA));
  final static Money Y = Money.of(100_000_000, Monetary.getCurrency(Locale.CHINA));
  final static Money B = Money.of(1_000_000_000, Monetary.getCurrency(Locale.CHINA));

  public static Money money(Number number) {
    return Money.of(number, Monetary.getCurrency(Locale.CHINA));
  }

  public static Money money(BigDecimal number) {
    return Money.of(number, Monetary.getCurrency(Locale.CHINA));
  }


  public static String format(MonetaryAmount amount) {
    return fmt.format(amount);
  }

  public static String formatInW(MonetaryAmount amount) {
    return format(amount.divide(W.getNumber())).concat("万");
  }

  // @formatter:off
  public static Tuple2<String,String> desc(MonetaryAmount amount) {
    if(amount.abs().isGreaterThan(W)) return Tuple.of(format(amount.divide(10_000d)),"万"); else return Tuple.of(format(amount),"元");
  }
  // @formatter:on


  public static MonetaryAmount parse(CharSequence text) {
    return fmt.parse(text);
  }
}
