package com.get.apm.api.ut;

import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelUnitTest {
  private ExpressionParser parser;
  private StandardEvaluationContext context;

  @Before
  public void setUp() {
    parser = new SpelExpressionParser();
    context = new StandardEvaluationContext();
  }

  @After
  public void tearDown() {

  }

  @Test
  public void testIntegerConcat() {
    context.setVariables(ImmutableMap.of("siteId", 1, "type", 2));
    Assertions.assertThat(parser.parseExpression("'commonService.findFields.'+#siteId+'.'+#type").getValue(context)).isEqualTo("commonService.findFields.1.2");
  }

}
