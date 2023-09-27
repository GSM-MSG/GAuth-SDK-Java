package gauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface GAuth {
    GAuthToken generateToken(String email, String password, String clientId, String clientSecret, String redirectUri);

    GAuthToken generateToken(String code, String clientId, String clientSecret, String redirectUri);

    GAuthCode generateCode(String email, String password);

    GAuthToken refresh(String refreshToken);

    GAuthUserInfo getUserInfo(String accessToken);
}
