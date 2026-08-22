package com.tengan.mall.seckill.interfaces.rest;

import com.tengan.mall.seckill.application.activity.CreateActivityCommand;
import com.tengan.mall.seckill.application.activity.CreateActivityUseCase;
import com.tengan.mall.seckill.application.activity.DeleteActivityUseCase;
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
import com.tengan.mall.seckill.application.session.CreateSessionCommand;
import com.tengan.mall.seckill.application.session.CreateSessionUseCase;
import com.tengan.mall.seckill.application.session.DeleteSessionUseCase;
import com.tengan.mall.seckill.application.session.ListSessionsUseCase;
import com.tengan.mall.seckill.application.session.UpdateSessionCommand;
import com.tengan.mall.seckill.application.session.UpdateSessionUseCase;
import com.tengan.mall.seckill.application.warmup.WarmUpActivitiesUseCase;
import com.tengan.mall.seckill.domain.model.ActivityType;
import com.tengan.mall.seckill.interfaces.rest.dto.ActiveSkuResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.ActivityDetailResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.ActivityListResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.ActivityResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.BatchStatusRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.BatchStatusResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.CreateActivityRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.CreateActivityResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.CreateSessionResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.ReleaseRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.ReserveRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.ReserveResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.SessionListResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.SessionRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.SessionResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.SkuResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.UpdateActivitySkusRequest;
import com.tengan.mall.seckill.interfaces.rest.dto.WarmUpNowResponse;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/internal/seckill")
public class InternalSeckillController {

    private final CreateActivityUseCase createActivityUseCase;
    private final DeleteActivityUseCase deleteActivityUseCase;
    private final UpdateActivitySkusUseCase updateActivitySkusUseCase;
    private final ListActivitiesUseCase listActivitiesUseCase;
    private final GetActivityUseCase getActivityUseCase;
    private final ReserveQuotaUseCase reserveQuotaUseCase;
    private final ReleaseQuotaUseCase releaseQuotaUseCase;
    private final CheckActiveSkusUseCase checkActiveSkusUseCase;
    private final CreateSessionUseCase createSessionUseCase;
    private final UpdateSessionUseCase updateSessionUseCase;
    private final DeleteSessionUseCase deleteSessionUseCase;
    private final ListSessionsUseCase listSessionsUseCase;
    private final WarmUpActivitiesUseCase warmUpActivitiesUseCase;

    public InternalSeckillController(CreateActivityUseCase createActivityUseCase,
            DeleteActivityUseCase deleteActivityUseCase, UpdateActivitySkusUseCase updateActivitySkusUseCase,
            ListActivitiesUseCase listActivitiesUseCase,
            GetActivityUseCase getActivityUseCase, ReserveQuotaUseCase reserveQuotaUseCase,
            ReleaseQuotaUseCase releaseQuotaUseCase, CheckActiveSkusUseCase checkActiveSkusUseCase,
            CreateSessionUseCase createSessionUseCase, UpdateSessionUseCase updateSessionUseCase,
            DeleteSessionUseCase deleteSessionUseCase, ListSessionsUseCase listSessionsUseCase,
            WarmUpActivitiesUseCase warmUpActivitiesUseCase) {
        this.createActivityUseCase = createActivityUseCase;
        this.deleteActivityUseCase = deleteActivityUseCase;
        this.updateActivitySkusUseCase = updateActivitySkusUseCase;
        this.listActivitiesUseCase = listActivitiesUseCase;
        this.getActivityUseCase = getActivityUseCase;
        this.reserveQuotaUseCase = reserveQuotaUseCase;
        this.releaseQuotaUseCase = releaseQuotaUseCase;
        this.checkActiveSkusUseCase = checkActiveSkusUseCase;
        this.createSessionUseCase = createSessionUseCase;
        this.updateSessionUseCase = updateSessionUseCase;
        this.deleteSessionUseCase = deleteSessionUseCase;
        this.listSessionsUseCase = listSessionsUseCase;
        this.warmUpActivitiesUseCase = warmUpActivitiesUseCase;
    }

    /** 供 tengan-admin 呼叫。 */
    @GetMapping("/activities")
    @PreAuthorize("hasAuthority('SCOPE_seckill.read')")
    public ActivityListResponse listActivities(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = listActivitiesUseCase.list(page, pageSize);
        var items = result.items().stream()
                .map(a -> new ActivityResponse(a.id(), a.activityType().name(), a.startTime(), a.endTime(),
                        a.sessionId(), a.activityDate(), a.sessionName(), a.status().name()))
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
                detail.endTime(), detail.sessionId(), detail.activityDate(), detail.sessionName(),
                detail.status().name(), skus);
    }

    /** 供 tengan-admin 呼叫；FLASH_SALE 帶 sessionId+activityDate，LAUNCH 帶 startTime+endTime（見 CreateActivityRequest）。 */
    @PostMapping("/activities")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public CreateActivityResponse createActivity(@Valid @RequestBody CreateActivityRequest request) {
        ActivityType activityType = ActivityType.valueOf(request.activityType().toUpperCase(Locale.ROOT));
        Long id = createActivityUseCase.create(new CreateActivityCommand(activityType, request.sessionId(),
                request.activityDate(), request.startTime(), request.endTime()));
        return new CreateActivityResponse(id);
    }

    /** 供 tengan-admin 呼叫；不擋任何狀態（見 DeleteActivityService 說明）。 */
    @DeleteMapping("/activities/{id}")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        deleteActivityUseCase.delete(id);
        return ResponseEntity.noContent().build();
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

    /** 場次範本 CRUD，供 tengan-admin 呼叫（見場次機制規劃文件）。 */
    @GetMapping("/sessions")
    @PreAuthorize("hasAuthority('SCOPE_seckill.read')")
    public SessionListResponse listSessions() {
        var sessions = listSessionsUseCase.list().stream()
                .map(s -> new SessionResponse(s.id(), s.name(), s.timeOfDay(), s.durationMinutes(), s.sortOrder(),
                        s.enabled()))
                .toList();
        return new SessionListResponse(sessions);
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public CreateSessionResponse createSession(@Valid @RequestBody SessionRequest request) {
        Long id = createSessionUseCase.create(new CreateSessionCommand(request.name(), request.timeOfDay(),
                request.durationMinutes(), request.sortOrder(), request.enabled()));
        return new CreateSessionResponse(id);
    }

    @PutMapping("/sessions/{id}")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public ResponseEntity<Void> updateSession(@PathVariable Long id, @Valid @RequestBody SessionRequest request) {
        updateSessionUseCase.update(new UpdateSessionCommand(id, request.name(), request.timeOfDay(),
                request.durationMinutes(), request.sortOrder(), request.enabled()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sessions/{id}")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        deleteSessionUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** 供 tengan-admin BFF「立即預熱」按鈕呼叫——不用等 {@code WarmUpScheduler} 固定的每日四個時間點，
     * demo/測試時新建的場次不用乾等到下一個排程時間點才會從 PUBLISHED 轉 ACTIVE。 */
    @PostMapping("/warmup-now")
    @PreAuthorize("hasAuthority('SCOPE_seckill.write')")
    public WarmUpNowResponse warmUpNow() {
        int count = warmUpActivitiesUseCase.warmUp();
        return new WarmUpNowResponse(count);
    }
}
