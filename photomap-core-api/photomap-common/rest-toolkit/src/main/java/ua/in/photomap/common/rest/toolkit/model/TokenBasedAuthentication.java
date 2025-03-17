package ua.in.photomap.common.rest.toolkit.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
public class TokenBasedAuthentication extends AbstractAuthenticationToken {

    private final String token;
    private final UserDetails principal;

    public TokenBasedAuthentication(UserDetails principal, String token) {
        super(principal.getAuthorities());
        this.principal = principal;
        this.token = token;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public UserDetails getPrincipal() {
        return principal;
    }
}