package com.get.apm.api.ft;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Doubles;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import okhttp3.ResponseBody;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class ProfitApiTest extends AbstractApiTest {
  private final double accuracy = 0.01D;
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

  private void rootEqualsSumOfChildrenTest(ProfitApiTestsInterface tests, Map<String, String> queryMap) throws IOException {
    Response<ResponseBody> response = tests.profit(this.getCookie(), queryMap).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Config parsedResponse = ConfigFactory.parseString(response.body().string());
    Assertions.assertThat(Doubles.tryParse(parsedResponse.getString("root.revenue"))).isEqualTo(javaslang.collection.List.ofAll(parsedResponse.getConfigList("items")).map(v -> v.getString("revenue")).map(Doubles::tryParse).sum().doubleValue());
    Assertions.assertThat(Doubles.tryParse(parsedResponse.getString("root.cost"))).isEqualTo(javaslang.collection.List.ofAll(parsedResponse.getConfigList("items")).map(v -> v.getString("cost")).map(Doubles::tryParse).sum().doubleValue());
  }

  private void consistencyForDifferentQueriesTest(ProfitApiTestsInterface tests, Map<String, String> queryMap1, Map<String, String> queryMap2) throws IOException {
    Assertions.assertThat(Doubles.tryParse(ConfigFactory.parseString(tests.profit(this.getCookie(), queryMap1).execute().body().string()).getString("root.revenue"))).isEqualTo(Doubles.tryParse(ConfigFactory.parseString(tests.profit(this.getCookie(), queryMap2).execute().body().string()).getString("root.revenue")));
    Assertions.assertThat(Doubles.tryParse(ConfigFactory.parseString(tests.profit(this.getCookie(), queryMap1).execute().body().string()).getString("root.cost"))).isEqualTo(Doubles.tryParse(ConfigFactory.parseString(tests.profit(this.getCookie(), queryMap2).execute().body().string()).getString("root.cost")));
  }

  private void rootEqualsSumOfChildrenForecastTest(ProfitApiTestsInterface tests, Map<String, String> queryMap) throws IOException {
    Response<ResponseBody> response = tests.forecast(this.getCookie(), queryMap).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Config parsedResponse = ConfigFactory.parseString(response.body().string());
    Assertions.assertThat(Doubles.tryParse(parsedResponse.getString("root.revenue"))).isCloseTo(javaslang.collection.List.ofAll(parsedResponse.getConfigList("items")).map(v -> v.getString("revenue")).map(Doubles::tryParse).sum().doubleValue(), Percentage.withPercentage(accuracy));
    Assertions.assertThat(Doubles.tryParse(parsedResponse.getString("root.cost"))).isCloseTo(javaslang.collection.List.ofAll(parsedResponse.getConfigList("items")).map(v -> v.getString("cost")).map(Doubles::tryParse).sum().doubleValue(), Percentage.withPercentage(accuracy));
  }

  private void consistencyForDifferentQueriesForecastTest(ProfitApiTestsInterface tests, Map<String, String> queryMap1, Map<String, String> queryMap2) throws IOException {
    Assertions.assertThat(Doubles.tryParse(ConfigFactory.parseString(tests.forecast(this.getCookie(), queryMap1).execute().body().string()).getString("root.revenue"))).isCloseTo(Doubles.tryParse(ConfigFactory.parseString(tests.forecast(this.getCookie(), queryMap2).execute().body().string()).getString("root.revenue")), Percentage.withPercentage(accuracy));
    Assertions.assertThat(Doubles.tryParse(ConfigFactory.parseString(tests.forecast(this.getCookie(), queryMap1).execute().body().string()).getString("root.cost"))).isCloseTo(Doubles.tryParse(ConfigFactory.parseString(tests.forecast(this.getCookie(), queryMap2).execute().body().string()).getString("root.cost")), Percentage.withPercentage(accuracy));
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

  @Test
  public void testFromTo() throws IOException {
    doOkTest(tests, ImmutableMap.of("from", "2015-12-12", "to", "2016-02-13"));
    doOkTest(tests, ImmutableMap.of("from", "2015-12-22", "to", "2016-12-13"));
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

    doOkTest(tests, ImmutableMap.of("year", "2016", "groupby", "dept", "dept", "5"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "month", "month", "11"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "type", "month", "11"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "groupby", "month", "month", "11", "limit", "10", "start", "6"));

    doNegativeTest(tests, ImmutableMap.of("year", "2016", "type", "4", "dept", "5", "month", "4"));
  }

  @Test
  public void testLackFromOrTo() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString()));
    doNegativeTest(tests, ImmutableMap.of("to", LocalDate.now().toString()));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "dept", "5"));
  }

  @Test
  public void testWrongFromTo() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(3).minusDays(1).toString(), "to", LocalDate.now().toString()));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(3).minusDays(1).toString(), "to", LocalDate.now().plusDays(1).toString(), "dept", "5"));
  }

  //logical tests
  @Test
  public void testHeadGroupbyType() throws IOException {
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2016", "groupby", "type"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-05-06", "to", "2017-01-09", "groupby", "type"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2016", "type", "2"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-04-10", "to", "2017-02-19", "type", "5"));
  }

  @Test
  public void testHeadGroupbyDept() throws IOException {
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2015", "groupby", "dept"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-03-06", "to", "2017-02-09", "groupby", "dept"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2015", "dept", "1"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-03-02", "to", "2016-12-19", "dept", "3"));
  }

  @Test
  public void testHeadGroupbyMonth() throws IOException {
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2016", "groupby", "month"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-03-06", "to", "2016-11-09", "groupby", "month"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2016", "month", "12"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-03-12", "to", "2016-11-19", "dept", "7"));
  }

  @Test
  public void testHeadSingleMachine() throws IOException {
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2015"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-07-06", "to", "2017-02-22"));
  }

  @Test
  public void testDeptheadGroupbyType() throws IOException {
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2016", "groupby", "type", "dept", "1"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-05-06", "to", "2017-01-09", "groupby", "type", "dept", "2"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2016", "type", "2", "dept", "3"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-04-10", "to", "2017-02-19", "type", "5", "dept", "4"));
  }

  @Test
  public void testDeptheadGroupbyMonth() throws IOException {
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2016", "groupby", "month", "dept", "1"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-03-06", "to", "2016-11-09", "groupby", "month", "dept", "2"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("year", "2016", "month", "12", "dept", "3"));
    rootEqualsSumOfChildrenTest(tests, ImmutableMap.of("from", "2016-03-12", "to", "2016-11-19", "dept", "7"));
  }

  @Test
  public void testConsistency() throws IOException {
    consistencyForDifferentQueriesTest(tests, ImmutableMap.of("year", "2016", "groupby", "type"), ImmutableMap.of("from", "2016-01-01", "to", "2016-12-31", "groupby", "month"));
    consistencyForDifferentQueriesTest(tests, ImmutableMap.of("year", "2016", "month", "12"), ImmutableMap.of("from", "2016-12-01", "to", "2016-12-31", "groupby", "type"));
  }

  //logical forecast tests
  @Test
  public void testHeadGroupbyTypeForecast() throws IOException {
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().getYear(), "groupby", "type"));
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().plusYears(1).getYear(), "type", "2"));
  }

  @Test
  public void testHeadGroupbyDeptForecast() throws IOException {
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().getYear(), "groupby", "dept"));
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().plusYears(1).getYear(), "dept", "1"));
  }

  @Test
  public void testHeadGroupbyMonthForecast() throws IOException {
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().getYear(), "groupby", "month"));
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().plusYears(1).getYear(), "month", "12"));
  }

  @Test
  public void testHeadSingleMachineForecast() throws IOException {
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().getYear()));
  }

  @Test
  public void testDeptheadGroupbyTypeForecast() throws IOException {
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().plusYears(1).getYear(), "groupby", "type", "dept", "1"));
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().getYear(), "type", "2", "dept", "3"));
  }

  @Test
  public void testDeptheadGroupbyMonthForecast() throws IOException {
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().plusYears(1).getYear(), "groupby", "month", "dept", "1"));
    rootEqualsSumOfChildrenForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().getYear(), "month", "12", "dept", "3"));
  }

  @Test
  public void testConsistencyForecast() throws IOException {
    consistencyForDifferentQueriesForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().plusYears(1).getYear(), "groupby", "type"), ImmutableMap.of("from", LocalDate.now().withDayOfYear(1).plusYears(1).toString(), "to", LocalDate.now().withDayOfYear(1).plusYears(2).minusDays(1).toString(), "groupby", "month"));
    consistencyForDifferentQueriesForecastTest(tests, ImmutableMap.of("year", "" + LocalDate.now().getYear()), ImmutableMap.of("from", LocalDate.now().withDayOfYear(1).plusMonths(11).toString(), "to", LocalDate.now().withDayOfYear(1).plusYears(1).minusDays(1).toString(), "groupby", "type"));
  }

}


interface ProfitApiTestsInterface {
  @Headers("Accept: application/json")
  @GET("api/profit")
  Call<ResponseBody> profit(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("Accept: application/json")
  @GET("api/profit/forecast")
  Call<ResponseBody> forecast(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("Accept: application/json")
  @GET("api/profit/forecastrate")
  Call<ResponseBody> forecastrate(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("Accept: application/json")
  @GET("api/profit/forecast")
  Call<ResponseBody> forecastPut(@Header("Cookie") String cookie, @QueryMap Map<String, String> options, Object body);

  @Headers("Accept: application/json")
  @GET("api/profit/forecastrate")
  Call<ResponseBody> forecastratePut(@Header("Cookie") String cookie, @QueryMap Map<String, String> options, Object body);

  @Headers("Accept: application/json")
  @GET("api/profit/{wrongPath}")
  Call<ResponseBody> wrongPath(@Header("Cookie") String cookie, @Path("wrongPath") String wrongPath);
}