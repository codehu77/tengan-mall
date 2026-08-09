package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CreateWarehousePayload;
import com.tengan.mall.admin.application.port.InventoryWarehousePort;
import com.tengan.mall.admin.interfaces.rest.dto.CreateWarehouseRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateWarehouseResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListWarehousesResponse;
import com.tengan.mall.admin.interfaces.rest.dto.WarehouseItemResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-inventory 的 /internal/inventory/warehouses，跟 {@link ProductBrandController} 同樣的純代理原則。 */
@RestController
@RequestMapping("/api/admin/inventory/warehouses")
public class InventoryWarehouseController {

    private final InventoryWarehousePort inventoryWarehousePort;

    public InventoryWarehouseController(InventoryWarehousePort inventoryWarehousePort) {
        this.inventoryWarehousePort = inventoryWarehousePort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inventory:warehouse:read')")
    public ListWarehousesResponse list() {
        var items = inventoryWarehousePort.listWarehouses().stream()
                .map(w -> new WarehouseItemResponse(w.id(), w.name(), w.address()))
                .toList();
        return new ListWarehousesResponse(items);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inventory:warehouse:write')")
    public CreateWarehouseResponse create(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateWarehouseRequest request) {
        Long id = inventoryWarehousePort.createWarehouse(new CreateWarehousePayload(request.name(), request.address()),
                operatorJwt.getTokenValue());
        return new CreateWarehouseResponse(id);
    }
}
