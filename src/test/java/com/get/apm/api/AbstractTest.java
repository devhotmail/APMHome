package com.get.apm.api;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockMvcClientHttpRequestFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath*:config/springmvc-parent.xml", "file:src/main/webapp/WEB-INF/config/servlet-context.xml"})
public abstract class AbstractTest {
  @Resource
  private WebApplicationContext applicationContext;
  protected MockMvc mvc;
  protected RestTemplate rest;

  @Before
  public void setup() {
    this.mvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
    this.rest = new RestTemplate(new MockMvcClientHttpRequestFactory(mvc));
  }

}
