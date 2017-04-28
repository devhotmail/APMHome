package com.ge.apm.service.utils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {

	public static String getAuthorizationByWechatId(String url, String wechatId) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";

		JSONObject jsonBody = new JSONObject();
		jsonBody.put("weChatId", wechatId);

		try {
			//HttpPost request = new HttpPost("http://localhost:8090/api/apm/security/userAccounts/authenticateWeChat");
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(jsonBody.toJSONString(), "UTF-8");
			request.addHeader("content-type", "application/json");
			request.addHeader("Accept", "application/json");
			request.setEntity(params);
			response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println(statusCode);

			if(response != null){
				resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		JSONObject json = JSONObject.parseObject(resultString);

		String authorization = json.getJSONObject("data").getString("id_token");

		return authorization;

	}

}
