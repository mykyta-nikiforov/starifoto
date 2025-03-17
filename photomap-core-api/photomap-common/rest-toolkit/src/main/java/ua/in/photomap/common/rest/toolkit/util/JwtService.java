package ua.in.photomap.common.rest.toolkit.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;

import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${photomap.security.secret}")
    private String jwtSecret;

    public Map<String, Claim> getClaims(String authToken) throws ForbiddenException, JWTVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC512(jwtSecret))
                .build()
                .verify(authToken);
        return jwt.getClaims();
    }

    public void validateClaims(Map<String, Claim> claims) {
        String scope = claims.get("scope").asString();
        Date expiresAt = claims.get("exp").asDate();
        String email = claims.get("sub").asString();

        if (ObjectUtils.isEmpty(scope)) {
            throw new ForbiddenException("Token doesn't contain scope");
        } else if (ObjectUtils.isEmpty(email)) {
            throw new ForbiddenException("Token doesn't contain email");
        } else if (expiresAt.before(new Date())) {
            throw new ForbiddenException("Token is expired");
        }
    }
}
