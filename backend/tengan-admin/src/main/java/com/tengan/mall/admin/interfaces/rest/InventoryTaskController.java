package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.InventoryTaskPort;
import com.tengan.mall.admin.interfaces.rest.dto.ListTasksResponse;
import com.tengan.mall.admin.interfaces.rest.dto.TaskItemResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-inventory 的 /internal/inventory/tasks，供客服排查卡單問題，唯讀不需要稽核。 */
@RestController
@RequestMapping("/api/admin/inventory/tasks")
public class InventoryTaskController {

    private final InventoryTaskPort inventoryTaskPort;

    public InventoryTaskController(InventoryTaskPort inventoryTaskPort) {
        this.inventoryTaskPort = inventoryTaskPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inventory:task:read')")
    public ListTasksResponse list(@RequestParam(required = false) Integer status,
            @RequestParam(required = false) String from, @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int pageSize) {
        var result = inventoryTaskPort.listTasks(status, from, to, page, pageSize);
        var items = result.items().stream()
                .map(t -> new TaskItemResponse(t.id(), t.orderSn(), t.status(), t.createdAt()))
                .toList();
        return new ListTasksResponse(items, result.total());
    }
}
