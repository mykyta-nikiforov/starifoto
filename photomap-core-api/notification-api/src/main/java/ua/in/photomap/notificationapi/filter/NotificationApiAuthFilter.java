package ua.in.photomap.notificationapi.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.in.photomap.common.rest.toolkit.filter.JwtTokenAuthFilter;
import ua.in.photomap.common.rest.toolkit.util.JwtService;

import java.io.IOException;

public class NotificationApiAuthFilter extends JwtTokenAuthFilter {
    public NotificationApiAuthFilter(JwtService jwtService) {
        super(jwtService);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURL().toString().contains("/health")) {
            super.doFilterInternal(request, response, filterChain);
        }
    }
}
