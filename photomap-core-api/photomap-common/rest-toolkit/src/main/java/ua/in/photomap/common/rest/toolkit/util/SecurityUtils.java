package ua.in.photomap.common.rest.toolkit.util;

import com.auth0.jwt.interfaces.Claim;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import ua.in.photomap.common.rest.toolkit.model.JwtAuthenticatedUser;
import ua.in.photomap.common.rest.toolkit.model.TokenBasedAuthentication;

import java.util.List;
import java.util.Map;

@UtilityClass
public class SecurityUtils {

    public static Authentication buildAuthentication(Map<String, Claim> claims, String authToken) {
        String email = claims.get("sub").asString();
        Long userId = claims.get("user_id").asLong();
        List<String> privileges = claims.get("privileges").asList(String.class);

        JwtAuthenticatedUser userDetails = new JwtAuthenticatedUser();
        userDetails.setId(userId);
        userDetails.setEmail(email);
        userDetails.setPrivileges(privileges);

        TokenBasedAuthentication result = new TokenBasedAuthentication(userDetails, authToken);
        result.setAuthenticated(true);
        return result;
    }
}
