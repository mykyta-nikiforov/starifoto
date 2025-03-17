package ua.in.photomap.user.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.in.photomap.user.service.CookiesService;

import java.util.concurrent.TimeUnit;

@Component
public class CookiesServiceImpl implements CookiesService {

    @Value("${photomap.security.jwt.expirationDateInMs}")
    private int jwtExpirationInMs;

    @Value("${photomap.security.jwt.refreshExpirationDateInMs}")
    private int jwtRefreshExpirationInMs;

    public void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        var accessTokenCookie = buildCookie("accessToken", accessToken,
                Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(jwtExpirationInMs)).intValue());
        var refreshTokenCookie = buildCookie("refreshToken", refreshToken,
                Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(jwtRefreshExpirationInMs)).intValue());
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    private Cookie buildCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        response.addCookie(buildCookie("refreshToken", null, 0));
        response.addCookie(buildCookie("accessToken", null, 0));
    }
}
