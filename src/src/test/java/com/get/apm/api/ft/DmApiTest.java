package com.get.apm.api.ft;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javaslang.Tuple;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.control.Option;
import javaslang.control.Try;
import okhttp3.ResponseBody;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

import java.io.IOException;
import java.util.Map;

public class DmApiTest extends AbstractApiTest {
  final private double accuracy = 0.01;
  private DmApiTestInterface tests;

  public DmApiTest() {
    super();
    this.tests = this.getRetrofit().create(DmApiTestInterface.class);
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

  private void usageStatisticsByTypeTest(DmApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Config parsedResponse = ConfigFactory.parseString(tests.dm(this.getCookie(), queryMap).execute().body().string());
    Assertions.assertThat(Option.of(Doubles.tryParse(parsedResponse.getStringList("usage_sum").get(0))).getOrElse(-1D)).isEqualTo(
      Option.when(!parsedResponse.getConfigList("items").isEmpty(), List.ofAll(parsedResponse.getConfigList("items"))
        .map(v -> List.ofAll(v.getConfigList("items"))
          .map(v2 -> v2.getString("usage_predict"))
          .map(Doubles::tryParse)
          .count(v3 -> Option.of(v3).getOrElse(1D) <= 0.3D))
        .sum().doubleValue()).getOrElse(0D)
    );
    parsedResponse.getConfigList("items").forEach(items ->
      Assertions.assertThat(Doubles.tryParse(items.getStringList("usage_sum").get(0))).isEqualTo(
        List.ofAll(items.getConfigList("items"))
          .map(subItems -> subItems.getString("usage_predict")).map(Doubles::tryParse).count(v -> v <= 0.3D)
      )
    );
    Assertions.assertThat(Option.of(Doubles.tryParse(parsedResponse.getStringList("usage_sum").get(1))).getOrElse(-1D)).isEqualTo(
      Option.when(!parsedResponse.getConfigList("items").isEmpty(), List.ofAll(parsedResponse.getConfigList("items"))
        .map(v -> List.ofAll(v.getConfigList("items"))
          .map(v2 -> v2.getString("usage_predict"))
          .map(Doubles::tryParse)
          .count(v3 -> Option.of(v3).getOrElse(0D) > 1D))
        .sum().doubleValue()).getOrElse(0D)
    );
    parsedResponse.getConfigList("items").forEach(items ->
      Assertions.assertThat(Doubles.tryParse(items.getStringList("usage_sum").get(1))).isEqualTo(
        (double) (List.ofAll(items.getConfigList("items"))
          .map(subItems -> subItems.getString("usage_predict")).map(Doubles::tryParse).count(v -> v > 1D))
      )
    );
  }

  private void usageStatisticsByDeptTest(DmApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Config parsedResponse = ConfigFactory.parseString(tests.dm(this.getCookie(), queryMap).execute().body().string());
    Assertions.assertThat(Option.of(Doubles.tryParse(parsedResponse.getStringList("usage_sum").get(0))).getOrElse(-1D)).isEqualTo(
      Option.when(!parsedResponse.getConfigList("items").isEmpty(), List.ofAll(parsedResponse.getConfigList("items"))
        .map(v -> List.ofAll(v.getConfigList("items"))
          .map(v2 -> List.ofAll(v2.getConfigList("items"))
            .map(v3 -> v3.getString("usage_predict"))
            .map(Doubles::tryParse)
            .count(v4 -> Option.of(v4).getOrElse(1D) <= 0.3D))
          .sum().doubleValue())
        .sum().doubleValue()).getOrElse(0D)
    );

    Assertions.assertThat(Doubles.tryParse(parsedResponse.getStringList("usage_sum").get(1))).isEqualTo(
      Option.when(!parsedResponse.getConfigList("items").isEmpty(), List.ofAll(parsedResponse.getConfigList("items"))
        .map(v -> List.ofAll(v.getConfigList("items"))
          .map(v2 -> List.ofAll(v2.getConfigList("items"))
            .map(v3 -> v3.getString("usage_predict"))
            .map(Doubles::tryParse)
            .count(v4 -> Option.of(v4).getOrElse(0D) > 1D))
          .sum().doubleValue())
        .sum().doubleValue()).getOrElse(0D)
    );

    parsedResponse.getConfigList("items").forEach(items ->
      Assertions.assertThat(Option.of(Doubles.tryParse(items.getStringList("usage_sum").get(0))).getOrElse(-1D)).isEqualTo(
        List.ofAll(items.getConfigList("items"))
          .map(v -> List.ofAll(v.getConfigList("items"))
            .map(v2 -> v2.getString("usage_predict"))
            .map(Doubles::tryParse)
            .count(v3 -> Option.of(v3).getOrElse(1D) <= 0.3D))
          .sum().doubleValue()
      )
    );

    parsedResponse.getConfigList("items").forEach(items ->
      Assertions.assertThat(Option.of(Doubles.tryParse(items.getStringList("usage_sum").get(1))).getOrElse(-1D)).isEqualTo(
        List.ofAll(items.getConfigList("items"))
          .map(v -> List.ofAll(v.getConfigList("items"))
            .map(v2 -> v2.getString("usage_predict"))
            .map(Doubles::tryParse)
            .count(v3 -> Option.of(v3).getOrElse(0D) > 1D))
          .sum().doubleValue()
      )
    );

    parsedResponse.getConfigList("items").forEach(items ->
      items.getConfigList("items").forEach(subItems ->
        Assertions.assertThat(Option.of(Doubles.tryParse(subItems.getStringList("usage_sum").get(0))).getOrElse(-1D)).isEqualTo(
          (double) List.ofAll(subItems.getConfigList("items")).map(v -> v.getString("usage_predict")).map(Doubles::tryParse).count(v -> Option.of(v).getOrElse(1D) <= 0.3D)
        )
      )
    );

    parsedResponse.getConfigList("items").forEach(items ->
      items.getConfigList("items").forEach(subItems ->
        Assertions.assertThat(Option.of(Doubles.tryParse(subItems.getStringList("usage_sum").get(1))).getOrElse(-1D)).isEqualTo(
          (double) List.ofAll(subItems.getConfigList("items")).map(v -> v.getString("usage_predict")).map(Doubles::tryParse).count(v -> Option.of(v).getOrElse(0D) > 1D)
        )
      )
    );
  }

  private void averageUsageGroupByTest(DmApiTestInterface tests, Map<String, String> queryMap) throws IOException {
    Config parsedResponse = ConfigFactory.parseString(tests.dm(this.getCookie(), queryMap).execute().body().string());
    Assertions.assertThat(Option.of(Doubles.tryParse(parsedResponse.getString("usage_predict"))).getOrElse(-1D)).isCloseTo(
      Option.when(!parsedResponse.getConfigList("items").isEmpty(), List.ofAll(parsedResponse.getConfigList("items"))
        .map(v -> v.getString("usage_predict"))
        .map(Doubles::tryParse)
        .average().getOrElse(0D)).getOrElse(0D),
      Percentage.withPercentage(accuracy)
    );
    parsedResponse.getConfigList("items").forEach(items ->
      Assertions.assertThat(Doubles.tryParse(items.getString("usage_predict"))).isCloseTo(
        List.ofAll(items.getConfigList("items"))
          .map(subItems -> subItems.getString("usage_predict")).map(Doubles::tryParse).average().getOrElse(0D),
        Percentage.withPercentage(accuracy)
      )
    );
  }

  private void usageGroupByTypePutTest(DmApiTestInterface tests, Map<String, String> queryMap, double change, int type) throws IOException {
    Config parsedResponse = ConfigFactory.parseString(tests.dm(this.getCookie(), queryMap).execute().body().string());
    Seq<Map<String, Object>> items = List.ofAll(parsedResponse.getConfigList("items"))
      .map(v -> Tuple.of(v.getString("id"), v.getConfigList("items")))
      .map(v -> Tuple.of(Ints.tryParse(v._1), v._2))
      .filter(v -> v._1.equals(type))
      .flatMap(v -> v._2)
      .map(v -> v.getString("id"))
      .map(Ints::tryParse)
      .map(v -> ImmutableMap.of("id", v, "change", change));
    Map<String, Object> body = ImmutableMap.of("config", items.toJavaList());
    //second query
    parsedResponse = ConfigFactory.parseString(tests.putTest(this.getCookie(), queryMap, body).execute().body().string());
    Assertions.assertThat(
      List.ofAll(parsedResponse.getConfigList("items"))
        .find(v -> Try.of(() -> Ints.tryParse(v.getString("id"))).getOrElse(0).equals(type))
        .map(v -> Doubles.tryParse(v.getString("usage_predict_increase")))
        .getOrElse(0D)
    ).isCloseTo(change, Percentage.withPercentage(accuracy));
  }

  private void usageThreSugByTypetest(DmApiTestInterface tests, Map<String, String> queryMap, int type) throws IOException {
    Config parsedResponse = ConfigFactory.parseString(tests.dm(this.getCookie(), queryMap).execute().body().string());
    Seq<Map<String, Object>> items = List.ofAll(parsedResponse.getConfigList("items"))
      .map(v -> Tuple.of(v.getString("id"), v.getConfigList("items"), v.getString("usage_predict")))
      .map(v -> Tuple.of(Ints.tryParse(v._1), v._2, Doubles.tryParse(v._3)))
      .filter(v -> v._1.equals(type))
      .flatMap(v -> List.ofAll(v._2).map(sub -> ImmutableMap.of("id", Ints.tryParse(sub.getString("id")), "threshold", ImmutableList.of(v._3 - 0.5D, v._3 - 0.1D))));
    Map<String, Object> body = ImmutableMap.of("config", items.toJavaList());
    //second query
    parsedResponse = ConfigFactory.parseString(tests.putTest(this.getCookie(), queryMap, body).execute().body().string());
    Assertions.assertThat(
      List.ofAll(parsedResponse.getConfigList("items"))
        .find(v -> Try.of(() -> Ints.tryParse(v.getString("id"))).getOrElse(0).equals(type))
        .map(v -> List.ofAll(v.getConfigList("suggestions")).map(sub -> sub.getString("title")).toJavaList())
        .getOrElse(List.of("").toJavaList())
    ).contains("建议购买");

    //do it again, test low level
    parsedResponse = ConfigFactory.parseString(tests.dm(this.getCookie(), queryMap).execute().body().string());
    Seq<Map<String, Object>> items2 = List.ofAll(parsedResponse.getConfigList("items"))
      .map(v -> Tuple.of(v.getString("id"), v.getConfigList("items"), v.getString("usage_predict")))
      .map(v -> Tuple.of(Ints.tryParse(v._1), v._2, Doubles.tryParse(v._3)))
      .filter(v -> v._1.equals(type))
      .flatMap(v -> List.ofAll(v._2).map(sub -> ImmutableMap.of("id", Ints.tryParse(sub.getString("id")), "threshold", ImmutableList.of(v._3 + 0.1D, v._3 + 0.6D))));
    Map<String, Object> body2 = ImmutableMap.of("config", items2.toJavaList());
    //second query
    parsedResponse = ConfigFactory.parseString(tests.putTest(this.getCookie(), queryMap, body2).execute().body().string());
    Assertions.assertThat(
      List.ofAll(parsedResponse.getConfigList("items"))
        .find(v -> Try.of(() -> Ints.tryParse(v.getString("id"))).getOrElse(0).equals(type))
        .map(v -> List.ofAll(v.getConfigList("suggestions")).map(sub -> sub.getString("title")).toJavaList())
        .getOrElse(List.of("").toJavaList())
    ).contains("建议提高使用率");
  }


  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }


  //positive tests
  @Test
  public void testGroupby() throws IOException {
    doOkTest(tests, ImmutableMap.of("groupby", "dept", "year", "2017"));
    doOkTest(tests, ImmutableMap.of("groupby", "type", "year", "2017"));
  }

  @Test
  public void testExtraParameter() throws IOException {
    doOkTest(tests, ImmutableMap.of("groupby", "type", "dummy", "foo", "year", "2017"));
    doOkTest(tests, ImmutableMap.of("groupby", "dept", "foo", "2", "year", "2017"));
  }

  //negative tests
  @Test
  public void testWrongPath() throws IOException {
    doWrongPathTest(tests, "dummy");
    doWrongPathTest(tests, "foo");
  }

  @Test
  public void testLackGroupby() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("year", "2017"));
    doNegativeTest(tests, ImmutableMap.of("foo", "2", "year", "2017"));
    doNegativeTest(tests, ImmutableMap.of("dummy", "foo", "foo", "2", "year", "2017"));
  }

  @Test
  public void testWrongGroupby() throws IOException {
    doNegativeTest(tests, ImmutableMap.of("groupby", "5", "year", "2017"));
    doNegativeTest(tests, ImmutableMap.of("groupby", "month", "year", "2017"));
    doNegativeTest(tests, ImmutableMap.of("groupby", "year", "foo", "dummy", "year", "2017"));
  }

  //logic tests
  @Test
  public void testUsageStatistics() throws IOException {
    usageStatisticsByDeptTest(tests, ImmutableMap.of("groupby", "dept", "year", "2017"));
    usageStatisticsByTypeTest(tests, ImmutableMap.of("groupby", "type", "year", "2017"));
    usageStatisticsByTypeTest(tests, ImmutableMap.of("groupby", "type", "dummy", "foo", "year", "2017"));
    usageStatisticsByDeptTest(tests, ImmutableMap.of("groupby", "dept", "foo", "2", "year", "2017"));
  }

  @Test
  public void testAverageUsage() throws IOException {
    averageUsageGroupByTest(tests, ImmutableMap.of("groupby", "type", "year", "2017"));
    averageUsageGroupByTest(tests, ImmutableMap.of("groupby", "type", "year", "2018"));
    averageUsageGroupByTest(tests, ImmutableMap.of("groupby", "dept", "year", "2017"));
    averageUsageGroupByTest(tests, ImmutableMap.of("groupby", "dept", "year", "2018"));
  }

  @Test
  public void testUsageGroupbyTypePut() throws IOException {
    usageGroupByTypePutTest(tests, ImmutableMap.of("groupby", "type", "year", "2017"), 0.3D, 1);
    usageGroupByTypePutTest(tests, ImmutableMap.of("groupby", "type", "year", "2018"), 0.3D, 2);
    usageGroupByTypePutTest(tests, ImmutableMap.of("groupby", "type", "year", "2017"), -0.5D, 3);
    usageGroupByTypePutTest(tests, ImmutableMap.of("groupby", "type", "year", "2018"), -0.5D, 4);
    usageGroupByTypePutTest(tests, ImmutableMap.of("groupby", "type", "year", "2017"), 10D, 5);
    usageGroupByTypePutTest(tests, ImmutableMap.of("groupby", "type", "year", "2018"), 10D, 6);
  }

  @Test
  public void testUsageThreGroupByType() throws IOException {
    usageThreSugByTypetest(tests, ImmutableMap.of("groupby", "type", "year", "2017"), 1);
    usageThreSugByTypetest(tests, ImmutableMap.of("groupby", "type", "year", "2018"), 2);
    usageThreSugByTypetest(tests, ImmutableMap.of("groupby", "type", "year", "2017"), 3);
    usageThreSugByTypetest(tests, ImmutableMap.of("groupby", "type", "year", "2018"), 4);
    usageThreSugByTypetest(tests, ImmutableMap.of("groupby", "type", "year", "2017"), 5);
    usageThreSugByTypetest(tests, ImmutableMap.of("groupby", "type", "year", "2018"), 6);
  }
}


interface DmApiTestInterface {
  @Headers("accept:application/json")
  @GET("api/dmv2")
  Call<ResponseBody> dm(@Header("Cookie") String cookie, @QueryMap Map<String, String> options);

  @Headers("accept:application/json")
  @GET("api/dmv2/{wrongPath}")
  Call<ResponseBody> wrongPath(@Header("Cookie") String cookie, @Path("wrongPath") String wrongPath);

  @Headers("accept:application/json")
  @PUT("api/dmv2")
  Call<ResponseBody> putTest(@Header("Cookie") String cookie, @QueryMap Map<String, String> options, @Body Object body);
}