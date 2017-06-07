package com.get.apm.api.ft;

import com.google.common.collect.ImmutableMap;
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

public class MsgApiTest extends AbstractApiTest {
  private MsgApiTestInterface tests;

  public MsgApiTest() {
    super();
    tests = this.getRetrofit().create(MsgApiTestInterface.class);
  }

  private void doOkTestType(MsgApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Response<ResponseBody> response = tests.msg(this.getCookie(), queryMap).execute();
    Assertions.assertThat(Try.of(() -> response.body().contentType().toString()).getOrElse("")).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(Try.of(() -> response.body().string()).getOrElse("")).contains("{");
  }

  private void doOkTestAll(MsgApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Response<ResponseBody> response = tests.msgAll(this.getCookie(), queryMap).execute();
    Assertions.assertThat(Try.of(() -> response.body().contentType().toString()).getOrElse("")).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(Try.of(() -> response.body().string()).getOrElse("")).contains("messages");
  }

  private void doOkTestId(MsgApiTestInterface tests, String id) throws IOException {
    Response<ResponseBody> response = tests.msgId(this.getCookie(), id).execute();
    Assertions.assertThat(Try.of(() -> response.body().contentType().toString()).getOrElse("")).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(Try.of(() -> response.body().string()).getOrElse("")).contains("id");
  }

  private void doNegativeTest(MsgApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Assertions.assertThat(Try.of(() -> tests.msg(this.getCookie(), queryMap).execute().code()).getOrElse(0)).isGreaterThanOrEqualTo(400);
  }

  private void doWrongPathTest(MsgApiTestInterface tests, String wrongPath) throws IOException {
    Call<ResponseBody> call = tests.wrongPath(this.getCookie(), wrongPath);
    Response response = call.execute();
    Assertions.assertThat(Try.of(response::code).getOrElse(0)).isEqualTo(400);
  }

  private void doNegativeTestAll(MsgApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Assertions.assertThat(Try.of(() -> tests.msgAll(this.getCookie(), queryMap).execute().code()).getOrElse(0)).isGreaterThanOrEqualTo(400);
  }

  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }

  //positive tests
  @Test
  public void testType() throws IOException {
    doOkTestType(tests, ImmutableMap.of("type", "assetGroup"));
    doOkTestType(tests, ImmutableMap.of("type", "label"));
    doOkTestType(tests, ImmutableMap.of("type", "file_name"));
  }

  @Test
  public void testAll() throws IOException {
    doOkTestAll(tests, ImmutableMap.of());
    doOkTestAll(tests, ImmutableMap.of("limit", "1"));
    doOkTestAll(tests, ImmutableMap.of("limit", "30"));
    doOkTestAll(tests, ImmutableMap.of("start", "0"));
    doOkTestAll(tests, ImmutableMap.of("start", "50"));
    doOkTestAll(tests, ImmutableMap.of("start", "10000"));
    doOkTestAll(tests, ImmutableMap.of("limit", "20", "start", "100"));
  }

  @Test
  public void testId() throws IOException {
    doOkTestId(tests, "1");
    doOkTestId(tests, "50");
  }

  //negative cases
  @Test
  public void testWrongPath() throws IOException {
    doWrongPathTest(tests, "dummy");
    doWrongPathTest(tests, "foo");
  }

  @Test
  public void testLackType() throws IOException {
    doNegativeTest(tests, ImmutableMap.of());
    doNegativeTest(tests, ImmutableMap.of("foo", "dummy"));
  }

  @Test
  public void testWrongLimit() throws IOException {
    doNegativeTestAll(tests, ImmutableMap.of("limit", "0"));
    doNegativeTestAll(tests, ImmutableMap.of("limit", "ahf"));
    doNegativeTestAll(tests, ImmutableMap.of("limit", "0", "type", "assetGroup"));
  }

  @Test
  public void testWrongStart() throws IOException {
    doNegativeTestAll(tests, ImmutableMap.of("start", "-1"));
    doNegativeTestAll(tests, ImmutableMap.of("start", "ad"));
    doNegativeTestAll(tests, ImmutableMap.of("start", "-1", "type", "ile_name"));
  }
}

interface MsgApiTestInterface {
  @Headers("Accept: application/json")
  @GET("api/msg")
  Call<ResponseBody> msg(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("Accept: application/json")
  @GET("api/msg/all")
  Call<ResponseBody> msgAll(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("Accept: application/json")
  @GET("api/msg/{id}")
  Call<ResponseBody> msgId(@Header("Cookie") String cookie, @Path("id") String wrongPath);

  @Headers("Accept: application/json")
  @GET("api/msg/{wrongPath}")
  Call<ResponseBody> wrongPath(@Header("Cookie") String cookie, @Path("wrongPath") String wrongPath);
}
