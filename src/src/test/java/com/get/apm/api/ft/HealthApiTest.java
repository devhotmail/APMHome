package com.get.apm.api.ft;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Doubles;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javaslang.control.Option;
import javaslang.control.Try;
import okhttp3.ResponseBody;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.io.IOException;
import java.util.Map;

public class HealthApiTest extends AbstractApiTest {
  private HealthApiTestInterface tests;

  public HealthApiTest() {
    super();
    tests = this.getRetrofit().create(HealthApiTestInterface.class);
  }

  private void doOkTest(HealthApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Response<ResponseBody> response = tests.health(this.getCookie(), queryMap).execute();
    Assertions.assertThat(Try.of(() -> response.body().contentType().toString()).getOrElse("")).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(Try.of(() -> response.body().string()).getOrElse("")).contains("items");
  }

  private void doNegativeTest(HealthApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Assertions.assertThat(Try.of(() -> tests.health(this.getCookie(), queryMap).execute().code()).getOrElse(0)).isGreaterThanOrEqualTo(400);
  }

  private void doWrongPathTest(HealthApiTestInterface tests, String wrongPath) throws IOException {
    Call<ResponseBody> call = tests.wrongPath(this.getCookie(), wrongPath);
    Response response = call.execute();
    Assertions.assertThat(Try.of(response::code).getOrElse(0)).isEqualTo(404);
  }

  private void rootEqualsSumOfChildrenTest(HealthApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Config parsedResponse = ConfigFactory.parseString(tests.health(this.getCookie(), queryMap).execute().body().string());
    Assertions.assertThat(Doubles.tryParse(parsedResponse.getString("root.count"))).isEqualTo(javaslang.collection.List.ofAll(parsedResponse.getConfigList("items")).map(v -> v.getString("count")).map(Doubles::tryParse).sum().doubleValue());
  }

  private void parentChildrenContinuityTest(HealthApiTestInterface tests, Map<String, String> rootQueryMap, Map<String, String> queryMap, int index) throws IOException {
    Config rootParsedResponse = ConfigFactory.parseString(tests.health(this.getCookie(), rootQueryMap).execute().body().string());
    Config parsedResponse = ConfigFactory.parseString(tests.health(this.getCookie(), queryMap).execute().body().string());
    Assertions.assertThat(Doubles.tryParse(parsedResponse.getString("pages.total")))
      .isEqualTo(Option.of(javaslang.collection.List.ofAll(
        rootParsedResponse.getConfigList("items"))
        .filter(v -> v.getInt("category") == index)
        .map(v -> v.getString("count"))
        .headOption().getOrElse(""))
        .map(Doubles::tryParse).getOrElse(0D).doubleValue());
  }

  @Before
  public void configEnv() throws IOException {
    this.login("assetHead", "111");
  }

  //positive tests
  @Test
  public void testCategory() throws IOException {
    doOkTest(tests, ImmutableMap.of());
    doOkTest(tests, ImmutableMap.of("category", "1"));
    doOkTest(tests, ImmutableMap.of("category", "2"));
    doOkTest(tests, ImmutableMap.of("category", "3"));
    doOkTest(tests, ImmutableMap.of("category", "4"));
    doOkTest(tests, ImmutableMap.of("category", "5"));
    doOkTest(tests, ImmutableMap.of("category", "6"));
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
  public void testAll() throws IOException {
    doOkTest(tests, ImmutableMap.of("category", "1", "limit", "20"));
    doOkTest(tests, ImmutableMap.of("category", "2", "start", "10"));
    doOkTest(tests, ImmutableMap.of("category", "3", "limit", "5", "start", "5"));
  }

  @Test
  public void testExtroParameter() throws IOException {
    doOkTest(tests, ImmutableMap.of("po", "foo"));
    doOkTest(tests, ImmutableMap.of("orderby", "supplier", "jdc", "apo"));
  }

  //Negative cases
  @Test
  public void testWrongPath() throws IOException {
    doWrongPathTest(tests, "dummy");
    doWrongPathTest(tests, "foo");
  }

  @Test
  public void testWrongCategory() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("category", "7"));
    doNegativeTest(tests, ImmutableMap.of("category", "0"));
    doNegativeTest(tests, ImmutableMap.of("category", "month"));
    doNegativeTest(tests, ImmutableMap.of("category", "year", "foo", "dummy"));
  }

  @Test
  public void testWrongLimit() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("limit", "0"));
    doNegativeTest(tests, ImmutableMap.of("limit", "ahf"));
    doNegativeTest(tests, ImmutableMap.of("limit", "0", "category", "1"));
  }

  @Test
  public void testWrongStart() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("start", "-1"));
    doNegativeTest(tests, ImmutableMap.of("start", "ad"));
    doNegativeTest(tests, ImmutableMap.of("start", "-1", "limit", "5", "category", "2"));
  }

  //logic tests
  @Test
  public void testContinuity() throws IOException {
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of());
    parentChildrenContinuityTest(tests, ImmutableMap.of(), ImmutableMap.of("category", "1"), 1);
    parentChildrenContinuityTest(tests, ImmutableMap.of(), ImmutableMap.of("category", "2"), 2);
    parentChildrenContinuityTest(tests, ImmutableMap.of(), ImmutableMap.of("category", "3"), 3);
    parentChildrenContinuityTest(tests, ImmutableMap.of(), ImmutableMap.of("category", "4"), 4);
    parentChildrenContinuityTest(tests, ImmutableMap.of(), ImmutableMap.of("category", "5"), 5);
    parentChildrenContinuityTest(tests, ImmutableMap.of(), ImmutableMap.of("category", "6"), 6);
  }
}

interface HealthApiTestInterface {
  @Headers("accept:application/json")
  @GET("api/health")
  Call<ResponseBody> health(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("accept:application/json")
  @GET("api/health/{wrongPath}")
  Call<ResponseBody> wrongPath(@Header("Cookie") String cookie, @Path("wrongPath") String wrongPath);
}