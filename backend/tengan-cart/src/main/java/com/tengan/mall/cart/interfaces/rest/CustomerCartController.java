package com.tengan.mall.cart.interfaces.rest;

import com.tengan.mall.cart.application.cart.AddCartItemCommand;
import com.tengan.mall.cart.application.cart.AddCartItemUseCase;
import com.tengan.mall.cart.application.cart.CartCountUseCase;
import com.tengan.mall.cart.application.cart.CartLineView;
import com.tengan.mall.cart.application.cart.CartOwner;
import com.tengan.mall.cart.application.cart.ListCartUseCase;
import com.tengan.mall.cart.application.cart.MergeCartUseCase;
import com.tengan.mall.cart.application.cart.MiniCartUseCase;
import com.tengan.mall.cart.application.cart.RemoveCartItemUseCase;
import com.tengan.mall.cart.application.cart.RemoveCheckedItemsUseCase;
import com.tengan.mall.cart.application.cart.ToggleCartItemCheckedUseCase;
import com.tengan.mall.cart.application.cart.ToggleCheckedAllUseCase;
import com.tengan.mall.cart.application.cart.UpdateCartItemCountUseCase;
import com.tengan.mall.cart.interfaces.filter.CartIdentityFilter;
import com.tengan.mall.cart.interfaces.rest.dto.AddCartItemRequest;
import com.tengan.mall.cart.interfaces.rest.dto.AddCartItemResponse;
import com.tengan.mall.cart.interfaces.rest.dto.CartCountResponse;
import com.tengan.mall.cart.interfaces.rest.dto.CartLineResponse;
import com.tengan.mall.cart.interfaces.rest.dto.CartListResponse;
import com.tengan.mall.cart.interfaces.rest.dto.ToggleCheckedRequest;
import com.tengan.mall.cart.interfaces.rest.dto.UpdateCartItemCountRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * 這個前綴同時服務會員跟訪客——身份由 CartIdentityFilter 解析放進 request attribute，這裡
 * 一律用 CartIdentityFilter.resolveOwner(request) 取得（見該類註解，SecurityConfig 對這個
 * 前綴是 permitAll，不是靠 Spring Security 驗證鏈強制登入）。
 */
@RestController
@RequestMapping("/api/customer/cart")
public class CustomerCartController {

    private final ListCartUseCase listCartUseCase;
    private final MiniCartUseCase miniCartUseCase;
    private final CartCountUseCase cartCountUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final UpdateCartItemCountUseCase updateCartItemCountUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ToggleCartItemCheckedUseCase toggleCartItemCheckedUseCase;
    private final ToggleCheckedAllUseCase toggleCheckedAllUseCase;
    private final RemoveCheckedItemsUseCase removeCheckedItemsUseCase;
    private final MergeCartUseCase mergeCartUseCase;

    public CustomerCartController(ListCartUseCase listCartUseCase, MiniCartUseCase miniCartUseCase,
            CartCountUseCase cartCountUseCase, AddCartItemUseCase addCartItemUseCase,
            UpdateCartItemCountUseCase updateCartItemCountUseCase, RemoveCartItemUseCase removeCartItemUseCase,
            ToggleCartItemCheckedUseCase toggleCartItemCheckedUseCase,
            ToggleCheckedAllUseCase toggleCheckedAllUseCase, RemoveCheckedItemsUseCase removeCheckedItemsUseCase,
            MergeCartUseCase mergeCartUseCase) {
        this.listCartUseCase = listCartUseCase;
        this.miniCartUseCase = miniCartUseCase;
        this.cartCountUseCase = cartCountUseCase;
        this.addCartItemUseCase = addCartItemUseCase;
        this.updateCartItemCountUseCase = updateCartItemCountUseCase;
        this.removeCartItemUseCase = removeCartItemUseCase;
        this.toggleCartItemCheckedUseCase = toggleCartItemCheckedUseCase;
        this.toggleCheckedAllUseCase = toggleCheckedAllUseCase;
        this.removeCheckedItemsUseCase = removeCheckedItemsUseCase;
        this.mergeCartUseCase = mergeCartUseCase;
    }

    @GetMapping("/items")
    public CartListResponse items(HttpServletRequest request) {
        var result = listCartUseCase.list(owner(request));
        return new CartListResponse(toResponses(result.items()), result.checkedTotalPrice(), result.totalItemCount());
    }

    @GetMapping("/mini")
    public CartListResponse mini(HttpServletRequest request, @RequestParam(defaultValue = "5") int limit) {
        var result = miniCartUseCase.mini(owner(request), limit);
        return new CartListResponse(toResponses(result.items()), result.checkedTotalPrice(), result.totalItemCount());
    }

    @GetMapping("/count")
    public CartCountResponse count(HttpServletRequest request) {
        return new CartCountResponse(cartCountUseCase.count(owner(request)));
    }

    @PostMapping("/items")
    public ResponseEntity<AddCartItemResponse> add(HttpServletRequest request,
            @Valid @RequestBody AddCartItemRequest addRequest) {
        var result = addCartItemUseCase
                .add(new AddCartItemCommand(owner(request), addRequest.skuId(), addRequest.count(),
                        addRequest.specText()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddCartItemResponse(result.itemId()));
    }

    @PutMapping("/items/{itemId}")
    public void updateCount(HttpServletRequest request, @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemCountRequest updateRequest) {
        updateCartItemCountUseCase.update(owner(request), itemId, updateRequest.count());
    }

    @DeleteMapping("/items/{itemId}")
    public void remove(HttpServletRequest request, @PathVariable Long itemId) {
        removeCartItemUseCase.remove(owner(request), itemId);
    }

    @PutMapping("/items/{itemId}/checked")
    public void toggleChecked(HttpServletRequest request, @PathVariable Long itemId,
            @RequestBody ToggleCheckedRequest toggleRequest) {
        toggleCartItemCheckedUseCase.toggle(owner(request), itemId, toggleRequest.checked());
    }

    @PutMapping("/checked-all")
    public void toggleCheckedAll(HttpServletRequest request, @RequestBody ToggleCheckedRequest toggleRequest) {
        toggleCheckedAllUseCase.toggleAll(owner(request), toggleRequest.checked());
    }

    @DeleteMapping("/items/checked")
    public void removeChecked(HttpServletRequest request) {
        removeCheckedItemsUseCase.removeChecked(owner(request));
    }

    /** 合併動作本質上要求已登入——這個前綴整體是 permitAll，所以在這裡手動擋，不是靠 Spring Security。 */
    @PostMapping("/merge")
    public ResponseEntity<Void> merge(HttpServletRequest request) {
        Long memberId = CartIdentityFilter.resolveMemberIdOrNull(request);
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String guestKey = CartIdentityFilter.resolveGuestKey(request).orElse(null);
        mergeCartUseCase.merge(memberId, guestKey);
        return ResponseEntity.noContent().build();
    }

    private CartOwner owner(HttpServletRequest request) {
        return CartIdentityFilter.resolveOwner(request);
    }

    private List<CartLineResponse> toResponses(List<CartLineView> items) {
        return items.stream()
                .map(i -> new CartLineResponse(i.itemId(), i.skuId(), i.spuId(), i.name(), i.price(), i.mainImage(),
                        i.count(), i.checked(), i.specText(), i.available()))
                .toList();
    }
}
