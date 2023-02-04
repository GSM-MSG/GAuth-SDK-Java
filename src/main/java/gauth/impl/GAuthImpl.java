package gauth.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import gauth.GAuth;
import gauth.GAuthCode;
import gauth.GAuthToken;
import gauth.GAuthUserInfo;
import gauth.exception.GAuthException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GAuthImpl implements GAuth {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String GAuthServerURL = "https://server.gauth.co.kr/oauth";
    private final String ResourceServerURL = "https://open.gauth.co.kr";

    private enum Auth{
        ACCESS,
        REFRESH
    }
    public GAuthToken generateToken(String email, String password, String clientId, String clientSecret, String redirectUri) throws IOException {
        String code = generateCode(email, password).getCode();
        return new GAuthToken(getToken(code, clientId, clientSecret, redirectUri));
    }

    public GAuthToken generateToken(String code, String clientId, String clientSecret, String redirectUri) throws IOException {
        return new GAuthToken(getToken(code, clientId, clientSecret, redirectUri));
    }

    public GAuthCode generateCode(String email, String password) throws IOException {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        String code = sendPostGAuthServer(body, null, "/code").get("code");
        return new GAuthCode(code);
    }

    public GAuthToken refresh(String refreshToken) throws IOException{
        if(!refreshToken.startsWith("Bearer "))
            refreshToken = "Bearer "+refreshToken;
        return new GAuthToken(sendPatchGAuthServer(null, refreshToken, "/token", Auth.REFRESH));
    }

    public GAuthUserInfo getUserInfo(String accessToken) throws IOException{
        if(!accessToken.startsWith("Bearer "))
            accessToken = "Bearer "+accessToken;
        Map<String, Object> map = sendGetResourceServer(accessToken, "/user");
        return new GAuthUserInfo(map);
    }

    private Map<String, String> getToken(String code, String clientId, String clientSecret, String redirectUri) throws IOException {
        Map<String, String> body = new HashMap<>();
        body.put("code", code);
        body.put("clientId", clientId);
        body.put("clientSecret", clientSecret);
        body.put("redirectUri", redirectUri);
        return sendPostGAuthServer(body, null, "/token");
    }

    private Map<String, String> sendPostGAuthServer(Map<String, String> body, String token, String url) throws IOException {
        return sendPost(body, token, GAuthServerURL+url);
    }

    private Map<String, String> sendPatchGAuthServer(Map<String, String> body, String token, String url, Auth auth) throws IOException {
        return sendPatch(body, token, GAuthServerURL+ url, auth);
    }

    private Map<String, Object> sendGetResourceServer(String token, String url) throws IOException {
        return sendGet(token, ResourceServerURL + url);
    }

    private Map<String, Object> sendGet(String token, String url) throws IOException {
        HttpGet request = new HttpGet(url); //GET 메소드 URL 생성
        request.addHeader("Authorization", token);
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);
        if(response.getStatusLine().getStatusCode()!=200)
            throw new RuntimeException();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String responseBody = bufferedReader.readLine();
        bufferedReader.close();
        return mapper.readValue(responseBody, Map.class);
    }

    private Map<String, String> sendPatch(Map<String, String> body, String token, String url, Auth auth) throws IOException {
        HttpPatch request = new HttpPatch(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Content-Type", "application/json");
        if(auth == Auth.ACCESS)
            request.setHeader("Authorization", token);
        else
            request.addHeader("refreshToken", token);
        if(body != null){
            String json = new JSONObject(body).toJSONString();
            request.setEntity(new StringEntity(json));
        }
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(request);
        if(response.getStatusLine().getStatusCode()!=200)
            throw new RuntimeException();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String responseBody = bufferedReader.readLine();
        bufferedReader.close();
        return mapper.readValue(responseBody, Map.class);
    }

    private Map<String, String> sendPost(Map<String, String> body, String token, String url) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Content-Type", "application/json");
        request.addHeader("Authorization", token);
        if(body != null){
            String json = new JSONObject(body).toJSONString();
            request.setEntity(new StringEntity(json));
        }
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(request);
        if(response.getStatusLine().getStatusCode()!=200)
            throw new GAuthException(response.getStatusLine().getStatusCode());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String responseBody = bufferedReader.readLine();
        bufferedReader.close();
        return mapper.readValue(responseBody, Map.class);
    }


}