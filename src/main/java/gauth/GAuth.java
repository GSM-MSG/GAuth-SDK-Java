package gauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface GAuth {
    GAuthToken generateToken(String email, String password, String clientId, String clientSecret, String redirectUri) throws IOException;

    GAuthToken generateToken(String code, String clientId, String clientSecret, String redirectUri) throws IOException;

    GAuthCode generateCode(String email, String password) throws IOException;

    GAuthToken refresh(String refreshToken) throws IOException;

    GAuthUserInfo getUserInfo(String accessToken) throws IOException;
}
