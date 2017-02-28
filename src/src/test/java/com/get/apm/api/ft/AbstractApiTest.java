package com.get.apm.api.ft;

import com.google.common.base.Stopwatch;
import com.typesafe.config.ConfigFactory;
import javaslang.control.Option;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class AbstractApiTest {
  private static final Logger log = LoggerFactory.getLogger(AbstractApiTest.class);
  private String Cookie;
  private Retrofit retrofit;

  private void setCookie(String cookie) {
    this.Cookie = cookie;
  }

  public String getCookie() {
    return this.Cookie;
  }

  public Retrofit getRetrofit() {
    return this.retrofit;
  }

  public AbstractApiTest() {
    String sysVcap = System.getenv("VCAP");
    Proxy proxyTest = (sysVcap != null && !sysVcap.isEmpty()) ? (new Proxy(Proxy.Type.HTTP, new InetSocketAddress("3.20.128.6", 88))) : (new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8080)));


    OkHttpClient client = new OkHttpClient.Builder()
      .proxy(proxyTest)
      .addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
          Request request = chain.request();
          Stopwatch stopwatch = Stopwatch.createStarted();
          log.info("Sending request {}", request.url());
          Response response = chain.proceed(request);
          stopwatch.stop();
          log.info("Received response for {} in {} ms", response.request().url(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
          log.info("response: {}", response);

          return response;
        }
      }).build();

    String baseUrl = ((sysVcap != null && !sysVcap.isEmpty()) ? "https://" + ConfigFactory.parseString("{" + sysVcap.substring(sysVcap.indexOf("VCAP_APPLICATION") - 3, sysVcap.indexOf("User-Provided") - 1)).getStringList("VCAP_APPLICATION.application_uris").get(0) : "http://localhost:8080/geapm/");
    this.retrofit = new Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(JacksonConverterFactory.create())
      .client(client)
      .build();
  }


  public void login(String username, String password) throws IOException {
    LoginInterface loginInterface = this.getRetrofit().create(LoginInterface.class);
    String loginInfo = "javax.faces.partial.ajax=true&javax.faces.source=j_idt19&javax.faces.partial.execute=%40all&javax.faces.partial.render=growl&j_idt19=j_idt19&j_idt13=j_idt13&j_username=" + Option.of(username).getOrElse(System.getProperty("j_username", "admin")) + "&j_password=" + Option.of(password).getOrElse(System.getProperty("j_password", "111")) + "&javax.faces.ViewState=stateless";
    RequestBody body = RequestBody.create(MediaType.parse("text/plain"), loginInfo);

    okhttp3.Headers responseHeader;
    Call<ResponseBody> call = loginInterface.testLogin(body);
    try {
      responseHeader = call.execute().headers();
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
    this.setCookie(responseHeader.get("Set-Cookie"));
  }


}


interface LoginInterface {
  @Headers("Content-Type: application/x-www-form-urlencoded")
  @POST("login.xhtml")
  retrofit2.Call<ResponseBody> testLogin(@Body RequestBody body);
}