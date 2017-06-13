package com.get.apm.api.ft;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.Tuple3;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MaApiTest extends AbstractApiTest {
  private MaApiTestInterface tests;
  private final double accuracy = 0.1D;

  public MaApiTest() {
    super();
    tests = this.getRetrofit().create(MaApiTestInterface.class);
  }

  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }

  private void formatTests(MaApiTestInterface tests, Map<String, String> queryMap, Integer id, Object body, String condition) throws IOException {
    Response<ResponseBody> response = tests.ma(this.getCookie(), queryMap).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("root");

    response = tests.asset(id, this.getCookie(), queryMap).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("id");
  }

  private void formatTestsFuture(MaApiTestInterface tests, Map<String, String> queryMap, Integer id, Object body, String condition) throws IOException {
    Response<ResponseBody> response = tests.forecast(this.getCookie(), queryMap, body).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("root");

    response = tests.forecastrate(this.getCookie(), queryMap, body).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("root");

    response = tests.suggestion(condition, this.getCookie(), queryMap, body).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("count");

    response = tests.forecastasset(id, this.getCookie(), queryMap, body).execute();
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.body()).isNotNull();
    Assertions.assertThat(response.body().contentType().toString()).isEqualTo("application/json;charset=UTF-8");
    Assertions.assertThat(response.body().string()).contains("id");
  }


  private Map<String, String> maps(LocalDate from, LocalDate to, String groupBy, Integer dept,
                                   Integer type, Integer supplier, String rltgrp, String msa, Integer start, Integer limit) {
    ImmutableMap.Builder<String, String> result = new ImmutableMap.Builder<>();
    if (from != null) {
      result.put("from", from.toString());
    }
    if (to != null) {
      result.put("to", to.toString());
    }
    if (groupBy != null) {
      result.put("groupby", groupBy);
    }
    if (dept != null) {
      result.put("dept", dept.toString());
    }
    if (type != null) {
      result.put("type", type.toString());
    }
    if (supplier != null) {
      result.put("supplier", supplier.toString());
    }
    if (rltgrp != null) {
      result.put("rltgrp", rltgrp);
    }
    if (msa != null) {
      result.put("msa", msa);
    }
    if (start != null) {
      result.put("start", start.toString());
    }
    if (limit != null) {
      result.put("limit", limit.toString());
    }
    return result.build();
  }

  private Object bodies(List<ImmutableMap<String, Object>> pairs, List<Double> tr1, List<Double> tr2, List<Double> tr3) {
    if (tr1 == null) {
      tr1 = ImmutableList.of(0.8);
    }
    if (tr2 == null) {
      tr2 = ImmutableList.of(0.1);
    }
    if (tr3 == null) {
      tr3 = ImmutableList.of(0.8, 0.95, 0.1, 0.01);
    }
    return new ImmutableMap.Builder<String, Object>()
      .put("threshold", new ImmutableMap.Builder<String, Object>()
        .put("condition1", tr1)
        .put("condition2", tr2)
        .put("condition3", tr3)
        .build())
      .put("items", pairs)
      .build();
  }

  private void forecastHistoryJointTest(MaApiTestInterface tests, Map<String, String> queryMap,
                                        List<ImmutableMap<String, Object>> pairs, List<Double> tr1, List<Double> tr2, List<Double> tr3) throws IOException {
    pairs.forEach(v -> {
      String preAst = Try.of(() -> tests.forecastasset((Integer) v.get("id"), this.getCookie(),
        new ImmutableMap.Builder<String, String>().putAll(queryMap).put("from", LocalDate.now().withDayOfYear(1).toString()).put("to", LocalDate.now().withDayOfYear(1).plusYears(1).minusDays(1).toString()).build(),
        bodies(pairs, tr1, tr2, tr3)).execute().body().string()).getOrElseGet(e -> {
        e.printStackTrace();
        return "forecastasset fails";
      });

      Assertions.assertThat(preAst).isNotEqualTo("forecastasset fails");

      String preRate = Try.of(() -> tests.forecastrate(this.getCookie(),
        new ImmutableMap.Builder<String, String>().putAll(queryMap).put("from", LocalDate.now().withDayOfYear(1).toString()).put("to", LocalDate.now().withDayOfYear(1).plusYears(1).minusDays(1).toString()).build(),
        bodies(pairs, tr1, tr2, tr3)).execute().body().string()).getOrElse("forecastrate fails");

      Assertions.assertThat(preRate).isNotEqualTo("forecastrate fails");

      String hisAst = Try.of(() -> tests.asset((Integer) v.get("id"), this.getCookie(),
        new ImmutableMap.Builder<String, String>().putAll(queryMap).put("from", LocalDate.now().withDayOfYear(1).minusYears(1).toString()).put("to", LocalDate.now().withDayOfYear(1).minusDays(1).toString()).build())
        .execute().body().string()).getOrElse("asset fails");

      Assertions.assertThat(hisAst).isNotEqualTo("asset fails");

      if (v.get("onrate_increase") != null) {
        Assertions.assertThat(Try.of(() -> ConfigFactory.parseString(preAst.substring(1, preAst.length() - 1)).getDouble("onrate")).getOrElse(0D))
          .isCloseTo((Double) v.getOrDefault("onrate_increase", 0D), Percentage.withPercentage(accuracy));
      }

      if (v.get("cost1_increase") != null) {
        Assertions.assertThat(Try.of(() -> ConfigFactory.parseString(preAst.substring(1, preAst.length() - 1)).getDouble("acyman".equals(queryMap.get("rltgrp")) ? "labor" : "repair")).getOrElse(0D))
          .isCloseTo(Try.of(() -> ConfigFactory.parseString(hisAst.substring(1, hisAst.length() - 1)).getDouble("acyman".equals(queryMap.get("rltgrp")) ? "labor" : "repair")).getOrElse(0D) *
            (1D + (Double) v.get("cost1_increase")), Percentage.withPercentage(accuracy));

        Assertions.assertThat(Try.of(() -> javaslang.collection.List.ofAll(ConfigFactory.parseString(preRate).getConfigList("items"))
          .filter(it -> it.getInt("id") == (Integer) v.get("id")
          ).get(0).getDouble("acyman".equals(queryMap.get("rltgrp")) ? "labor_increase" : "repair_increase")).getOrElse(0D))
          .isCloseTo((Double) v.get("cost1_increase"), Percentage.withPercentage(accuracy));
      }

      if (v.get("cost2_increase") != null) {
        Assertions.assertThat(Try.of(() -> ConfigFactory.parseString(preAst.substring(1, preAst.length() - 1)).getDouble("acyman".equals(queryMap.get("rltgrp")) ? "parts" : "PM")).getOrElse(0D))
          .isCloseTo(Try.of(() -> ConfigFactory.parseString(hisAst.substring(1, hisAst.length() - 1)).getDouble("acyman".equals(queryMap.get("rltgrp")) ? "parts" : "PM")).getOrElse(0D) *
            (1D + (Double) v.get("cost2_increase")), Percentage.withPercentage(accuracy));

        Assertions.assertThat(Try.of(() -> javaslang.collection.List.ofAll(ConfigFactory.parseString(preRate).getConfigList("items"))
          .filter(it -> it.getInt("id") == (Integer) v.get("id")
          ).get(0).getDouble("acyman".equals(queryMap.get("rltgrp")) ? "parts_increase" : "PM_increase")).getOrElse(0D))
          .isCloseTo((Double) v.get("cost2_increase"), Percentage.withPercentage(accuracy));
      }
    });
  }

  private void hisDataConsistencyTest(MaApiTestInterface tests, Map<String, String> queryMap) throws IOException {

    String responseBody = tests.ma(this.getCookie(), queryMap).execute().body().string();

    Config root = ConfigFactory.parseString(responseBody).getConfig("root");

    Tuple2<Double, Double> rootV = Tuple.of(root.getDouble("acyman".equals(queryMap.get("rltgrp")) ? "labor" : "repair"),
      root.getDouble("acyman".equals(queryMap.get("rltgrp")) ? "parts" : "PM"));

    Tuple3<Double, Double, Double> itemSum =
      javaslang.collection.List.ofAll(ConfigFactory.parseString(responseBody).getConfigList("items"))
        .map(v -> v.getInt("id"))
        .map(v -> {
          String items = Try.of(() -> tests.asset(v, this.getCookie(), queryMap).execute().body().string()).getOrElse("query for item fails");
          Assertions.assertThat(items).isNotEqualTo("query for item fails");
          Config itemSingle = ConfigFactory.parseString(items.substring(1, items.length() - 1));
          return Tuple.of(itemSingle.getDouble("acyman".equals(queryMap.get("rltgrp")) ? "labor" : "repair"),
            itemSingle.getDouble("acyman".equals(queryMap.get("rltgrp")) ? "parts" : "PM"), itemSingle.getDouble("price"));
        }).reduceLeftOption((init, v) -> Tuple.of(init._1 + v._1, init._2 + v._2, init._3 + v._3)).getOrElse(Tuple.of(0D, 0D, 0D));

    Assertions.assertThat(rootV._1).isCloseTo(itemSum._1, Percentage.withPercentage(accuracy));
    Assertions.assertThat(rootV._2).isCloseTo(itemSum._2, Percentage.withPercentage(accuracy));
  }

  private void forecastDataConsistencyTest(MaApiTestInterface tests, Map<String, String> queryMap, Object body) throws IOException {
    String responseBody = tests.forecast(this.getCookie(), queryMap, body).execute().body().string();

    Config root = ConfigFactory.parseString(responseBody).getConfig("root");

    Tuple2<Double, Double> rootV = Tuple.of(root.getDouble("acyman".equals(queryMap.get("rltgrp")) ? "labor" : "repair"),
      root.getDouble("acyman".equals(queryMap.get("rltgrp")) ? "parts" : "PM"));

    Tuple3<Double, Double, Double> itemSum =
      javaslang.collection.List.ofAll(ConfigFactory.parseString(responseBody).getConfigList("items"))
        .map(v -> v.getInt("id"))
        .map(v -> {
          String items = Try.of(() -> tests.forecastasset(v, this.getCookie(), queryMap, body).execute().body().string()).getOrElse("query for item fails");
          Assertions.assertThat(items).isNotEqualTo("query for item fails");
          Config itemSingle = ConfigFactory.parseString(items.substring(1, items.length() - 1));
          return Tuple.of(itemSingle.getDouble("acyman".equals(queryMap.get("rltgrp")) ? "labor" : "repair"),
            itemSingle.getDouble("acyman".equals(queryMap.get("rltgrp")) ? "parts" : "PM"), itemSingle.getDouble("price"));
        }).reduceLeftOption((init, v) -> Tuple.of(init._1 + v._1, init._2 + v._2, init._3 + v._3)).getOrElse(Tuple.of(0D, 0D, 0D));

    Assertions.assertThat(rootV._1).isCloseTo(itemSum._1, Percentage.withPercentage(accuracy));
    Assertions.assertThat(rootV._2).isCloseTo(itemSum._2, Percentage.withPercentage(accuracy));
  }


  @Test
  public void testFormat() throws IOException {
    formatTests(tests, maps(LocalDate.now().minusYears(1), LocalDate.now(), "type", null, 2, null, "acyman", null, 0, 50),
      3, bodies(ImmutableList.of(ImmutableMap.of("id", 1, "onrate_increase", 1.05), ImmutableMap.of("id", 5, "cost1_increase", 0.05, "cost2_increase", 0.07),
        ImmutableMap.of("id", 7, "onrate_increase", 1.10, "cost1_increase", 0.01, "cost2_increase", 0.08)), null, null, null), "lowonrate");
    formatTests(tests, maps(LocalDate.now().minusYears(2), LocalDate.now().withDayOfYear(1), null, 1, null, null, "mtpm", "no", null, null),
      2, bodies(ImmutableList.of(ImmutableMap.of("id", 2, "onrate_increase", 1.02), ImmutableMap.of("id", 4, "cost1_increase", -1D, "cost2_increase", 0.5),
        ImmutableMap.of("id", 6, "onrate_increase", 2D, "cost1_increase", 0.1, "cost2_increase", -0.5)), ImmutableList.of(0.7), null, null), "highcost");
  }

  @Test
  public void testFormatFuture() throws IOException {
    formatTestsFuture(tests, maps(LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(1).plusYears(1).minusDays(1), "supplier", null, null, null, "acyman", "yes", 0, 50),
      90, bodies(ImmutableList.of(ImmutableMap.of("id", 1, "onrate_increase", 1.05), ImmutableMap.of("id", 5, "cost1_increase", 0.05, "cost2_increase", 0.07),
        ImmutableMap.of("id", 7, "onrate_increase", 1.10, "cost1_increase", 0.01, "cost2_increase", 0.08)), null, ImmutableList.of(0.2), null), "bindonratecost");
    formatTestsFuture(tests, maps(LocalDate.now().withDayOfYear(1).plusYears(1), LocalDate.now().withDayOfYear(1).plusYears(2).minusDays(1), "type", null, null, 10, "mtpm", "no", null, null),
      5, bodies(ImmutableList.of(ImmutableMap.of("id", 2, "onrate_increase", 1.02), ImmutableMap.of("id", 4, "cost1_increase", -1D, "cost2_increase", 0.5),
        ImmutableMap.of("id", 6, "onrate_increase", 2D, "cost1_increase", 0.1, "cost2_increase", -0.5)), ImmutableList.of(0.7), null, ImmutableList.of(0.7, 0.88, 0.15, 0.02)), "highcost");
  }

  //logic test
  @Test
  public void testHistoryForecastJoint() throws IOException {
    forecastHistoryJointTest(tests, maps(null, null, null, null, null, null, "acyman", null, null, null),
      ImmutableList.of(ImmutableMap.of("id", 1, "onrate_increase", 1.05), ImmutableMap.of("id", 5, "cost1_increase", 0.05, "cost2_increase", 0.07),
        ImmutableMap.of("id", 7, "onrate_increase", 1.10, "cost1_increase", 0.01, "cost2_increase", 0.08)), null, null, null);
    forecastHistoryJointTest(tests, maps(null, null, null, null, null, null, "mtpm", null, null, null),
      ImmutableList.of(ImmutableMap.of("id", 2, "onrate_increase", 1.02), ImmutableMap.of("id", 4, "cost1_increase", -1D, "cost2_increase", 0.5),
        ImmutableMap.of("id", 6, "onrate_increase", 2D, "cost1_increase", 0.1, "cost2_increase", -0.5)), null, null, null);
  }

  @Test
  public void testHisDataConsistency() throws IOException {
    hisDataConsistencyTest(tests, maps(LocalDate.now().withDayOfYear(1), LocalDate.now(), null, null, 1, null, "acyman", null, null, null));
    hisDataConsistencyTest(tests, maps(LocalDate.now().minusYears(1), LocalDate.now().minusMonths(2), null, null, null, 2, "mtpm", null, null, null));
    hisDataConsistencyTest(tests, maps(LocalDate.now().minusYears(3), LocalDate.now().minusMonths(1), null, 3, null, 2, "mtpm", null, null, null));
    hisDataConsistencyTest(tests, maps(LocalDate.now().minusYears(2), LocalDate.now().minusMonths(4).plusDays(2), null, 3, 1, 2, "acyman", null, 0, null));
  }

  @Test
  public void testForecastDataConsistency() throws IOException {
    forecastDataConsistencyTest(tests, maps(LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(1).plusYears(1).minusDays(1),
      null, null, 1, null, "acyman", null, null, null),
      bodies(ImmutableList.of(ImmutableMap.of("id", 1, "onrate_increase", 1.05), ImmutableMap.of("id", 5, "cost1_increase", 0.05, "cost2_increase", 0.07),
        ImmutableMap.of("id", 7, "onrate_increase", 1.10, "cost1_increase", 0.01, "cost2_increase", 0.08)), null, null, null));

    forecastDataConsistencyTest(tests, maps(LocalDate.now().withDayOfYear(1).plusYears(1), LocalDate.now().withDayOfYear(1).plusYears(2).minusDays(1),
      null, null, null, 2, "mtpm", null, null, null),
      bodies(ImmutableList.of(ImmutableMap.of("id", 2, "onrate_increase", 1.02), ImmutableMap.of("id", 4, "cost1_increase", -1D, "cost2_increase", 0.5),
        ImmutableMap.of("id", 6, "onrate_increase", 2D, "cost1_increase", 0.1, "cost2_increase", -0.5)), null, null, null));

    forecastDataConsistencyTest(tests, maps(LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(1).plusYears(1).minusDays(1),
      null, 3, null, 2, "mtpm", null, null, null),
      bodies(ImmutableList.of(ImmutableMap.of("id", 1, "onrate_increase", 1.05), ImmutableMap.of("id", 5, "cost1_increase", 0.05, "cost2_increase", 0.07),
        ImmutableMap.of("id", 7, "onrate_increase", 1.10, "cost1_increase", 0.01, "cost2_increase", 0.08)), null, null, null));

    forecastDataConsistencyTest(tests, maps(LocalDate.now().withDayOfYear(1).plusYears(1), LocalDate.now().withDayOfYear(1).plusYears(2).minusDays(1),
      null, 3, 1, 2, "acyman", null, 0, null),
      bodies(ImmutableList.of(ImmutableMap.of("id", 2, "onrate_increase", 1.02), ImmutableMap.of("id", 4, "cost1_increase", -1D, "cost2_increase", 0.5),
        ImmutableMap.of("id", 6, "onrate_increase", 2D, "cost1_increase", 0.1, "cost2_increase", -0.5)), null, null, null));
  }
}


interface MaApiTestInterface {
  @Headers("accept: application/json")
  @GET(value = "api/ma")
  Call<ResponseBody> ma(@Header("Cookie") String cookie,
                        @QueryMap Map<String, String> options);

  @Headers("accept: application/json")
  @GET(value = "api/ma/asset/{id}")
  Call<ResponseBody> asset(@Path(value = "id") Integer id, @Header("Cookie") String cookie,
                           @QueryMap Map<String, String> options);

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
  Call<ResponseBody> suggestion(@Path(value = "condition") String condition, @Header("Cookie") String cookie,
                                @QueryMap Map<String, String> options, @Body Object body);

  @Headers("accept: application/json")
  @PUT(value = "api/ma/forecast/asset/{id}")
  Call<ResponseBody> forecastasset(@Path(value = "id") Integer id, @Header("Cookie") String cookie,
                                   @QueryMap Map<String, String> options, @Body Object body);
}