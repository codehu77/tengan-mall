package com.tengan.mall.inventory.interfaces.rest;

import com.tengan.mall.inventory.application.purchaseorder.CreatePurchaseOrderCommand;
import com.tengan.mall.inventory.application.purchaseorder.CreatePurchaseOrderItem;
import com.tengan.mall.inventory.application.purchaseorder.CreatePurchaseOrderUseCase;
import com.tengan.mall.inventory.application.purchaseorder.GetPurchaseOrderDetailUseCase;
import com.tengan.mall.inventory.application.purchaseorder.ListPurchaseOrdersUseCase;
import com.tengan.mall.inventory.application.purchaseorder.ReceivePurchaseOrderCommand;
import com.tengan.mall.inventory.application.purchaseorder.ReceivePurchaseOrderItem;
import com.tengan.mall.inventory.application.purchaseorder.ReceivePurchaseOrderUseCase;
import com.tengan.mall.inventory.interfaces.rest.dto.CreatePurchaseOrderRequest;
import com.tengan.mall.inventory.interfaces.rest.dto.CreatePurchaseOrderResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.ListPurchaseOrdersResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.PurchaseOrderDetailResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.PurchaseOrderItemResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.PurchaseOrderSummaryResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.ReceivePurchaseOrderRequest;
import com.tengan.mall.jwt.IdentityAssertionVerifier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/inventory/purchase-orders")
public class InternalPurchaseOrderController {

    private final CreatePurchaseOrderUseCase createPurchaseOrderUseCase;
    private final ListPurchaseOrdersUseCase listPurchaseOrdersUseCase;
    private final GetPurchaseOrderDetailUseCase getPurchaseOrderDetailUseCase;
    private final ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalPurchaseOrderController(CreatePurchaseOrderUseCase createPurchaseOrderUseCase,
            ListPurchaseOrdersUseCase listPurchaseOrdersUseCase,
            GetPurchaseOrderDetailUseCase getPurchaseOrderDetailUseCase,
            ReceivePurchaseOrderUseCase receivePurchaseOrderUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.createPurchaseOrderUseCase = createPurchaseOrderUseCase;
        this.listPurchaseOrdersUseCase = listPurchaseOrdersUseCase;
        this.getPurchaseOrderDetailUseCase = getPurchaseOrderDetailUseCase;
        this.receivePurchaseOrderUseCase = receivePurchaseOrderUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public ResponseEntity<CreatePurchaseOrderResponse> create(
            @RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody CreatePurchaseOrderRequest request) {
        var items = request.items().stream()
                .map(i -> new CreatePurchaseOrderItem(i.skuId(), i.orderedQty())).toList();
        Long id = createPurchaseOrderUseCase.create(new CreatePurchaseOrderCommand(operator(identityAssertion),
                request.wareId(), request.supplierName(), items));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreatePurchaseOrderResponse(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_inventory.read')")
    public ListPurchaseOrdersResponse list(@RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long wareId, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = listPurchaseOrdersUseCase.list(status, wareId, page, pageSize);
        var items = result.items().stream()
                .map(po -> new PurchaseOrderSummaryResponse(po.id(), po.poNumber(), po.wareId(), po.supplierName(),
                        po.status(), po.createdAt().toString(),
                        po.receivedAt() == null ? null : po.receivedAt().toString()))
                .toList();
        return new ListPurchaseOrdersResponse(items, result.total());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_inventory.read')")
    public PurchaseOrderDetailResponse get(@PathVariable Long id) {
        var result = getPurchaseOrderDetailUseCase.get(id);
        var items = result.items().stream()
                .map(i -> new PurchaseOrderItemResponse(i.id(), i.skuId(), i.orderedQty(), i.receivedQty()))
                .toList();
        return new PurchaseOrderDetailResponse(result.id(), result.poNumber(), result.wareId(),
                result.supplierName(), result.status(), result.createdBy(), result.createdAt().toString(),
                result.receivedAt() == null ? null : result.receivedAt().toString(), items);
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public ResponseEntity<Void> receive(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id, @Valid @RequestBody ReceivePurchaseOrderRequest request) {
        var items = request.items().stream()
                .map(i -> new ReceivePurchaseOrderItem(i.itemId(), i.receivedQty())).toList();
        receivePurchaseOrderUseCase.receive(new ReceivePurchaseOrderCommand(operator(identityAssertion), id, items));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
