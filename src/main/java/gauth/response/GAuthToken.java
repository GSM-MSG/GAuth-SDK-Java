package gauth.response;

import java.util.Map;

public class GAuthToken {
    private String accessToken;
    private String refreshToken;

    public GAuthToken(Map<String, String> map) {
        this.accessToken = map.get("accessToken");
        this.refreshToken = map.get("refreshToken");
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
