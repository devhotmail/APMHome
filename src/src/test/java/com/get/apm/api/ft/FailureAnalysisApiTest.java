package com.get.apm.api.ft;


import com.google.common.collect.ImmutableMap;
import okhttp3.ResponseBody;
import org.junit.Before;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

import java.io.IOException;
import java.util.Map;

public class FailureAnalysisApiTest extends AbstractApiTest {
  private FailureAnalysisApiTestInterface tests;

  public FailureAnalysisApiTest() {
    super();
    tests = this.getRetrofit().create(FailureAnalysisApiTestInterface.class);
  }

  @Before
  public void configEnv() throws IOException {
    this.login("admin", "111");
  }

  private Map<String, String> maps(String from, String to, Integer type, Integer dept, Integer supplier, Integer asset) {
    ImmutableMap.Builder<String, String> result = new ImmutableMap.Builder<>();
    if (from != null) {
      result.put("from", from);
    }
    if (to != null) {
      result.put("to", to);
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
    return result.build();
  }

  private void formatTest(String from, String to, Integer type, Integer dept, Integer supplier, Integer asset) {

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
