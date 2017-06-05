package com.get.apm.api.ft;


import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigFactory;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.Seq;
import javaslang.control.Try;
import okhttp3.ResponseBody;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static javaslang.API.Case;
import static javaslang.API.Match;

public class FailureAnalysisApiTest extends AbstractApiTest {
  private FailureAnalysisApiTestInterface tests;
  private final static double accuracy = 0.01;

  public FailureAnalysisApiTest() {
    super();
    tests = this.getRetrofit().create(FailureAnalysisApiTestInterface.class);
  }

  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }

  private Map<String, String> maps(String from, String to, String groupBy, Integer type, Integer dept, Integer supplier, Integer asset, String orderBy, Integer start, Integer limit) {
    ImmutableMap.Builder<String, String> result = new ImmutableMap.Builder<>();
    if (from != null) {
      result.put("from", from);
    }
    if (to != null) {
      result.put("to", to);
    }
    if (groupBy != null) {
      result.put("groupby", groupBy);
    }
    if (type != null) {
      result.put("type", type.toString());
    }
    if (dept != null) {
      result.put("dept", dept.toString());
    }
    if (supplier != null) {
      result.put("supplier", supplier.toString());
    }
    if (asset != null) {
      result.put("asset", asset.toString());
    }
    if (orderBy != null) {
      result.put("orderby", orderBy);
    }
    if (start != null) {
      result.put("start", start.toString());
    }
    if (limit != null) {
      result.put("limit", limit.toString());
    }
    return result.build();
  }

  private void formatBriefTest(FailureAnalysisApiTestInterface tests, String from, String to, String groupBy, Integer type, Integer dept, Integer supplier, Integer asset, String orderBy, Integer start, Integer limit) throws IOException {
    Map<String, String> queryMap = maps(from, to, groupBy, type, dept, supplier, asset, orderBy, start, limit);
    Response<ResponseBody> response = Try.of(() -> tests.briefs(this.getCookie(), queryMap).execute()).getOrElse(() -> null);
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().string()).contains("briefs");
  }

  private void negativeBriefFormatTest(FailureAnalysisApiTestInterface tests, String from, String to, String groupBy, Integer type, Integer dept, Integer supplier, Integer asset, String orderBy, Integer start, Integer limit) throws IOException {
    Map<String, String> queryMap = maps(from, to, groupBy, type, dept, supplier, asset, orderBy, start, limit);
    Response<ResponseBody> response = Try.of(() -> tests.briefs(this.getCookie(), queryMap).execute()).getOrElse(() -> null);
    Assertions.assertThat(response.code()).isGreaterThanOrEqualTo(400);
  }

  private void formatReasonTest(FailureAnalysisApiTestInterface tests, String from, String to, Integer type, Integer dept, Integer supplier, Integer asset, String orderBy, Integer start, Integer limit) throws IOException {
    Map<String, String> queryMap = maps(from, to, null, type, dept, supplier, asset, orderBy, start, limit);
    Response<ResponseBody> response = Try.of(() -> tests.reasons(this.getCookie(), queryMap).execute()).getOrElse(() -> null);
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().string()).contains("reasons");
  }

  private void negativeReasonFormatTest(FailureAnalysisApiTestInterface tests, String from, String to, Integer type, Integer dept, Integer supplier, Integer asset, String orderBy, Integer start, Integer limit) throws IOException {
    Map<String, String> queryMap = maps(from, to, null, type, dept, supplier, asset, orderBy, start, limit);
    Response<ResponseBody> response = Try.of(() -> tests.reasons(this.getCookie(), queryMap).execute()).getOrElse(() -> null);
    Assertions.assertThat(response.code()).isGreaterThanOrEqualTo(400);
  }

  //logic test
  private void briefConsistencyTest(FailureAnalysisApiTestInterface tests, String from, String to, String groupBy, Integer type, Integer dept, Integer supplier, Integer asset, String orderBy, Integer start, Integer limit) {
    Map<String, String> query = maps(from, to, groupBy, type, dept, supplier, asset, orderBy, start, limit);
    String response = Try.of(() -> tests.briefs(this.getCookie(), query).execute().body().string()).getOrElseGet(e -> {
      e.printStackTrace();
      return "first query fails";
    });
    Assertions.assertThat(response).isNotEqualTo("first query fails");

    javaslang.collection.List.ofAll(ConfigFactory.parseString(response).getConfigList("briefs"))
      .forEach(config -> {
        int id = config.getInt("key.id");
        Map<String, String> subQuery = Match(groupBy).of(
          Case("type", maps(from, to, "asset", id, dept, supplier, asset, orderBy, start, limit)),
          Case("dept", maps(from, to, "asset", type, id, supplier, asset, orderBy, start, limit)),
          Case("supplier", maps(from, to, "asset", type, dept, id, asset, orderBy, start, limit))
        );
        String subResult = Try.of(() -> tests.briefs(this.getCookie(), subQuery).execute().body().string())
          .getOrElseGet(e -> {
            e.printStackTrace();
            return "subquery fails";
          });
        Seq<Tuple2<Double, Integer>> items =
          javaslang.collection.List.ofAll(ConfigFactory.parseString(subResult).getConfigList("briefs"))
            .map(subConfig -> Tuple.of(subConfig.getDouble("val.avail"), subConfig.getInt("val.fix")));
        Assertions.assertThat(config.getDouble("val.avail")).isCloseTo(items.map(t -> t._1).average().getOrElse(0D), Percentage.withPercentage(accuracy));
        Assertions.assertThat(config.getInt("val.fix")).isEqualTo(items.map(t -> t._2).sum().intValue());
      });
  }

  @Test
  public void testFormatBrief() throws IOException {
    formatBriefTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), "type", null, 1, null, null, "avail", null, null);
    formatBriefTest(tests, LocalDate.now().withDayOfYear(1).minusYears(1).toString(), LocalDate.now().toString(), "dept", 2, null, null, null, "fix", 1, 3);
    formatBriefTest(tests, LocalDate.now().withDayOfYear(1).minusYears(2).toString(), LocalDate.now().minusYears(1).toString(), "supplier", null, 1, null, 3, "avail", null, 10);
    formatBriefTest(tests, LocalDate.now().withDayOfYear(1).minusMonths(3).toString(), LocalDate.now().toString(), "asset", null, null, null, 1, "fix", 0, 10);
  }

  @Test
  public void testNegativeFormatBrief() throws IOException {
    negativeBriefFormatTest(tests, null, LocalDate.now().toString(), "type", null, 1, null, null, "avail", null, null);
    negativeBriefFormatTest(tests, LocalDate.now().withDayOfYear(1).minusYears(1).toString(), null, "dept", null, 1, null, null, null, 1, 2);
    negativeBriefFormatTest(tests, LocalDate.now().withDayOfYear(1).minusYears(1).toString(), LocalDate.now().toString(), "foo", null, 1, null, null, "fix", 3, 6);
    negativeBriefFormatTest(tests, LocalDate.now().withDayOfYear(1).minusMonths(3).toString(), LocalDate.now().toString(), "asset", -1, null, null, 1, "fix", 1, 5);
    negativeBriefFormatTest(tests, LocalDate.now().withDayOfYear(1).minusYears(2).toString(), LocalDate.now().minusYears(1).toString(), "supplier", null, -1, null, 3, "avail", 1, 5);
    negativeBriefFormatTest(tests, LocalDate.now().withDayOfYear(1).minusYears(2).toString(), LocalDate.now().minusYears(1).toString(), "supplier", null, 1, 0, 3, "avail", null, 5);
    negativeBriefFormatTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), "type", null, 1, null, 0, "avail", null, 4);
    negativeBriefFormatTest(tests, LocalDate.now().withDayOfYear(1).minusYears(2).toString(), LocalDate.now().minusYears(1).toString(), "supplier", null, 1, null, 3, "avail", -1, 10);
    negativeBriefFormatTest(tests, LocalDate.now().withDayOfYear(1).minusYears(2).toString(), LocalDate.now().minusYears(1).toString(), "supplier", null, 1, null, 3, "avail", 0, 0);
  }

  @Test
  public void testFormatReason() throws IOException {
    formatReasonTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), 1, 1, 2, 3, null, 0, 1);
    formatReasonTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), null, 1, null, null, null, null, 1);
    formatReasonTest(tests, LocalDate.now().withDayOfYear(1).minusDays(1).toString(), LocalDate.now().toString(), 2, null, null, null, null, 0, null);
    formatReasonTest(tests, LocalDate.now().withDayOfYear(1).minusMonths(3).toString(), LocalDate.now().minusMonths(2).toString(), null, null, 4, null, null, 0, 1);
  }

  @Test
  public void testNegativeFormatReason() throws IOException {
    negativeReasonFormatTest(tests, null, LocalDate.now().toString(), 1, 1, 2, 3, null, 0, 1);
    negativeReasonFormatTest(tests, LocalDate.now().withDayOfYear(1).toString(), null, 1, 1, null, 3, null, 0, 1);
    negativeReasonFormatTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), 0, 1, 2, 3, null, 0, 1);
    negativeReasonFormatTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), 1, 0, 2, 3, null, 0, 1);
    negativeReasonFormatTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), 1, 1, 0, 3, null, 0, 1);
    negativeReasonFormatTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), 1, 1, 2, 0, null, 0, 1);
    negativeReasonFormatTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), 1, 1, 2, 3, "", -1, 1);
    negativeReasonFormatTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), 1, 1, 2, 3, null, 0, 0);
  }

  @Test
  public void testBriefConsistency() throws IOException {
    briefConsistencyTest(tests, LocalDate.now().withDayOfYear(1).toString(), LocalDate.now().toString(), "supplier", 1, null, null, null, "avail", 0, Integer.MAX_VALUE);
    briefConsistencyTest(tests, LocalDate.now().withDayOfYear(1).minusYears(1).toString(), LocalDate.now().withDayOfYear(1).toString(), "dept", null, null, 3, null, "avail", 0, Integer.MAX_VALUE);
    briefConsistencyTest(tests, LocalDate.now().withDayOfYear(1).minusMonths(4).toString(), LocalDate.now().toString(), "type", 3, null, null, null, "avail", 0, Integer.MAX_VALUE);
  }
}

interface FailureAnalysisApiTestInterface {
  @Headers("Accept: application/json")
  @GET(value = "api/fa/briefs")
  Call<ResponseBody> briefs(@Header(value = "Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("Accept: application/json")
  @GET(value = "api/fa/reasons")
  Call<ResponseBody> reasons(@Header(value = "Cookie") String cookie, @QueryMap Map<String, String> options);
}
