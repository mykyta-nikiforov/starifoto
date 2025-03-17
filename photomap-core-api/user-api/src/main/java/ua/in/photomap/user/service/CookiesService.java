package ua.in.photomap.user.service;

import jakarta.servlet.http.HttpServletResponse;

public interface CookiesService {

    void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken);

    void removeRefreshTokenCookie(HttpServletResponse response);
}
