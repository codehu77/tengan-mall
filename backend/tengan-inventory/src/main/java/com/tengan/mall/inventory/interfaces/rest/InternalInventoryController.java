package com.tengan.mall.inventory.interfaces.rest;

import com.tengan.mall.inventory.application.stock.AdjustStockCommand;
import com.tengan.mall.inventory.application.stock.AdjustStockUseCase;
import com.tengan.mall.inventory.application.stock.CreateStockCommand;
import com.tengan.mall.inventory.application.stock.CreateStockUseCase;
import com.tengan.mall.inventory.application.stock.DeductInventoryUseCase;
import com.tengan.mall.inventory.application.stock.ListSkuStockUseCase;
import com.tengan.mall.inventory.application.stock.LockInventoryCommand;
import com.tengan.mall.inventory.application.stock.LockInventoryItem;
import com.tengan.mall.inventory.application.stock.LockInventoryUseCase;
import com.tengan.mall.inventory.application.stock.ReleaseInventoryUseCase;
import com.tengan.mall.inventory.application.stock.SeckillDeductCommand;
import com.tengan.mall.inventory.application.stock.SeckillDeductUseCase;
import com.tengan.mall.inventory.application.task.ListTasksUseCase;
import com.tengan.mall.inventory.application.warehouse.CreateWarehouseCommand;
import com.tengan.mall.inventory.application.warehouse.CreateWarehouseUseCase;
import com.tengan.mall.inventory.application.warehouse.ListWarehousesUseCase;
import com.tengan.mall.inventory.interfaces.rest.dto.AdjustStockRequest;
import com.tengan.mall.inventory.interfaces.rest.dto.CreateStockRequest;
import com.tengan.mall.inventory.interfaces.rest.dto.CreateWarehouseRequest;
import com.tengan.mall.inventory.interfaces.rest.dto.CreateWarehouseResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.ListSkuStockResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.ListTasksResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.ListWarehousesResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.LockInventoryRequest;
import com.tengan.mall.inventory.interfaces.rest.dto.LockInventoryResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.OrderSnRequest;
import com.tengan.mall.inventory.interfaces.rest.dto.SeckillDeductRequest;
import com.tengan.mall.inventory.interfaces.rest.dto.WareOrderTaskResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.WareSkuStockResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.WarehouseResponse;
import com.tengan.mall.jwt.IdentityAssertionVerifier;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/inventory")
public class InternalInventoryController {

    private final LockInventoryUseCase lockInventoryUseCase;
    private final ReleaseInventoryUseCase releaseInventoryUseCase;
    private final DeductInventoryUseCase deductInventoryUseCase;
    private final SeckillDeductUseCase seckillDeductUseCase;
    private final ListSkuStockUseCase listSkuStockUseCase;
    private final CreateStockUseCase createStockUseCase;
    private final AdjustStockUseCase adjustStockUseCase;
    private final ListWarehousesUseCase listWarehousesUseCase;
    private final CreateWarehouseUseCase createWarehouseUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalInventoryController(LockInventoryUseCase lockInventoryUseCase,
            ReleaseInventoryUseCase releaseInventoryUseCase, DeductInventoryUseCase deductInventoryUseCase,
            SeckillDeductUseCase seckillDeductUseCase, ListSkuStockUseCase listSkuStockUseCase,
            CreateStockUseCase createStockUseCase, AdjustStockUseCase adjustStockUseCase,
            ListWarehousesUseCase listWarehousesUseCase, CreateWarehouseUseCase createWarehouseUseCase,
            ListTasksUseCase listTasksUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.lockInventoryUseCase = lockInventoryUseCase;
        this.releaseInventoryUseCase = releaseInventoryUseCase;
        this.deductInventoryUseCase = deductInventoryUseCase;
        this.seckillDeductUseCase = seckillDeductUseCase;
        this.listSkuStockUseCase = listSkuStockUseCase;
        this.createStockUseCase = createStockUseCase;
        this.adjustStockUseCase = adjustStockUseCase;
        this.listWarehousesUseCase = listWarehousesUseCase;
        this.createWarehouseUseCase = createWarehouseUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @PostMapping("/lock")
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public LockInventoryResponse lock(@Valid @RequestBody LockInventoryRequest request) {
        var items = request.items().stream().map(i -> new LockInventoryItem(i.skuId(), i.count())).toList();
        var result = lockInventoryUseCase.lock(new LockInventoryCommand(request.orderSn(), items));
        return new LockInventoryResponse(result.success(), result.shortageSkuIds());
    }

    @PostMapping("/release")
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public ResponseEntity<Void> release(@Valid @RequestBody OrderSnRequest request) {
        releaseInventoryUseCase.release(request.orderSn());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deduct")
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public ResponseEntity<Void> deduct(@Valid @RequestBody OrderSnRequest request) {
        deductInventoryUseCase.deduct(request.orderSn());
        return ResponseEntity.noContent().build();
    }

    /** 獨立 scope（inventory.seckill.write），跟一般下單扣庫存的 inventory.write 區隔（見文件服務間身份驗證章節）。 */
    @PostMapping("/seckill/deduct")
    @PreAuthorize("hasAuthority('SCOPE_inventory.seckill.write')")
    public ResponseEntity<Void> seckillDeduct(@Valid @RequestBody SeckillDeductRequest request) {
        seckillDeductUseCase.deduct(new SeckillDeductCommand(request.skuId(), request.count()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/skus")
    @PreAuthorize("hasAuthority('SCOPE_inventory.read')")
    public ListSkuStockResponse listSkus(@RequestParam(required = false) Long wareId,
            @RequestParam(required = false) Long keyword, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = listSkuStockUseCase.list(wareId, keyword, page, pageSize);
        var items = result.items().stream()
                .map(i -> new WareSkuStockResponse(i.wareId(), i.skuId(), i.stock(), i.lockedStock()))
                .toList();
        return new ListSkuStockResponse(items, result.total());
    }

    /** 全新 SKU 第一次建立庫存列，用絕對值（沒有舊值可覆蓋）；已存在該 (wareId, skuId) 回 409，改用 PUT 調整。 */
    @PostMapping("/skus")
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public ResponseEntity<Void> createStock(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody CreateStockRequest request) {
        createStockUseCase.create(new CreateStockCommand(operator(identityAssertion), request.wareId(),
                request.skuId(), request.initialStock()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 對已存在的庫存列做增減量修正（+補貨/-報損），不是覆蓋絕對值；該列不存在回 404，改用 POST 新增。 */
    @PutMapping("/skus/{skuId}")
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public ResponseEntity<Void> adjustStock(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long skuId, @Valid @RequestBody AdjustStockRequest request) {
        adjustStockUseCase.adjust(new AdjustStockCommand(operator(identityAssertion), request.wareId(), skuId,
                request.delta(), request.reason()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouses")
    @PreAuthorize("hasAuthority('SCOPE_inventory.read')")
    public ListWarehousesResponse listWarehouses() {
        var items = listWarehousesUseCase.list().items().stream()
                .map(w -> new WarehouseResponse(w.id(), w.name(), w.address()))
                .toList();
        return new ListWarehousesResponse(items);
    }

    @PostMapping("/warehouses")
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public ResponseEntity<CreateWarehouseResponse> createWarehouse(
            @RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody CreateWarehouseRequest request) {
        var result = createWarehouseUseCase
                .create(new CreateWarehouseCommand(operator(identityAssertion), request.name(), request.address()));
        return ResponseEntity.ok(new CreateWarehouseResponse(result.id()));
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasAuthority('SCOPE_inventory.read')")
    public ListTasksResponse listTasks(@RequestParam(required = false) Integer status,
            @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int pageSize) {
        var result = listTasksUseCase.list(status, from, to, page, pageSize);
        var items = result.items().stream()
                .map(t -> new WareOrderTaskResponse(t.id(), t.orderSn(), t.status(), t.createdAt().toString()))
                .toList();
        return new ListTasksResponse(items, result.total());
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
