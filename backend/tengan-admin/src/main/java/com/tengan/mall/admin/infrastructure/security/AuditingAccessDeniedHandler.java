package com.tengan.mall.admin.infrastructure.security;

import com.tengan.mall.admin.domain.model.OperLog;
import com.tengan.mall.admin.domain.repository.OperLogRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * {@code @PreAuthorize} 403 時 best-effort 補一筆 result_status=0 的 oper_log
 * （ddd-standards.md 第四節「授權判斷不論放行或拒絕都要記 log」）。拿不到足夠資訊就跳過，
 * 稽核寫入失敗不能擋住 403 回應本身。
 */
@Component
public class AuditingAccessDeniedHandler implements AccessDeniedHandler {

    private final OperLogRepository operLogRepository;

    public AuditingAccessDeniedHandler(OperLogRepository operLogRepository) {
        this.operLogRepository = operLogRepository;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException, ServletException {
        tryAudit(request);
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }

    private void tryAudit(HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                Long adminId = Long.valueOf(jwt.getSubject());
                String username = jwt.getClaimAsString("username");
                operLogRepository.save(OperLog.create(adminId, username, "access_control", "denied",
                        request.getMethod() + " " + request.getRequestURI(), false));
            }
        } catch (RuntimeException ignored) {
            // best-effort：稽核寫入失敗不能擋住 403 回應本身
        }
    }
}
