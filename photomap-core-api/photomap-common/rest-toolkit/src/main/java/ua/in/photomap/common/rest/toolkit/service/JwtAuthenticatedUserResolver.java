package ua.in.photomap.common.rest.toolkit.service;

import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;
import ua.in.photomap.common.rest.toolkit.model.JwtAuthenticatedUser;
import ua.in.photomap.common.rest.toolkit.model.TokenBasedAuthentication;

public class JwtAuthenticatedUserResolver {

    /**
     * Get the current user based on Spring Security context.
     *
     * @return
     */
    @NonNull
    public static JwtAuthenticatedUser getCurrentUser() {
        TokenBasedAuthentication authentication = (TokenBasedAuthentication) SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null) {
            throw new ForbiddenException("No authentication in context");
        }
        JwtAuthenticatedUser details = (JwtAuthenticatedUser) authentication.getPrincipal();
        if (details == null) {
            throw new ForbiddenException("No user details in context");
        }
        return details;
    }
}