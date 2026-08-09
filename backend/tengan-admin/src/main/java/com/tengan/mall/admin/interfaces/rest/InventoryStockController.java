package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.AdjustStockPayload;
import com.tengan.mall.admin.application.port.CreateStockPayload;
import com.tengan.mall.admin.application.port.InventoryStockPort;
import com.tengan.mall.admin.application.port.ProductSkuPort;
import com.tengan.mall.admin.application.port.SkuItem;
import com.tengan.mall.admin.application.port.SkuStockItem;
import com.tengan.mall.admin.interfaces.rest.dto.AdjustStockRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateStockRequest;
import com.tengan.mall.admin.interfaces.rest.dto.ListSkuStockResponse;
import com.tengan.mall.admin.interfaces.rest.dto.SkuStockItemResponse;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-inventory 的 /internal/inventory/skus，跟 {@link ProductBrandController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/inventory/skus")
public class InventoryStockController {

    private final InventoryStockPort inventoryStockPort;
    private final ProductSkuPort productSkuPort;

    public InventoryStockController(InventoryStockPort inventoryStockPort, ProductSkuPort productSkuPort) {
        this.inventoryStockPort = inventoryStockPort;
        this.productSkuPort = productSkuPort;
    }

    /**
     * tengan-inventory 完全不知道商品名稱/圖片，這裡批次查 tengan-product 補上顯示用欄位——
     * 查不到（例如商品已被刪除）就讓對應欄位維持 null，前端顯示「—」，不當成錯誤處理。
     */
    @GetMapping
    @PreAuthorize("hasAuthority('inventory:stock:read')")
    public ListSkuStockResponse list(@RequestParam(required = false) Long wareId,
            @RequestParam(required = false) Long keyword, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = inventoryStockPort.listSkus(wareId, keyword, page, pageSize);
        var skuIds = result.items().stream().map(SkuStockItem::skuId).distinct().toList();
        Map<Long, SkuItem> skuById = productSkuPort.batchGet(skuIds).stream()
                .collect(Collectors.toMap(SkuItem::id, Function.identity()));
        var items = result.items().stream()
                .map(i -> {
                    SkuItem sku = skuById.get(i.skuId());
                    return new SkuStockItemResponse(i.wareId(), i.skuId(), i.stock(), i.lockedStock(),
                            sku == null ? null : sku.spuId(), sku == null ? null : sku.name(),
                            sku == null ? null : sku.mainImage());
                })
                .toList();
        return new ListSkuStockResponse(items, result.total());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inventory:stock:write')")
    public ResponseEntity<Void> create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateStockRequest request) {
        inventoryStockPort.createStock(new CreateStockPayload(request.skuId(), request.wareId(),
                request.initialStock()), operatorJwt.getTokenValue());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{skuId}")
    @PreAuthorize("hasAuthority('inventory:stock:write')")
    public void adjust(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long skuId,
            @Valid @RequestBody AdjustStockRequest request) {
        inventoryStockPort.adjustStock(skuId,
                new AdjustStockPayload(request.wareId(), request.delta(), request.reason()),
                operatorJwt.getTokenValue());
    }
}
