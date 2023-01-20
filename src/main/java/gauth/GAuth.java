package gauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GAuth {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String GAuthServerURL = "https://server.gauth.co.kr/oauth";
    private static final String ResourceServerURL = "https://open.gauth.co.kr";

    private enum Auth{
        ACCESS,
        REFRESH
    }
    public static Map<String, String> generateToken(String email, String password, String clientId, String clientSecret, String redirectUri) throws IOException {
        String code = generateCode(email, password);
        return getToken(code, clientId, clientSecret, redirectUri);
    }

    public static Map<String, String> generateToken(String code, String clientId, String clientSecret, String redirectUri) throws IOException {
        return getToken(code, clientId, clientSecret, redirectUri);
    }

    public static String generateCode(String email, String password) throws IOException {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        String code = sendPostGAuthServer(body, null, "/code").get("code");
        return code;
    }

    public static Map<String, String> refresh(String refreshToken) throws IOException{
        if(!refreshToken.startsWith("Bearer "))
            refreshToken = "Bearer "+refreshToken;
        return sendPatchGAuthServer(null, refreshToken, "/token", Auth.REFRESH);
    }

    public static Map<String, String> getUserInfo(String accessToken) throws IOException{
        if(!accessToken.startsWith("Bearer "))
            accessToken = "Bearer "+accessToken;
        return sendGetResourceServer(accessToken, "/user");
    }

    private static Map<String, String> getToken(String code, String clientId, String clientSecret, String redirectUri) throws IOException {
        Map<String, String> body = new HashMap<>();
        body.put("code", code);
        body.put("clientId", clientId);
        body.put("clientSecret", clientSecret);
        body.put("redirectUri", redirectUri);
        return sendPostGAuthServer(body, null, "/token");
    }

    private static Map<String, String> sendPostGAuthServer(Map<String, String> body, String token, String url) throws IOException {
        return sendPost(body, token, GAuthServerURL+url);
    }

    private static Map<String, String> sendPatchGAuthServer(Map<String, String> body, String token, String url, Auth auth) throws IOException {
        return sendPatch(body, token, GAuthServerURL+ url, auth);
    }

    private static Map<String, String> sendGetResourceServer(String token, String url) throws IOException {
        return sendGet(token, ResourceServerURL + url);
    }

    private static Map<String, String> sendGet(String token, String url) throws IOException {
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

    private static Map<String, String> sendPatch(Map<String, String> body, String token, String url, Auth auth) throws IOException {
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

    private static Map<String, String> sendPost(Map<String, String> body, String token, String url) throws IOException {
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
            throw new RuntimeException();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String responseBody = bufferedReader.readLine();
        bufferedReader.close();
        return mapper.readValue(responseBody, Map.class);
    }


}