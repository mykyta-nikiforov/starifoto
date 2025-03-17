package ua.in.photomap.user.security;

import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;
import ua.in.photomap.common.rest.toolkit.model.TokenBasedAuthentication;
import ua.in.photomap.user.model.User;

public class UserResolver {

    /**
     * Get the current user based on Spring Security context.
     *
     * @return
     */
    @NonNull
    public static User getCurrentUser() {
        TokenBasedAuthentication authentication = (TokenBasedAuthentication) SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null) {
            throw new ForbiddenException("No authentication in context");
        }
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        if (details == null) {
            throw new ForbiddenException("No user details in context");
        }
        return details.getUser();
    }
}
