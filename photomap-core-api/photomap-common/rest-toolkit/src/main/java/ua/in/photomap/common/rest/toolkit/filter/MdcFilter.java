package ua.in.photomap.common.rest.toolkit.filter;

import com.google.common.base.Stopwatch;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import ua.in.photomap.common.rest.toolkit.util.HttpServletRequestUtils;
import ua.in.photomap.common.rest.toolkit.util.JwtService;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class MdcFilter implements Filter {

    private final JwtService jwtService;

    private static final Integer RANDOM_LEFT_LIMIT = 48; // numeral '0'
    private static final Integer RANDOM_RIGHT_LIMIT = 122; // letter 'z'
    private static final Integer RANDOM_TARGET_STRING_LENGTH = 10;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        MDC.put("requestId", generateRandomString());
        MDC.put("httpMethod", httpServletRequest.getMethod());
        MDC.put("uri", httpServletRequest.getRequestURI());
        try {
            putUserEmailIntoMdc(request);
        } catch (Exception e) {
            log.error("Request " + httpServletRequest.getMethod() + ":" + httpServletRequest.getRequestURI(), e);
        }
        final Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("Request-processing-started");
        chain.doFilter(request, response);
        stopwatch.stop();
        log.info("Request-processing-finished in : " + stopwatch.elapsed().toString());
    }

    private void putUserEmailIntoMdc(ServletRequest request) {
        if (request instanceof HttpServletRequest httpServletRequest) {
            Optional<String> authToken = HttpServletRequestUtils.getAuthToken(httpServletRequest);
            if (authToken.isPresent()) {
                String email = jwtService.getClaims(authToken.get()).get("sub").asString();
                MDC.put("userEmail", email);
            } else
                MDC.put("userEmail", "emptyAuth");
        }
    }

    private String generateRandomString() {
        return new Random()
                .ints(RANDOM_LEFT_LIMIT, RANDOM_RIGHT_LIMIT + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))  // include only 0-9, A-Z, a-z
                .limit(RANDOM_TARGET_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}