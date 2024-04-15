package gauth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gauth.GAuth;
import gauth.enums.TokenType;
import gauth.response.GAuthCode;
import gauth.response.GAuthToken;
import gauth.response.GAuthUserInfo;
import gauth.exception.GAuthException;
import gauth.exception.InvalidEncodingException;
import gauth.exception.JsonNotParseException;
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
    private final String GAuthServerURL = "https://port-0-gauth-backend-85phb42bluutn9a7.sel5.cloudtype.app/oauth";
    private final String ResourceServerURL = "https://port-0-gauth-resource-server-71t02clq411q18.sel4.cloudtype.app";

    public GAuthToken generateToken(String email, String password, String clientId, String clientSecret, String redirectUri) {
        String code = generateCode(email, password).getCode();
        return new GAuthToken(getToken(code, clientId, clientSecret, redirectUri));
    }

    public GAuthToken generateToken(String code, String clientId, String clientSecret, String redirectUri) {
        return new GAuthToken(getToken(code, clientId, clientSecret, redirectUri));
    }

    public GAuthCode generateCode(String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        String code = sendPostGAuthServer(body, null, "/code").get("code");
        return new GAuthCode(code);
    }

    public GAuthToken refresh(String refreshToken) {
        if(!refreshToken.startsWith("Bearer "))
            refreshToken = "Bearer "+refreshToken;
        return new GAuthToken(sendPatchGAuthServer(null, refreshToken, "/token", TokenType.REFRESH));
    }

    public GAuthUserInfo getUserInfo(String accessToken) {
        if(!accessToken.startsWith("Bearer "))
            accessToken = "Bearer "+accessToken;
        Map<String, Object> map = sendGetResourceServer(accessToken, "/user");
        return new GAuthUserInfo(map);
    }

    private Map<String, String> getToken(String code, String clientId, String clientSecret, String redirectUri) {
        Map<String, String> body = new HashMap<>();
        body.put("code", code);
        body.put("clientId", clientId);
        body.put("clientSecret", clientSecret);
        body.put("redirectUri", redirectUri);
        return sendPostGAuthServer(body, null, "/token");
    }

    private Map<String, String> sendPostGAuthServer(Map<String, String> body, String token, String url) {
        return sendPost(body, token, GAuthServerURL+url);
    }

    private Map<String, String> sendPatchGAuthServer(Map<String, String> body, String token, String url, TokenType tokenType) {
        return sendPatch(body, token, GAuthServerURL+ url, tokenType);
    }

    private Map<String, Object> sendGetResourceServer(String token, String url) {
        return sendGet(token, ResourceServerURL + url);
    }

    private Map<String, Object> sendGet(String token, String url) {
        HttpGet request = new HttpGet(url); //GET 메소드 URL 생성
        request.addHeader("Authorization", token);
        try (
                CloseableHttpClient client = HttpClientBuilder.create().build();
                CloseableHttpResponse response = client.execute(request)
        ) {
            Integer statusCode = response.getStatusLine().getStatusCode();
            if(statusCode !=200)
                throw new GAuthException(statusCode);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"))) {
                String responseBody = bufferedReader.readLine();
                return mapper.readValue(responseBody, Map.class);
            } catch (IOException e) {
                throw new RuntimeException("BufferReader Can't read value", e);
            }
        }
        catch (JsonProcessingException e) {
            throw new JsonNotParseException(e);
        }
        catch (IOException e) {
            throw new RuntimeException("Can't connect GAuth server", e);
        }
    }

    private Map<String, String> sendPatch(Map<String, String> body, String token, String url, TokenType tokenType) {
        HttpPatch request = new HttpPatch(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Content-Type", "application/json");
        if(tokenType == TokenType.ACCESS)
            request.setHeader("Authorization", token);
        else
            request.addHeader("refreshToken", token);
        if(body != null){
            String json = new JSONObject(body).toJSONString();
            try {
                request.setEntity(new StringEntity(json));
            } catch (UnsupportedEncodingException e) {
                throw new InvalidEncodingException(e);
            }
        }
        try (
                CloseableHttpClient client = HttpClientBuilder.create().build();
                CloseableHttpResponse response = client.execute(request)
        ) {
            Integer statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200)
                throw new GAuthException(statusCode);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String responseBody = bufferedReader.readLine();
                bufferedReader.close();
                return mapper.readValue(responseBody, Map.class);
            } catch (IOException e) {
                throw new RuntimeException("BufferReader Can't read value", e);
            }
        }
        catch (JsonProcessingException e) {
            throw new JsonNotParseException(e);
        }
        catch (IOException e) {
            throw new RuntimeException("Can't connect GAuth server", e);
        }
    }

    private Map<String, String> sendPost(Map<String, String> body, String token, String url) {
        HttpPost request = new HttpPost(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Content-Type", "application/json");
        request.addHeader("Authorization", token);
        if (body != null) {
            String json = new JSONObject(body).toJSONString();
            try {
                request.setEntity(new StringEntity(json));
            } catch (UnsupportedEncodingException e) {
                throw new InvalidEncodingException(e);
            }
        }
        try (
            CloseableHttpClient client = HttpClientBuilder.create().build();
            CloseableHttpResponse response = client.execute(request)
        ) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new GAuthException(statusCode);
            }
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"))) {
                String responseBody = bufferedReader.readLine();
                return mapper.readValue(responseBody, Map.class);
            } catch (IOException e) {
                throw new RuntimeException("BufferReader Can't read value", e);
            }
        }
        catch (JsonProcessingException e) {
            throw new JsonNotParseException(e);
        }
        catch (IOException e) {
            throw new RuntimeException("Can't connect GAuth server", e);
        }
    }



}