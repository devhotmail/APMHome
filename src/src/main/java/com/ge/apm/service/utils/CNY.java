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

import static javaslang.API.*;

public interface CNY {
  MonetaryAmountFormat fmt = MonetaryAmountDecimalFormatBuilder.of("###.#", Locale.CHINA).build();
  Money O = Money.zero(Monetary.getCurrency(Locale.CHINA));
  Money K = Money.of(1_000, Monetary.getCurrency(Locale.CHINA));
  Money W = Money.of(10_000, Monetary.getCurrency(Locale.CHINA));
  Money M = Money.of(1_000_000, Monetary.getCurrency(Locale.CHINA));
  Money Y = Money.of(100_000_000, Monetary.getCurrency(Locale.CHINA));
  Money B = Money.of(1_000_000_000, Monetary.getCurrency(Locale.CHINA));

  static Money money(Number number) {
    return Money.of(number, Monetary.getCurrency(Locale.CHINA));
  }

  static Money money(BigDecimal number) {
    return Money.of(number, Monetary.getCurrency(Locale.CHINA));
  }


  static String format(MonetaryAmount amount) {
    return fmt.format(amount);
  }

  static String formatInW(MonetaryAmount amount) {
    return format(amount.divide(W.getNumber())).concat("万");
  }

  static String formatInY(MonetaryAmount amount) {
    return format(amount.divide(Y.getNumber())).concat("亿");
  }

  static Tuple2<String, String> desc(MonetaryAmount amount) {
    return Match(amount).of(
      Case($(t -> t.abs().isGreaterThan(Y)), t -> Tuple.of(format(t.divide(100_000_000D)), "亿")),
      Case($(t -> t.abs().isGreaterThan(W)), t -> Tuple.of(format(t.divide(10_000D)), "万")),
      Case($(), Tuple.of(format(amount), "元"))
    );
  }

  static MonetaryAmount parse(CharSequence text) {
    return fmt.parse(text);
  }
}
