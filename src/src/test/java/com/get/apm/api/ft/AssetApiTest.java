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

public class AssetApiTest extends AbstractApiTest {
  private AssetApiTestInterface tests;

  public AssetApiTest() {
    super();
    tests = this.getRetrofit().create(AssetApiTestInterface.class);
  }

  private void doOkTest(AssetApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Response<ResponseBody> response = tests.assets(this.getCookie(), queryMap).execute();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("assets");
  }

  private void doNegativeTest(AssetApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Assertions.assertThat(tests.assets(this.getCookie(), queryMap).execute().code()).isGreaterThanOrEqualTo(400);
  }

  private void doWrongPathTest(AssetApiTestInterface tests, String wrongPath) throws IOException {
    Call<ResponseBody> call = tests.wrongPath(this.getCookie(), wrongPath);
    Response response = call.execute();
    Assertions.assertThat(response.code()).isEqualTo(404);
  }

  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }

  //positive tests
  @Test
  public void testOrderby() throws IOException {
    doOkTest(tests, ImmutableMap.of());
    doOkTest(tests, ImmutableMap.of("orderby", "type"));
    doOkTest(tests, ImmutableMap.of("orderby", "dept"));
    doOkTest(tests, ImmutableMap.of("orderby", "supplier"));
    doOkTest(tests, ImmutableMap.of("orderby", "price"));
    doOkTest(tests, ImmutableMap.of("orderby", "yoa"));
  }

  @Test
  public void testLimitAndStart() throws IOException {
    doOkTest(tests, ImmutableMap.of("limit", "1"));
    doOkTest(tests, ImmutableMap.of("limit", "50"));
    doOkTest(tests, ImmutableMap.of("limit", "23"));
    doOkTest(tests, ImmutableMap.of("start", "0"));
    doOkTest(tests, ImmutableMap.of("start", "30"));
    doOkTest(tests, ImmutableMap.of("start", "10000"));
    doOkTest(tests, ImmutableMap.of("limit", "20", "start", "5"));
  }

  @Test
  public void testAllParameters() throws IOException {
    doOkTest(tests, ImmutableMap.of("orderby", "type"));
    doOkTest(tests, ImmutableMap.of("orderby", "price", "start", "10"));
    doOkTest(tests, ImmutableMap.of("orderby", "price", "limit", "5", "start", "5"));
  }

  @Test
  public void testExtraParameter() throws IOException {
    doOkTest(tests, ImmutableMap.of("po", "foo"));
    doOkTest(tests, ImmutableMap.of("orderby", "supplier", "jdc", "apo"));
  }

  //negative tests
  @Test
  public void testWrongPath() throws IOException {
    doWrongPathTest(tests, "dummy");
    doWrongPathTest(tests, "foo");
  }

  @Test
  public void testWrongOrderby() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("orderby", "20"));
    doNegativeTest(tests, ImmutableMap.of("orderby", "foo"));
    doNegativeTest(tests, ImmutableMap.of("orderby", "20", "start", "4", "limit", "6"));
  }

  @Test
  public void testWrongLimit() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("limit", "0"));
    doNegativeTest(tests, ImmutableMap.of("limit", "51"));
    doNegativeTest(tests, ImmutableMap.of("limit", "ahf"));
    doNegativeTest(tests, ImmutableMap.of("limit", "60", "dept", "5", "orderby", "dept"));
  }

  @Test
  public void testWrongStart() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("start", "-1"));
    doNegativeTest(tests, ImmutableMap.of("start", "ad"));
    doNegativeTest(tests, ImmutableMap.of("start", "-1", "limit", "5", "groupby", "fix"));
  }
}


interface AssetApiTestInterface {
  @Headers("accept:application/json")
  @GET("api/asset")
  Call<ResponseBody> assets(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("accept:application/json")
  @GET("api/asset/{wrongPath}")
  Call<ResponseBody> wrongPath(@Header("Cookie") String cookie, @Path("wrongPath") String wrongPath);
}