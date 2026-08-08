package com.tengan.mall.cart.interfaces.rest;

import com.tengan.mall.cart.application.cart.GetCheckedItemsForOrderUseCase;
import com.tengan.mall.cart.application.cart.RemoveItemsAfterOrderUseCase;
import com.tengan.mall.cart.interfaces.rest.dto.CheckedItemResponse;
import com.tengan.mall.cart.interfaces.rest.dto.RemoveItemsAfterOrderRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 給 tengan-order 用（Phase 6 才會真的有呼叫端註冊對應 client/scope，這裡先把 scaffold+驗證
 * 建好，比照 tengan-product 當初先做 Category internal 端點的順序——先把驗證機制立起來，
 * 之後每個新服務只要照抄就好，不用等到 order 真的存在才回頭補）。
 */
@RestController
@RequestMapping("/internal/cart")
public class InternalCartController {

    private final GetCheckedItemsForOrderUseCase getCheckedItemsForOrderUseCase;
    private final RemoveItemsAfterOrderUseCase removeItemsAfterOrderUseCase;

    public InternalCartController(GetCheckedItemsForOrderUseCase getCheckedItemsForOrderUseCase,
            RemoveItemsAfterOrderUseCase removeItemsAfterOrderUseCase) {
        this.getCheckedItemsForOrderUseCase = getCheckedItemsForOrderUseCase;
        this.removeItemsAfterOrderUseCase = removeItemsAfterOrderUseCase;
    }

    /** 目前只支援已勾選項目（checkedOnly），沒有「回傳全部」的變體——結帳頁本來就只需要這個。 */
    @GetMapping("/{userId}/items")
    @PreAuthorize("hasAuthority('SCOPE_cart.read')")
    public List<CheckedItemResponse> checkedItems(@PathVariable Long userId) {
        return getCheckedItemsForOrderUseCase.get(userId).stream()
                .map(i -> new CheckedItemResponse(i.skuId(), i.count()))
                .toList();
    }

    @DeleteMapping("/{userId}/items")
    @PreAuthorize("hasAuthority('SCOPE_cart.write')")
    public ResponseEntity<Void> removeAfterOrder(@PathVariable Long userId,
            @RequestBody RemoveItemsAfterOrderRequest request) {
        removeItemsAfterOrderUseCase.remove(userId, request.skuIds());
        return ResponseEntity.noContent().build();
    }
}
