package com.ge.apm.service.utils;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.MonetaryAmountDecimalFormatBuilder;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import java.math.BigDecimal;
import java.util.Locale;

public interface CNY {
  public final static MonetaryAmountFormat fmt = MonetaryAmountDecimalFormatBuilder.of(",###.##", Locale.CHINA).build();
  public final static Money O = Money.zero(Monetary.getCurrency(Locale.CHINA));
  public final static Money K = Money.of(1_000, Monetary.getCurrency(Locale.CHINA));
  public final static Money W = Money.of(10_000, Monetary.getCurrency(Locale.CHINA));
  public final static Money M = Money.of(1_000_000, Monetary.getCurrency(Locale.CHINA));
  public final static Money Y = Money.of(100_000_000, Monetary.getCurrency(Locale.CHINA));
  public final static Money B = Money.of(1_000_000_000, Monetary.getCurrency(Locale.CHINA));

  public static Money money(Number number) {
    return Money.of(number, Monetary.getCurrency(Locale.CHINA));
  }

  public static Money money(BigDecimal number) {
    return Money.of(number, Monetary.getCurrency(Locale.CHINA));
  }


  public static String format(MonetaryAmount amount) {
    return fmt.format(amount);
  }

  // @formatter:off
  public static String desc(MonetaryAmount amount) {
    if(amount.abs().isGreaterThan(W)) return format(amount.divide(10_000d)).concat("万"); else return format(amount).concat("元");
  }
  // @formatter:on

  public static MonetaryAmount parse(CharSequence text) {
    return fmt.parse(text);
  }
}
