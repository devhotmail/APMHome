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

public class ProfitApiTest extends AbstractApiTest {
  private ProfitApiTestsInterface tests;

  public ProfitApiTest() {
    super();
    tests = this.getRetrofit().create(ProfitApiTestsInterface.class);
  }

  private void doOkTest(ProfitApiTestsInterface tests, Map<String, String> queryMap) throws IOException {
    Response<ResponseBody> response = tests.profit(this.getCookie(), queryMap).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("总收入");
  }

  private void doNegativeTest(ProfitApiTestsInterface tests, Map<String, String> queryMap) throws IOException {
    Assertions.assertThat(tests.profit(this.getCookie(), queryMap).execute().code()).isGreaterThanOrEqualTo(400);
  }

  private void doWrongPathTest(ProfitApiTestsInterface tests, String wrongPath) throws IOException {
    Call<ResponseBody> call;
    call = tests.wrongPath(this.getCookie(), wrongPath);
    Response response;
    response = call.execute();
    Assertions.assertThat(response.code()).isEqualTo(404);
  }

  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }


  @Test
  public void testGroupByType() throws IOException {
    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "type"));

    doOkTest(tests, ImmutableMap.of("year", "2017", "groupby", "type", "limit", "5"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "type", "start", "3"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "type", "limit", "10", "start", "6"));
  }

  @Test
  public void testGroupByDept() throws IOException {
    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "dept"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "dept", "limit", "50"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "dept", "start", "3"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "dept", "limit", "10", "start", "6"));
  }

  @Test
  public void testGroupByMonth() throws IOException {
    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "month"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "month", "limit", "1"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "month", "start", "3"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "month", "limit", "10", "start", "6"));
  }

  @Test
  public void testGroupByDevice() throws IOException {
    doOkTest(tests, ImmutableMap.of("year", "2014"));

    doOkTest(tests, ImmutableMap.of("year", "2016"));

    doOkTest(tests, ImmutableMap.of("year", "2017"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "limit", "1"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "limit", "12"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "start", "0"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "limit", "10", "start", "6"));
  }

  @Test
  public void testExtraParameterInPath() throws IOException {
    doOkTest(tests, ImmutableMap.of("year", "2016", "po", "5"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "month", "12", "limit", "10", "start", "6", "da", "or"));
  }

  @Test
  public void testItemCalls() throws IOException {
    //(4) year + type/dept/month
    doOkTest(tests, ImmutableMap.of("year", "2016", "type", "1"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "type", "4"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "dept", "1"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "dept", "5"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "month", "1"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "month", "12"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "month", "4"));

    //(5) year + type/dept/month + limit/start/(limit + start)
    doOkTest(tests, ImmutableMap.of("year", "2016", "type", "5", "limit", "5"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "type", "5", "start", "3"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "type", "5", "limit", "10", "start", "6"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "dept", "1", "limit", "5"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "dept", "12", "start", "3"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "dept", "12", "limit", "10", "start", "6"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "month", "1", "limit", "5"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "month", "7", "start", "0"));

    doOkTest(tests, ImmutableMap.of("year", "2016", "month", "12", "limit", "10", "start", "6"));
  }


  //Negative tests

  @Test
  public void testWrongPath() throws IOException {
    doWrongPathTest(tests, "dummy");
    doWrongPathTest(tests, "revenue");
  }

  @Test
  public void testWrongYear() throws IOException {
    //lack of parameter "year"
    doNegativeTest(tests, ImmutableMap.of("groupby", "type"));

    doNegativeTest(tests, ImmutableMap.of("dept", "16"));

    doNegativeTest(tests, ImmutableMap.of("groupby", "month", "limit", "10", "start", "6"));

    doNegativeTest(tests, ImmutableMap.of("month", "7", "limit", "10", "start", "6"));

    // wrong value in parameter “year”
    doNegativeTest(tests, ImmutableMap.of("year", "2013"));

    doNegativeTest(tests, ImmutableMap.of("year", "2018"));

    doNegativeTest(tests, ImmutableMap.of("year", "2abc"));

    doNegativeTest(tests, ImmutableMap.of("year", "2018", "groupby", "type"));

  }

  @Test
  public void testWrongGroupBy() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "dep"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "100"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "dep", "limit", "10", "start", "6"));
  }

  @Test
  public void testWrongType() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("year", "2016", "type", "-1"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "type", "adf"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "type", "-1", "limit", "5", "start", "6"));
  }

  @Test
  public void testWrongDept() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("year", "2016", "dept", "-1"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "dept", "abc"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "dept", "-1", "limit", "5", "start", "6"));
  }

  @Test
  public void testWrongMonth() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("year", "2016", "month", "0"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "month", "-1"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "month", "13"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "month", "ga"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "month", "-1", "limit", "5", "start", "6"));
  }

  @Test
  public void testWrongLimit() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("year", "2016", "limit", "0"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "limit", "51"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "limit", "dh"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "limit", "-1"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "month", "4", "limit", "0", "start", "6"));
  }

  @Test
  public void testWrongStart() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("year", "2016", "start", "-1"));


    doNegativeTest(tests, ImmutableMap.of("year", "2016", "start", "da"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "month", "4", "limit", "5", "start", "-1"));
  }

  @Test
  public void testParameterConflicts() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "type", "type", "3"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "dept", "dept", "5"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "month", "month", "11"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "type", "month", "11"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "month", "month", "11", "limit", "10", "start", "6"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "type", "4", "dept", "5", "month", "4"));
  }

}


interface ProfitApiTestsInterface {
  @Headers("Accept: application/json")
  @GET("api/profit")
  Call<ResponseBody> profit(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("Accept: application/json")
  @GET("api/profit/{wrongPath}")
  Call<ResponseBody> wrongPath(@Header("Cookie") String cookie, @Path("wrongPath") String wrongPath);
}