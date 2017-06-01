package com.get.apm.api.ft;


import okhttp3.ResponseBody;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.io.IOException;
import java.util.Map;

public class MaApiTest extends AbstractApiTest {
  private MaApiTestInterface tests;

  public MaApiTest() {
    super();
    tests = this.getRetrofit().create(MaApiTestInterface.class);
  }

  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }

  private void formatTests(Map<String, String> queryMap, Integer id, Object body, String condition) throws IOException {
    Response<ResponseBody> response = tests.ma(this.getCookie(), queryMap).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("root");

    response = tests.asset(this.getCookie(), queryMap, id).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("id");

    response = tests.forecast(this.getCookie(), queryMap, body).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("root");

    response = tests.forecastrate(this.getCookie(), queryMap, body).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("root");

    response = tests.suggestion(this.getCookie(), queryMap, body, condition).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("count");

    response = tests.forecastasset(this.getCookie(), queryMap, body, id).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("id");
  }

  @Test
  public void testFormat() throws IOException {

  }
}


interface MaApiTestInterface {
  @Headers("accept: application/json")
  @GET(value = "api/ma")
  Call<ResponseBody> ma(@Header("Cookie") String cookie,
                        @QueryMap Map<String, String> options);

  @Headers("accept: application/json")
  @GET(value = "api/ma/asset/{id}")
  Call<ResponseBody> asset(@Header("Cookie") String cookie,
                           @QueryMap Map<String, String> options, @Path(value = "id") Integer id);

  @Headers("accept: application/json")
  @PUT(value = "api/ma/forecast")
  Call<ResponseBody> forecast(@Header("Cookie") String cookie,
                              @QueryMap Map<String, String> options, @Body Object body);

  @Headers("accept: application/json")
  @PUT(value = "api/ma/forecastrate")
  Call<ResponseBody> forecastrate(@Header("Cookie") String cookie,
                                  @QueryMap Map<String, String> options, @Body Object body);

  @Headers("accept: application/json")
  @PUT(value = "api/ma/suggestion/{condition}")
  Call<ResponseBody> suggestion(@Header("Cookie") String cookie,
                                @QueryMap Map<String, String> options, @Body Object body, @Path(value = "condition") String condition);

  @Headers("accept: application/json")
  @PUT(value = "api/ma/forecast/asset/{id}")
  Call<ResponseBody> forecastasset(@Header("Cookie") String cookie,
                                   @QueryMap Map<String, String> options, @Body Object body, @Path(value = "id") Integer id);
}