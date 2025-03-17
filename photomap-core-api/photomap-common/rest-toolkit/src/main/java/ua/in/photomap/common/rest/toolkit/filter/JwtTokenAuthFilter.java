package ua.in.photomap.common.rest.toolkit.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.in.photomap.common.rest.toolkit.dto.GenericErrorResponse;
import ua.in.photomap.common.rest.toolkit.exception.FilterChainException;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;
import ua.in.photomap.common.rest.toolkit.util.CookiesUtils;
import ua.in.photomap.common.rest.toolkit.util.HttpServletRequestUtils;
import ua.in.photomap.common.rest.toolkit.util.JwtService;
import ua.in.photomap.common.rest.toolkit.util.SecurityUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthFilter extends OncePerRequestFilter {

    protected final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = null;
        Optional<String> authToken = getAuthToken(request);
        log.debug("Request to {}, has token: {}", request.getRequestURL().toString(), authToken.isPresent());
        if (authToken.isPresent()) {
            String authTokenValue = authToken.get();
            try {
                Map<String, Claim> claims = jwtService.getClaims(authTokenValue);
                validateClaims(request, claims);

                authentication = buildAuthentication(claims, authTokenValue);
            } catch (TokenExpiredException | SignatureVerificationException | ForbiddenException var9) {
                setErrorResponse(response, request, HttpStatus.FORBIDDEN);
                return;
            }
        }
        try {
            customValidation(request, response);
        } catch (FilterChainException e) {
            setErrorResponse(response, request, e.getHttpStatus());
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    protected void customValidation(HttpServletRequest request, HttpServletResponse response) throws FilterChainException {
        // Override this method to add custom logic. Throw FilterChainException to return 403
    }

    protected static void setErrorResponse(HttpServletResponse response, HttpServletRequest request,
                                           HttpStatus httpStatus) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(LocalDateTime.now().toString(), httpStatus.value(),
                httpStatus.getReasonPhrase(), "Failed to authenticate. Token is invalid or expired.",
                request.getRequestURI());
        try {
            String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            CookiesUtils.clearCookie(response, "accessToken");
            CookiesUtils.clearCookie(response, "refreshToken");
        } catch (IOException e) {
            log.error("Failed to serialize error response", e);
        }
        response.setStatus(httpStatus.value());
    }


    protected Optional<String> getAuthToken(HttpServletRequest request) {
        return HttpServletRequestUtils.getAuthToken(request);
    }

    protected void validateClaims(HttpServletRequest request, Map<String, Claim> claims) {
        jwtService.validateClaims(claims);
    }

    protected Authentication buildAuthentication(Map<String, Claim> claims, String authToken) {
        return SecurityUtils.buildAuthentication(claims, authToken);
    }
}
