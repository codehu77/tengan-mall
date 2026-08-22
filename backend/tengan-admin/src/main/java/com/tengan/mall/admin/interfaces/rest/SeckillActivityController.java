package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateSeckillActivityPayload;
import com.tengan.mall.admin.application.port.ReplaceProductSkusPayload;
import com.tengan.mall.admin.application.port.SeckillActivityPort;
import com.tengan.mall.admin.application.port.SeckillSkuItemPayload;
import com.tengan.mall.admin.application.port.UpdateSeckillActivitySkusPayload;
import com.tengan.mall.admin.application.seckill.GetSeckillActivitySpuSkusUseCase;
import com.tengan.mall.admin.application.seckill.SuggestSeckillSpuSkusUseCase;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSeckillActivityRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateSeckillActivityResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ReplaceProductSkusRequest;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillActivityDetailResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillActivityListResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillActivityResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillActivitySpuSkusListResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillActivitySpuSkusResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillSkuResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillSpuSkuSuggestionListResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SeckillSpuSkuSuggestionResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateSeckillActivitySkusRequest;
import com.tengan.mall.admin.interfaces.rest.dto.WarmUpNowResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * BFF：轉發到 tengan-seckill 的 /internal/seckill/activities(+/skus)，跟 {@link CouponTemplateController}
 * 同樣的純代理原則。配額保留/釋放/批次查詢那幾支給 tengan-order 呼叫的端點，後台不需要，不轉發。
 */
@RestController
@RequestMapping("/api/admin/seckill")
public class SeckillActivityController {

    private final SeckillActivityPort seckillActivityPort;
    private final SuggestSeckillSpuSkusUseCase suggestSeckillSpuSkusUseCase;
    private final GetSeckillActivitySpuSkusUseCase getSeckillActivitySpuSkusUseCase;

    public SeckillActivityController(SeckillActivityPort seckillActivityPort,
            SuggestSeckillSpuSkusUseCase suggestSeckillSpuSkusUseCase,
            GetSeckillActivitySpuSkusUseCase getSeckillActivitySpuSkusUseCase) {
        this.seckillActivityPort = seckillActivityPort;
        this.suggestSeckillSpuSkusUseCase = suggestSeckillSpuSkusUseCase;
        this.getSeckillActivitySpuSkusUseCase = getSeckillActivitySpuSkusUseCase;
    }

    @GetMapping("/activities")
    @PreAuthorize("hasAuthority('seckill:activity:read')")
    public SeckillActivityListResponse listActivities() {
        var items = seckillActivityPort.listActivities().stream()
                .map(a -> new SeckillActivityResponse(a.id(), a.activityType(), a.startTime(), a.endTime(),
                        a.sessionId(), a.activityDate(), a.sessionName(), a.status()))
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
                detail.endTime(), detail.sessionId(), detail.activityDate(), detail.sessionName(), detail.status(),
                skus);
    }

    @PostMapping("/activities")
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public CreateSeckillActivityResponse createActivity(@Valid @RequestBody CreateSeckillActivityRequest request) {
        Long id = seckillActivityPort.createActivity(new CreateSeckillActivityPayload(request.activityType(),
                request.sessionId(), request.activityDate(), request.startTime(), request.endTime()));
        return new CreateSeckillActivityResponse(id);
    }

    @DeleteMapping("/activities/{id}")
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public void deleteActivity(@PathVariable Long id) {
        seckillActivityPort.deleteActivity(id);
    }

    @PutMapping("/activities/{id}/skus")
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public void updateSkus(@PathVariable Long id, @Valid @RequestBody UpdateSeckillActivitySkusRequest request) {
        var items = request.items().stream()
                .map(i -> new SeckillSkuItemPayload(i.skuId(), i.seckillPrice(), i.seckillCount(), i.limitPerUser()))
                .toList();
        seckillActivityPort.updateActivitySkus(id, new UpdateSeckillActivitySkusPayload(items));
    }

    /** 「設定活動商品」列表頁的新增/編輯/刪除單一商品用，只覆蓋這個商品範圍，其餘商品不受影響
     * （見 tengan-seckill ReplaceProductSkusService 說明）；items 可以是空清單代表整個商品從活動移除。 */
    @PutMapping("/activities/{id}/products/{spuId}/skus")
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public void replaceProductSkus(@PathVariable Long id, @PathVariable Long spuId,
            @Valid @RequestBody ReplaceProductSkusRequest request) {
        var items = request.items().stream()
                .map(i -> new SeckillSkuItemPayload(i.skuId(), i.seckillPrice(), i.seckillCount(), i.limitPerUser()))
                .toList();
        seckillActivityPort.replaceProductSkus(id, spuId, new ReplaceProductSkusPayload(request.skuIds(), items));
    }

    @PostMapping("/warmup-now")
    @PreAuthorize("hasAuthority('seckill:activity:warmup')")
    public WarmUpNowResponse warmUpNow() {
        return new WarmUpNowResponse(seckillActivityPort.triggerWarmUpNow());
    }

    /** 選 SPU 建活動時，依真實庫存比例算各規格的建議配額（見 SuggestSeckillSpuSkusService 說明）。 */
    @GetMapping("/spu-skus")
    @PreAuthorize("hasAuthority('seckill:activity:write')")
    public SeckillSpuSkuSuggestionListResponse suggestSpuSkus(@RequestParam Long spuId,
            @RequestParam(defaultValue = "0") int totalQuota) {
        var items = suggestSeckillSpuSkusUseCase.suggest(spuId, totalQuota).stream()
                .map(s -> new SeckillSpuSkuSuggestionResponse(s.skuId(), s.variantLabel(), s.mainImage(),
                        s.realStock(), s.suggestedQuota()))
                .toList();
        return new SeckillSpuSkuSuggestionListResponse(items);
    }

    /** 重新編輯既有活動的商品時回查目前的設定（見 GetSeckillActivitySpuSkusService 說明）；一場活動可以綁多個商品，
     * 空清單代表這場活動還沒設定過商品。 */
    @GetMapping("/activities/{id}/spu-skus")
    @PreAuthorize("hasAuthority('seckill:activity:read')")
    public SeckillActivitySpuSkusListResponse getActivitySpuSkus(@PathVariable Long id) {
        var results = getSeckillActivitySpuSkusUseCase.get(id).stream()
                .map(result -> {
                    var items = result.items().stream()
                            .map(s -> new SeckillSpuSkuSuggestionResponse(s.skuId(), s.variantLabel(), s.mainImage(),
                                    s.realStock(), s.suggestedQuota()))
                            .toList();
                    return new SeckillActivitySpuSkusResponse(result.spuId(), result.spuName(),
                            result.spuMainImage(), result.seckillPrice(), result.limitPerUser(), items);
                })
                .toList();
        return new SeckillActivitySpuSkusListResponse(results);
    }
}
