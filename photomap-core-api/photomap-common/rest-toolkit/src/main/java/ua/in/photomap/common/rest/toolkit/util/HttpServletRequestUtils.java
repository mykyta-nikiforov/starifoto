package ua.in.photomap.common.rest.toolkit.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@UtilityClass
public final class HttpServletRequestUtils {

    public static final String AUTH_HEADER_PREFIX = "Bearer ";

    public static Optional<String> getAuthToken(HttpServletRequest request) {
        Optional<String> result = getAuthHeader(request);
        if (result.isEmpty()) {
            result = CookiesUtils.getCookieByName(request, "accessToken");
        }
        return result;
    }

    private static Optional<String> getAuthHeader(HttpServletRequest request) {
        Optional<String> result = Optional.empty();
        String authHeader = request.getHeader("Authorization");
        log.debug("Auth header: {}", authHeader);
        if (authHeader != null && authHeader.startsWith(AUTH_HEADER_PREFIX)) {
            result = Optional.of(authHeader.substring(AUTH_HEADER_PREFIX.length()));
        }
        return result;
    }

}
