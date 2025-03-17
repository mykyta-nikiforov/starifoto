package ua.in.photomap.common.rest.toolkit.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class CookiesUtils {

    public static Optional<String> getCookieByName(HttpServletRequest request, String cookieName) {
        Optional<String> result = Optional.empty();
        if (request.getCookies() != null) {
            String cookieValue = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equalsIgnoreCase(cookieName)
                            && !cookie.getValue().isEmpty())
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
            log.debug("Cookie value: {}", cookieValue);
            result = Optional.ofNullable(cookieValue);
        } else {
            log.debug("No cookies found in the request");
        }
        return result;
    }

    public static void clearCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
