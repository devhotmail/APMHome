package com.get.apm.api.ut;


import javaslang.control.Try;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ByteBuddyUnitTest {
  static class Greeting {
    String greet(String argument) {
      return "Hello from " + argument;
    }
  }

  public static class GreetingInterceptor {
    private final Greeting greeting;

    GreetingInterceptor(Greeting greeting) {
      this.greeting = greeting;
    }

    public String test(String argument) {
      return "Intercepted: " + greeting.greet(argument);
    }
  }

  @Test
  public void testHello() {
    Class<? extends Greeting> dynamicType = new ByteBuddy()
      .subclass(Greeting.class)
      .method(ElementMatchers.named("greet"))
      .intercept(MethodDelegation.to(new GreetingInterceptor(new Greeting())))
      .make()
      .load(getClass().getClassLoader())
      .getLoaded();
    Assertions.assertThat(Try.of(() -> dynamicType.newInstance().greet("Ivar")).getOrElse("")).isEqualTo("Intercepted: Hello from Ivar");
  }
}
