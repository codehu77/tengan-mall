package com.tengan.mall.admin.infrastructure.security;

import com.tengan.mall.admin.application.port.AdminAccessTokenIssuerPort;
import com.tengan.mall.admin.domain.model.AdminUserId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/**
 * claims 放 adminId/username/permissions，TTL 15 分鐘，比照 tengan-auth 的
 * AccessTokenIssuerAdapter，多一個 permissions（登入時算好，避免每個請求都查 DB）。
 */
@Component
public class AdminAccessTokenIssuerAdapter implements AdminAccessTokenIssuerPort {

    private static final long ACCESS_TOKEN_TTL_MINUTES = 15;

    private final JwtEncoder jwtEncoder;

    public AdminAccessTokenIssuerAdapter(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String issue(AdminUserId adminUserId, String username, List<String> permissions) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("tengan-mall-admin")
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_TTL_MINUTES, ChronoUnit.MINUTES))
                .subject(String.valueOf(adminUserId.value()))
                .claim("username", username)
                .claim("permissions", permissions)
                .build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
