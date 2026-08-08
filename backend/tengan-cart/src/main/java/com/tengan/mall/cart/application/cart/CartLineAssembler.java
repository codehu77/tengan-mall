package com.tengan.mall.cart.application.cart;

import com.tengan.mall.cart.domain.repository.CartItemRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ListCartService/MiniCartService 共用的組裝邏輯（比照 tengan-product 的 SpuCompositionAssembler：
 * package-private @Component，不是 use case本身）：讀原始購物車行 → 批次向 tengan-product
 * 查一次即時價格 → 合併成 CartLineView。CartCountService 不用這個（不需要商品資料，見
 * cart_storage_decision 的低頻讀取判準）。
 */
@Component
class CartLineAssembler {

    private final CartItemRepository cartItemRepository;
    private final GuestCartPort guestCartPort;
    private final ProductSkuPort productSkuPort;

    CartLineAssembler(CartItemRepository cartItemRepository, GuestCartPort guestCartPort,
            ProductSkuPort productSkuPort) {
        this.cartItemRepository = cartItemRepository;
        this.guestCartPort = guestCartPort;
        this.productSkuPort = productSkuPort;
    }

    List<CartLineView> assemble(CartOwner owner) {
        List<RawLine> rawLines = loadRawLines(owner);
        if (rawLines.isEmpty()) {
            return List.of();
        }
        List<Long> skuIds = rawLines.stream().map(RawLine::skuId).distinct().toList();
        Map<Long, ProductSkuInfo> infoBySkuId = productSkuPort.findByIds(skuIds).stream()
                .collect(Collectors.toMap(ProductSkuInfo::skuId, Function.identity()));
        return rawLines.stream().map(line -> toView(line, infoBySkuId.get(line.skuId()))).toList();
    }

    BigDecimal checkedTotalPrice(List<CartLineView> lines) {
        return lines.stream()
                .filter(CartLineView::checked)
                .filter(CartLineView::available)
                .map(line -> line.price().multiply(BigDecimal.valueOf(line.count())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<RawLine> loadRawLines(CartOwner owner) {
        return switch (owner) {
            case CartOwner.Member member -> cartItemRepository.findByUserId(member.userId()).stream()
                    .map(item -> new RawLine(item.getId(), item.getSkuId(), item.getCount(), item.isChecked(),
                            item.getSpecText()))
                    .toList();
            case CartOwner.Guest guest -> guestCartPort.findAll(guest.guestKey()).stream()
                    .map(item -> new RawLine(item.skuId(), item.skuId(), item.count(), item.checked(),
                            item.specText()))
                    .toList();
        };
    }

    private CartLineView toView(RawLine line, ProductSkuInfo info) {
        if (info == null) {
            return new CartLineView(line.itemId(), line.skuId(), null, null, null, null, line.count(),
                    line.checked(), line.specText(), false);
        }
        return new CartLineView(line.itemId(), line.skuId(), info.spuId(), info.name(), info.price(),
                info.mainImage(), line.count(), line.checked(), line.specText(), true);
    }

    private record RawLine(Long itemId, Long skuId, int count, boolean checked, String specText) {
    }
}
