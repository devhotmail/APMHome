package com.get.apm.api.ft;

import com.ge.apm.web.ProfitApi;
import com.google.common.base.Stopwatch;
import okhttp3.*;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AbstractApiTest {
  private static final Logger log = LoggerFactory.getLogger(ProfitApi.class);
  protected String Cookie;

  private OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new Interceptor() {
      @Override
      public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("Sending request {}", request.url());
        Response response = chain.proceed(request);
        stopwatch.stop();
        log.info("Received response for {} in {} ms", response.request().url(), stopwatch.elapsed(TimeUnit.MILLISECONDS));

        return response;
      }
    }).build();


  protected Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("http://localhost:8080/geapm/")
    .addConverterFactory(JacksonConverterFactory.create())
    .client(client)
    .build();

  @Before
  public void login() throws IOException {
    LoginInterface loginInterface = this.retrofit.create(LoginInterface.class);

    String loginInfo = "javax.faces.partial.ajax=true&javax.faces.source=j_idt19&javax.faces.partial.execute=%40all&javax.faces.partial.render=growl&j_idt19=j_idt19&j_idt13=j_idt13&j_username=admin&j_password=111&javax.faces.ViewState=stateless";
    RequestBody body = RequestBody.create(MediaType.parse("text/plain"), loginInfo);

    Call<ResponseBody> call = loginInterface.testLogin(body);
    okhttp3.Headers responseHeader = call.execute().headers();
    this.Cookie = responseHeader.get("Set-Cookie");
  }

}


interface LoginInterface {
  @Headers("Content-Type: application/x-www-form-urlencoded")
  @POST("login.xhtml")
  retrofit2.Call<ResponseBody> testLogin(@Body RequestBody body);
}