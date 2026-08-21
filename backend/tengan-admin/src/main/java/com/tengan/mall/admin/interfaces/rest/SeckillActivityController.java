package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateSeckillActivityPayload;
import com.tengan.mall.admin.application.port.SeckillActivityPort;
import com.tengan.mall.admin.application.port.SeckillSkuItemPayload;
import com.tengan.mall.admin.application.port.UpdateSeckillActivitySkusPayload;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSeckillActivityRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSeckillActivityResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillActivityDetailResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillActivityListResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillActivityResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillSkuResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateSeckillActivitySkusRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BFF：轉發到 tengan-seckill 的 /internal/seckill/activities(+/skus)，跟 {@link CouponTemplateController}
 * 同樣的純代理原則。配額保留/釋放/批次查詢那幾支給 tengan-order 呼叫的端點，後台不需要，不轉發。
 */
@RestController
@RequestMapping("/api/admin/seckill")
public class SeckillActivityController {

    private final SeckillActivityPort seckillActivityPort;

    public SeckillActivityController(SeckillActivityPort seckillActivityPort) {
        this.seckillActivityPort = seckillActivityPort;
    }

    @GetMapping("/activities")
    @PreAuthorize("hasAuthority('seckill:activity:read')")
    public SeckillActivityListResponse listActivities() {
        var items = seckillActivityPort.listActivities().stream()
                .map(a -> new SeckillActivityResponse(a.id(), a.activityType(), a.startTime(), a.endTime(),
                        a.status()))
                .toList();
        return new SeckillActivityListResponse(items, items.size());
    }

    @GetMapping("/activities/{id}")
    @PreAuthorize("hasAuthority('seckill:activity:read')")
    public SeckillActivityDetailResponse getActivity(@PathVariable Long id) {
        var detail = seckillActivityPort.getActivity(id);
        var skus = detail.skus().stream()
                .map(s -> new SeckillSkuResponse(s.id(), s.skuId(), s.seckillPrice(), s.seckillCount(),
                        s.limitPerUser(), s.soldCount(), s.settledAt()))
                .toList();
        return new SeckillActivityDetailResponse(detail.id(), detail.activityType(), detail.startTime(),
                detail.endTime(), detail.status(), skus);
    }

    @PostMapping("/activities")
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public CreateSeckillActivityResponse createActivity(@Valid @RequestBody CreateSeckillActivityRequest request) {
        Long id = seckillActivityPort.createActivity(
                new CreateSeckillActivityPayload(request.activityType(), request.startTime(), request.endTime()));
        return new CreateSeckillActivityResponse(id);
    }

    @PutMapping("/activities/{id}/skus")
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public void updateSkus(@PathVariable Long id, @Valid @RequestBody UpdateSeckillActivitySkusRequest request) {
        var items = request.items().stream()
                .map(i -> new SeckillSkuItemPayload(i.skuId(), i.seckillPrice(), i.seckillCount(), i.limitPerUser()))
                .toList();
        seckillActivityPort.updateActivitySkus(id, new UpdateSeckillActivitySkusPayload(items));
    }
}
