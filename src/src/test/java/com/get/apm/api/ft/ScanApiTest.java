package com.get.apm.api.ft;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.typesafe.config.ConfigFactory;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.control.Try;
import okhttp3.ResponseBody;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class ScanApiTest extends AbstractApiTest {
  private ScanApiTestInterface tests;

  public ScanApiTest() {
    super();
    tests = this.getRetrofit().create(ScanApiTestInterface.class);
  }

  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }

  private void briefDetailContinuityTest(ScanApiTestInterface tests,
                                         LocalDate from, LocalDate to,
                                         Integer type, Integer dept, Integer asset)
    throws IOException {
    ImmutableMap.Builder<String, String> builder =
      new ImmutableMap.Builder<String, String>()
        .put("limit", String.format("%s", Integer.MAX_VALUE));
    if (from != null) {
      builder = builder.put("from", from.toString());
    }
    if (to != null) {
      builder = builder.put("to", to.toString());
    }
    if (type != null) {
      builder = builder.put("type", type.toString());
    }
    if (dept != null) {
      builder = builder.put("dept", dept.toString());
    }
    if (asset != null) {
      builder = builder.put("asset", asset.toString());
    }
    Map<String, String> briefQueryMap = builder.build();

    //query for brief
    String briefRes = tests.brief(this.getCookie(), briefQueryMap).execute().body().string();

    //parse data and test each exam part with data got from detail()
    List.ofAll(ConfigFactory.parseString(briefRes).getConfigList("brief"))
      .map(v -> Tuple.of(
        Ints.tryParse(v.getConfig("type").getString("id")),
        List.ofAll(v.getConfig("items").getConfigList("data"))
          .map(sub -> Tuple.of(Ints.tryParse(sub.getString("id")), Longs.tryParse(sub.getString("count"))))
        )
      ).forEach(v -> v._2.forEach(
      sub -> checkEachPart(from, to, type, dept, asset, v._1, sub)
    ));
  }

  private void checkEachPart(LocalDate from, LocalDate to,
                             Integer type, Integer dept, Integer asset,
                             Integer currentType,
                             Tuple2<Integer, Long> onePartInType) {
    ImmutableMap.Builder<String, String> b =
      new ImmutableMap.Builder<String, String>()
        .put("part", onePartInType._1.toString())
        .put("groupby", "asset")
        .put("limit", String.format("%s", Integer.MAX_VALUE));
    if (from != null) {
      b = b.put("from", from.toString());
    }
    if (to != null) {
      b = b.put("to", to.toString());
    }
    if (type != null) {
      b = b.put("type", type.toString());
    }
    if (dept != null) {
      b = b.put("dept", dept.toString());
    }
    if (asset != null) {
      b = b.put("asset", asset.toString());
    }
    Map<String, String> detailQueryMap1 = b.build();
    //query for detail by asset
    String dtlAstRes =
      Try.of(() -> tests.detail(this.getCookie(), detailQueryMap1).execute
        ().body().string()).getOrElse("detail query by asset fails");

    //make sure query is success
    Assertions.assertThat(dtlAstRes).isNotEqualTo("detail query by asset fails");

    //parse the result and do further check
    Assertions.assertThat(
      List.ofAll(ConfigFactory.parseString(dtlAstRes).getConfigList("detail"))
        .filter(dt -> currentType.equals(Ints.tryParse(dt.getConfig("type").getString("id"))))
        .map(dt -> Longs.tryParse(dt.getConfig("items").getConfigList("data").get(0).getString("count")))
        .sum().longValue()
    ).isEqualTo(onePartInType._2);

    //query for detail by step
    Map<String, String> detailQueryMap2 =
      javaslang.collection.HashMap.ofAll(b.build())
        .put("groupby", "step").toJavaMap();
    dtlAstRes =
      Try.of(() -> tests.detail(this.getCookie(), detailQueryMap2).execute
        ().body().string()).getOrElse("detail query by step fails");

    //make sure query is success
    Assertions.assertThat(dtlAstRes).isNotEqualTo("detail query by step fails");

    //parse the result and do further check
    Assertions.assertThat(
      List.ofAll(ConfigFactory.parseString(dtlAstRes).getConfigList("detail"))
        .filter(dt -> currentType.equals(Ints.tryParse(dt.getConfig("type").getString("id"))))
        .filter(dt -> onePartInType._1.equals(Ints.tryParse(dt.getConfig("part").getString("id"))))
        .map(dt -> Longs.tryParse(dt.getConfig("step").getString("count")))
        .sum().longValue()
    ).isEqualTo(onePartInType._2);
  }

  @Test
  public void testNoPre() throws IOException {
    briefDetailContinuityTest(tests, LocalDate.now().withDayOfYear(1), LocalDate.now(),
      null, null,
      null);
  }
}


interface ScanApiTestInterface {
  @Headers("Accept: application/json")
  @GET("api/scan/brief")
  Call<ResponseBody> brief(@Header("Cookie") String cookie,
                           @QueryMap Map<String, String> options);

  @Headers("Accept: application/json")
  @GET("api/scan/detail")
  Call<ResponseBody> detail(@Header("Cookie") String cookie,
                            @QueryMap Map<String, String> options);
}