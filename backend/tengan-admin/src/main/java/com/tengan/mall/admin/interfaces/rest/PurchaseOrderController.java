package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreatePurchaseOrderItemPayload;
import com.tengan.mall.admin.application.port.CreatePurchaseOrderPayload;
import com.tengan.mall.admin.application.port.PurchaseOrderPort;
import com.tengan.mall.admin.application.port.ReceivePurchaseOrderItemPayload;
import com.tengan.mall.admin.application.port.ReceivePurchaseOrderPayload;
import com.tengan.mall.admin.interfaces.rest.dto.CreatePurchaseOrderRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreatePurchaseOrderResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListPurchaseOrdersResponse;
import com.tengan.mall.admin.interfaces.rest.dto.PurchaseOrderDetailResponse;
import com.tengan.mall.admin.interfaces.rest.dto.PurchaseOrderItemResponse;
import com.tengan.mall.admin.interfaces.rest.dto.PurchaseOrderSummaryResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ReceivePurchaseOrderRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-inventory 的 /internal/inventory/purchase-orders，跟 {@link InventoryStockController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/inventory/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderPort purchaseOrderPort;

    public PurchaseOrderController(PurchaseOrderPort purchaseOrderPort) {
        this.purchaseOrderPort = purchaseOrderPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inventory:purchase:read')")
    public ListPurchaseOrdersResponse list(@RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long wareId, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = purchaseOrderPort.listPurchaseOrders(status, wareId, page, pageSize);
        var items = result.items().stream()
                .map(po -> new PurchaseOrderSummaryResponse(po.id(), po.poNumber(), po.wareId(), po.supplierName(),
                        po.status(), po.createdAt(), po.receivedAt()))
                .toList();
        return new ListPurchaseOrdersResponse(items, result.total());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inventory:purchase:read')")
    public PurchaseOrderDetailResponse get(@PathVariable Long id) {
        var detail = purchaseOrderPort.getPurchaseOrderDetail(id);
        var items = detail.items().stream()
                .map(i -> new PurchaseOrderItemResponse(i.id(), i.skuId(), i.orderedQty(), i.receivedQty()))
                .toList();
        return new PurchaseOrderDetailResponse(detail.id(), detail.poNumber(), detail.wareId(),
                detail.supplierName(), detail.status(), detail.createdBy(), detail.createdAt(),
                detail.receivedAt(), items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inventory:purchase:write')")
    public CreatePurchaseOrderResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreatePurchaseOrderRequest request) {
        var items = request.items().stream()
                .map(i -> new CreatePurchaseOrderItemPayload(i.skuId(), i.orderedQty())).toList();
        Long id = purchaseOrderPort.createPurchaseOrder(
                new CreatePurchaseOrderPayload(request.wareId(), request.supplierName(), items),
                operatorJwt.getTokenValue());
        return new CreatePurchaseOrderResponse(id);
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('inventory:purchase:write')")
    public void receive(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody ReceivePurchaseOrderRequest request) {
        var items = request.items().stream()
                .map(i -> new ReceivePurchaseOrderItemPayload(i.itemId(), i.receivedQty())).toList();
        purchaseOrderPort.receivePurchaseOrder(id, new ReceivePurchaseOrderPayload(items),
                operatorJwt.getTokenValue());
    }
}
