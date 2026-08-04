package com.tengan.mall.admin.infrastructure.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

/**
 * 把 JWT 的 permissions claim（colon 式 RBAC 權限碼，例如 system:user:write）轉成
 * GrantedAuthority，純字串不加 SCOPE_/ROLE_ 前綴——這樣 {@code @PreAuthorize("hasAuthority(...)")}
 * 才能直接比對，跟 OAuth2 scope 的 dot 式命名是兩套系統（見 docs/JWT設計.md 第四節）。
 */
@Component
public class AdminJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtAuthenticationConverter delegate;

    public AdminJwtAuthenticationConverter() {
        this.delegate = new JwtAuthenticationConverter();
        this.delegate.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        return delegate.convert(source);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> permissions = jwt.getClaimAsStringList("permissions");
        if (permissions == null) {
            return List.of();
        }
        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
