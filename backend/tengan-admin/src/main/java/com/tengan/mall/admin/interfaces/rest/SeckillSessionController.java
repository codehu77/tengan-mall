package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.SeckillSessionPayload;
import com.tengan.mall.admin.application.port.SeckillSessionPort;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSeckillSessionResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillSessionListResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillSessionRequest;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillSessionResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-seckill 的 /internal/seckill/sessions，跟 {@link SeckillActivityController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/seckill/sessions")
public class SeckillSessionController {

    private final SeckillSessionPort seckillSessionPort;

    public SeckillSessionController(SeckillSessionPort seckillSessionPort) {
        this.seckillSessionPort = seckillSessionPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('seckill:activity:read')")
    public SeckillSessionListResponse listSessions() {
        var sessions = seckillSessionPort.listSessions().stream()
                .map(s -> new SeckillSessionResponse(s.id(), s.name(), s.timeOfDay(), s.durationMinutes(),
                        s.sortOrder(), s.enabled()))
                .toList();
        return new SeckillSessionListResponse(sessions);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public CreateSeckillSessionResponse createSession(@Valid @RequestBody SeckillSessionRequest request) {
        Long id = seckillSessionPort.createSession(new SeckillSessionPayload(request.name(), request.timeOfDay(),
                request.durationMinutes(), request.sortOrder(), request.enabled()));
        return new CreateSeckillSessionResponse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public void updateSession(@PathVariable Long id, @Valid @RequestBody SeckillSessionRequest request) {
        seckillSessionPort.updateSession(id, new SeckillSessionPayload(request.name(), request.timeOfDay(),
                request.durationMinutes(), request.sortOrder(), request.enabled()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public void deleteSession(@PathVariable Long id) {
        seckillSessionPort.deleteSession(id);
    }
}
