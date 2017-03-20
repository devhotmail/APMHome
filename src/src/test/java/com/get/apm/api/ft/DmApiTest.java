package com.get.apm.api.ft;


import com.google.common.collect.ImmutableMap;
import okhttp3.ResponseBody;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.io.IOException;
import java.util.Map;

public class DmApiTest extends AbstractApiTest {
  private DmApiTestInterface tests;

  public DmApiTest() {
    super();
    this.tests = this.getRetrofit().create(DmApiTestInterface.class);
  }

  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }

  private void doOkTest(DmApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Response<ResponseBody> response = tests.dm(this.getCookie(), queryMap).execute();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("suggestion");
  }

  private void doNegativeTest(DmApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Assertions.assertThat(tests.dm(this.getCookie(), queryMap).execute().code()).isGreaterThanOrEqualTo(400);
  }

  private void doWrongPathTest(DmApiTestInterface tests, String wrongPath) throws IOException {
    Assertions.assertThat(tests.wrongPath(this.getCookie(), wrongPath).execute().code()).isEqualTo(404);
  }

  //positive tests
  @Test
  public void testGroupby() throws IOException {
    doOkTest(tests, ImmutableMap.of("groupby", "dept"));
    doOkTest(tests, ImmutableMap.of("groupby", "type"));
  }

  @Test
  public void testExtraParameter() throws IOException {
    doOkTest(tests, ImmutableMap.of("groupby", "type", "dummy", "foo"));
    doOkTest(tests, ImmutableMap.of("groupby", "dept", "foo", "2"));
  }

  //negative tests
  @Test
  public void testWrongPath() throws IOException {
    doWrongPathTest(tests, "dummy");
    doWrongPathTest(tests, "foo");
  }

  @Test
  public void testLackGroupby() throws IOException {
    doNegativeTest(tests, ImmutableMap.of());
    doNegativeTest(tests, ImmutableMap.of("foo", "2"));
    doNegativeTest(tests, ImmutableMap.of("dummy", "foo", "foo", "2"));
  }

  @Test
  public void testWrongGroupby() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("groupby", "5"));
    doNegativeTest(tests, ImmutableMap.of("groupby", "month"));
    doNegativeTest(tests, ImmutableMap.of("groupby", "year", "foo", "dummy"));

  }
}


interface DmApiTestInterface {
  @Headers("accept:application/json")
  @GET("api/dm")
  Call<ResponseBody> dm(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("accept:application/json")
  @GET("api/dm/{wrongPath}")
  Call<ResponseBody> wrongPath(@Header("Cookie") String cookie, @Path("wrongPath") String wrongPath);
}