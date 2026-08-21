package com.tengan.mall.seckill.interfaces.rest;

import com.tengan.mall.seckill.application.activity.CreateActivityCommand;
import com.tengan.mall.seckill.application.activity.CreateActivityUseCase;
import com.tengan.mall.seckill.application.activity.GetActivityUseCase;
import com.tengan.mall.seckill.application.activity.ListActivitiesUseCase;
import com.tengan.mall.seckill.application.activity.SkuItem;
import com.tengan.mall.seckill.application.activity.UpdateActivitySkusCommand;
import com.tengan.mall.seckill.application.activity.UpdateActivitySkusUseCase;
import com.tengan.mall.seckill.application.reservation.CheckActiveSkusUseCase;
import com.tengan.mall.seckill.application.reservation.ReleaseQuotaCommand;
import com.tengan.mall.seckill.application.reservation.ReleaseQuotaUseCase;
import com.tengan.mall.seckill.application.reservation.ReserveQuotaCommand;
import com.tengan.mall.seckill.application.reservation.ReserveQuotaUseCase;
import com.tengan.mall.seckill.domain.model.ActivityType;
import com.tengan.mall.seckill.interfaces.rest.dto.ActiveSkuResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.ActivityDetailResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.ActivityListResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.ActivityResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.BatchStatusRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.BatchStatusResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.CreateActivityRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.CreateActivityResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.ReleaseRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.ReserveRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.ReserveResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.SkuResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.UpdateActivitySkusRequest;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/seckill")
public class InternalSeckillController {

    private final CreateActivityUseCase createActivityUseCase;
    private final UpdateActivitySkusUseCase updateActivitySkusUseCase;
    private final ListActivitiesUseCase listActivitiesUseCase;
    private final GetActivityUseCase getActivityUseCase;
    private final ReserveQuotaUseCase reserveQuotaUseCase;
    private final ReleaseQuotaUseCase releaseQuotaUseCase;
    private final CheckActiveSkusUseCase checkActiveSkusUseCase;

    public InternalSeckillController(CreateActivityUseCase createActivityUseCase,
            UpdateActivitySkusUseCase updateActivitySkusUseCase, ListActivitiesUseCase listActivitiesUseCase,
            GetActivityUseCase getActivityUseCase, ReserveQuotaUseCase reserveQuotaUseCase,
            ReleaseQuotaUseCase releaseQuotaUseCase, CheckActiveSkusUseCase checkActiveSkusUseCase) {
        this.createActivityUseCase = createActivityUseCase;
        this.updateActivitySkusUseCase = updateActivitySkusUseCase;
        this.listActivitiesUseCase = listActivitiesUseCase;
        this.getActivityUseCase = getActivityUseCase;
        this.reserveQuotaUseCase = reserveQuotaUseCase;
        this.releaseQuotaUseCase = releaseQuotaUseCase;
        this.checkActiveSkusUseCase = checkActiveSkusUseCase;
    }

    /** 供 tengan-admin 呼叫。 */
    @GetMapping("/activities")
    @PreAuthorize("hasAuthority('SCOPE_seckill.read')")
    public ActivityListResponse listActivities(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = listActivitiesUseCase.list(page, pageSize);
        var items = result.items().stream()
                .map(a -> new ActivityResponse(a.id(), a.activityType().name(), a.startTime(), a.endTime(),
                        a.status().name()))
                .toList();
        return new ActivityListResponse(items, result.total());
    }

    /** 供 tengan-admin 呼叫（編輯前先取得目前商品清單）。 */
    @GetMapping("/activities/{id}")
    @PreAuthorize("hasAuthority('SCOPE_seckill.read')")
    public ActivityDetailResponse getActivity(@PathVariable Long id) {
        var detail = getActivityUseCase.get(id);
        var skus = detail.skus().stream()
                .map(s -> new SkuResponse(s.id(), s.skuId(), s.seckillPrice(), s.seckillCount(), s.limitPerUser(),
                        s.soldCount(), s.settledAt()))
                .toList();
        return new ActivityDetailResponse(detail.id(), detail.activityType().name(), detail.startTime(),
                detail.endTime(), detail.status().name(), skus);
    }

    /** 供 tengan-admin 呼叫。 */
    @PostMapping("/activities")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public CreateActivityResponse createActivity(@Valid @RequestBody CreateActivityRequest request) {
        ActivityType activityType = ActivityType.valueOf(request.activityType().toUpperCase(Locale.ROOT));
        Long id = createActivityUseCase
                .create(new CreateActivityCommand(activityType, request.startTime(), request.endTime()));
        return new CreateActivityResponse(id);
    }

    /** 供 tengan-admin 呼叫；同時是 DRAFT 活動唯一的「完成編輯」動作（見 UpdateActivitySkusService 說明）。 */
    @PutMapping("/activities/{id}/skus")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public ResponseEntity<Void> updateSkus(@PathVariable Long id,
            @Valid @RequestBody UpdateActivitySkusRequest request) {
        var items = request.items().stream()
                .map(i -> new SkuItem(i.skuId(), i.seckillPrice(), i.seckillCount(), i.limitPerUser()))
                .toList();
        updateActivitySkusUseCase.update(new UpdateActivitySkusCommand(id, items));
        return ResponseEntity.noContent().build();
    }

    /** 供 tengan-order 的訂單建立 Saga 呼叫（三道防線本體，見規劃第 4.2 節）。 */
    @PostMapping("/reservations")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public ReserveResponse reserve(@Valid @RequestBody ReserveRequest request) {
        var result = reserveQuotaUseCase
                .reserve(new ReserveQuotaCommand(request.skuId(), request.memberId(), request.count()));
        return new ReserveResponse(result.activityId(), result.seckillPrice());
    }

    /** 供 tengan-order 訂單建立 Saga 的補償堆疊呼叫。 */
    @PostMapping("/reservations/release")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public ResponseEntity<Void> release(@Valid @RequestBody ReleaseRequest request) {
        releaseQuotaUseCase
                .release(new ReleaseQuotaCommand(request.skuId(), request.memberId(), request.count()));
        return ResponseEntity.noContent().build();
    }

    /** 供 tengan-order 判斷購物車項目該走秒殺保留還是一般庫存鎖定（見規劃第 4.2 節「判斷路徑」）。 */
    @PostMapping("/skus/batch-status")
    @PreAuthorize("hasAuthority('SCOPE_seckill.read')")
    public BatchStatusResponse batchStatus(@Valid @RequestBody BatchStatusRequest request) {
        var activeSkus = checkActiveSkusUseCase.check(request.skuIds()).stream()
                .map(s -> new ActiveSkuResponse(s.skuId(), s.activityId(), s.seckillPrice(), s.limitPerUser()))
                .toList();
        return new BatchStatusResponse(activeSkus);
    }
}
