package com.get.apm.api.it;

import com.get.apm.api.AbstractTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class ProfitApiTest extends AbstractTest {
  @Test
  public void getAccount() throws Exception {
    this.mvc.perform(get("/profit/dummy")
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/json;charset=UTF-8"));
  }

  @Test
  public void test() throws Exception {
    String result = this.rest.getForObject("/profit/foo", String.class);
    Assertions.assertThat("bar").isEqualToIgnoringCase("bar");
  }

  @Test
  public void testDummy() {
    Map map = this.rest.getForObject("/profit/dummy", Map.class);
    System.out.println(map.get("total"));
  }

}
