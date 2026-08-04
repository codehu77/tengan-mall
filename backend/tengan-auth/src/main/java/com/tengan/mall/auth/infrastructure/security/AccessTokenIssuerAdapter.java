package com.tengan.mall.auth.infrastructure.security;

import com.tengan.mall.auth.application.port.AccessTokenIssuerPort;
import com.tengan.mall.auth.domain.model.AccountId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/**
 * claims 只放 userId/username（微服務前台API待開發清單.md 第2節「Access Token + Refresh
 * Token（無黑名單）」），TTL 15 分鐘，Gateway 與下游服務只驗簽章 + exp，不查任何狀態。
 */
@Component
public class AccessTokenIssuerAdapter implements AccessTokenIssuerPort {

    private static final long ACCESS_TOKEN_TTL_MINUTES = 15;

    private final JwtEncoder jwtEncoder;

    public AccessTokenIssuerAdapter(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String issue(AccountId accountId, String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("tengan-mall-auth")
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_TTL_MINUTES, ChronoUnit.MINUTES))
                .subject(String.valueOf(accountId.value()))
                .claim("username", username)
                .build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
