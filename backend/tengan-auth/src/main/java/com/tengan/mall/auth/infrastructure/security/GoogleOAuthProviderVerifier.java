package com.tengan.mall.auth.infrastructure.security;

import com.tengan.mall.auth.application.port.OAuthProviderVerifier;
import com.tengan.mall.auth.application.port.OAuthUserInfo;
import com.tengan.mall.auth.domain.exception.InvalidOAuthTokenException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class GoogleOAuthProviderVerifier implements OAuthProviderVerifier {

    private final JwtDecoder googleJwtDecoder;

    public GoogleOAuthProviderVerifier(JwtDecoder googleJwtDecoder) {
        this.googleJwtDecoder = googleJwtDecoder;
    }

    @Override
    public OAuthUserInfo verify(String token) {
        Jwt jwt;
        try {
            jwt = googleJwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new InvalidOAuthTokenException(e.getMessage());
        }

        String sub = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        Boolean emailVerified = jwt.getClaim("email_verified");
        String name = jwt.getClaimAsString("name");
        String picture = jwt.getClaimAsString("picture");

        return new OAuthUserInfo(sub, email, Boolean.TRUE.equals(emailVerified), name, picture);
    }
}
