package com.get.apm.api.ft;

import com.google.common.collect.ImmutableMap;
import okhttp3.ResponseBody;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Ignore
public class ListApiTest extends AbstractApiTest {
  private ListApiTestInterface tests;


  public ListApiTest() {
    super();
    tests = this.getRetrofit().create(ListApiTestInterface.class);
  }

  private void doOkTest(ListApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Response<ResponseBody> response = tests.listAssets(this.getCookie(), queryMap).execute();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("iterms");
  }

  private void doNegativeTest(ListApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Assertions.assertThat(tests.listAssets(this.getCookie(), queryMap).execute().code()).isGreaterThanOrEqualTo(400);
  }

  private void doWrongPathTest(ListApiTestInterface tests, String wrongPath) throws IOException {
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
  public void testTimeFrame() throws IOException {
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().minusYears(1).toString()));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(3).toString(), "to", LocalDate.now().toString()));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusDays(1).toString(), "to", LocalDate.now().toString()));
  }

  @Test
  public void testDept() throws IOException {
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "dept", "1"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "dept", "20"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "dept", "10000"));
  }

  @Test
  public void testOrderby() throws IOException {
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "orderby", "rating"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "orderby", "scan"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "orderby", "exposure"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "orderby", "usage"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "orderby", "fix"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "orderby", "stoprate"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "orderby", "profit"));
  }

  @Test
  public void testDeptAndOrderby() throws IOException {
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "dept", "30", "orderby", "exposure"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "dept", "5", "orderby", "fix"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(1).toString(), "to", LocalDate.now().toString(), "dept", "23", "orderby", "rating"));
  }

  @Test
  public void testStartAndLimit() throws IOException {
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "limit", "1"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "limit", "50"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "limit", "23"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "start", "0"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "start", "30"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "start", "10000"));
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "limit", "20", "start", "5"));
  }

  @Test
  public void testAllParameter() throws IOException {
    doOkTest(tests, new ImmutableMap.Builder<String, String>().put("from", LocalDate.now().minusYears(2).toString()).put("to", LocalDate.now().toString()).put("dept", "30").put("orderby", "exposure").put("limit", "20").put("start", "5").build());
  }

  @Test
  public void testExtraParameter() throws IOException {
    doOkTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().minusYears(1).toString(), "dji", "fpp"));
    doOkTest(tests, new ImmutableMap.Builder<String, String>().put("from", LocalDate.now().minusYears(2).toString()).put("to", LocalDate.now().toString()).put("dept", "30").put("dummy", "foo").put("orderby", "exposure").put("limit", "20").put("start", "5").build());
  }

  //negative tests
  @Test
  public void testWrongPath() throws IOException {
    doWrongPathTest(tests, "dummy");
    doWrongPathTest(tests, "foo");
  }

  @Test
  public void testLackTimeFrame() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("dept", "5"));
    doNegativeTest(tests, ImmutableMap.of("orderby", "scan"));
    doNegativeTest(tests, ImmutableMap.of("limit", "4"));
    doNegativeTest(tests, ImmutableMap.of("start", "10"));
    doNegativeTest(tests, ImmutableMap.of("dept", "5", "orderby", "scan", "limit", "4", "start", "10"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString()));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "dept", "5"));
    doNegativeTest(tests, ImmutableMap.of("to", LocalDate.now().toString()));
  }

  @Test
  public void testWrongTimeFrame() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(3).minusDays(1).toString(), "to", LocalDate.now().toString()));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().plusDays(1).toString()));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(3).minusDays(1).toString(), "to", LocalDate.now().plusDays(1).toString()));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().toString(), "to", LocalDate.now().minusDays(1).toString()));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().plusDays(1).toString(), "dept", "5"));
  }

  @Test
  public void testWrongDept() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "dept", "-1"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "dept", "abc"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "dept", "-1", "orderby", "exposure"));
  }

  @Test
  public void testWrongOrderBy() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "orderby", "10"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "orderby", "yait"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "orderby", "10", "dept", "5", "limit", "10"));
  }

  @Test
  public void testWrongLimit() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "limit", "0"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "limit", "51"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "limit", "ahf"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "limit", "60", "dept", "5", "orderby", "usage"));
  }

  @Test
  public void testWrongStart() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "start", "-1"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "start", "ad"));
    doNegativeTest(tests, ImmutableMap.of("from", LocalDate.now().minusYears(2).toString(), "to", LocalDate.now().toString(), "start", "-1", "limit", "5", "groupby", "fix"));
  }
}


interface ListApiTestInterface {
  @Headers("accept:application/json")
  @GET("api/list")
  Call<ResponseBody> listAssets(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("accept:application/json")
  @GET("api/list/{wrongPath}")
  Call<ResponseBody> wrongPath(@Header("Cookie") String cookie, @Path("wrongPath") String wrongPath);
}