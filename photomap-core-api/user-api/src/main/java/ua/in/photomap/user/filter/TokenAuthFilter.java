package ua.in.photomap.user.filter;

import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ua.in.photomap.common.rest.toolkit.exception.FilterChainException;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;
import ua.in.photomap.common.rest.toolkit.filter.JwtTokenAuthFilter;
import ua.in.photomap.common.rest.toolkit.model.TokenBasedAuthentication;
import ua.in.photomap.common.rest.toolkit.util.CookiesUtils;
import ua.in.photomap.common.rest.toolkit.util.JwtService;
import ua.in.photomap.user.utils.JwtUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class TokenAuthFilter extends JwtTokenAuthFilter {

    private final UserDetailsService userDetailsService;

    public TokenAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        super(jwtService);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requestIsNotLogin(request)) {
            super.doFilterInternal(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected void customValidation(HttpServletRequest request, HttpServletResponse response) throws FilterChainException {
        if (getAuthToken(request).isEmpty() && isRefreshTokenRequest(request.getRequestURL().toString())) {
            throw new FilterChainException("No token found", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    protected void validateClaims(HttpServletRequest request, Map<String, Claim> claims) {
        super.validateClaims(request, claims);
        String scope = claims.get("scope").asString();
        String requestURL = request.getRequestURL().toString();

        if (JwtUtils.SCOPE_REFRESH_TOKEN.equals(scope) && !isRefreshTokenRequest(requestURL)) {
            throw new ForbiddenException("Use access token instead of refresh token");
        }
    }

    private boolean isRefreshTokenRequest(String requestUrl) {
        return requestUrl.contains("/api/auth/refresh");
    }

    @Override
    protected Optional<String> getAuthToken(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        Optional<String> result;
        if (isRefreshTokenRequest(requestURL)) {
            result = CookiesUtils.getCookieByName(request, "refreshToken");
        } else {
            result = super.getAuthToken(request);
        }
        return result;
    }

    @Override
    protected Authentication buildAuthentication(Map<String, Claim> claims, String authToken) {
        String username = claims.get("sub").asString();
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new TokenBasedAuthentication(userDetails, authToken);
        } catch (UsernameNotFoundException e) {
            throw new ForbiddenException("User has changed email");
        }
    }

    private boolean requestIsNotLogin(HttpServletRequest request) {
        return !request.getRequestURL().toString().contains("/api/auth/google")
                && !request.getRequestURL().toString().contains("/api/auth/login");
    }
}
