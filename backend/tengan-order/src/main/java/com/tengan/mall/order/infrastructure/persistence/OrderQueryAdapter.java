package com.tengan.mall.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.order.application.order.OrderDetailView;
import com.tengan.mall.order.application.order.OrderItemView;
import com.tengan.mall.order.application.order.OrderQueryPort;
import com.tengan.mall.order.application.order.OrderSummary;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class OrderQueryAdapter implements OrderQueryPort {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderQueryAdapter(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public List<OrderSummary> search(Long memberId, Integer status, int pageNum, int pageSize) {
        Page<OrderPO> page = orderMapper.selectPage(new Page<>(pageNum, pageSize),
                buildWrapper(memberId, status).orderByDesc(OrderPO::getCreatedAt).orderByDesc(OrderPO::getId));
        return page.getRecords().stream()
                .map(po -> new OrderSummary(po.getId(), po.getOrderSn(), po.getMemberId(), po.getStatus().getValue(),
                        po.getPayAmount(), po.getPaymentMethod(), toInstant(po.getCreatedAt())))
                .toList();
    }

    @Override
    public long countSearch(Long memberId, Integer status) {
        return orderMapper.selectCount(buildWrapper(memberId, status));
    }

    @Override
    public Optional<OrderDetailView> findDetailByOrderSn(String orderSn) {
        OrderPO po = orderMapper.selectOne(new LambdaQueryWrapper<OrderPO>().eq(OrderPO::getOrderSn, orderSn));
        if (po == null) {
            return Optional.empty();
        }
        List<OrderItemView> items = orderItemMapper.findByOrderId(po.getId()).stream()
                .map(i -> new OrderItemView(i.getSkuId(), i.getSpuId(), i.getSkuName(), i.getSkuImage(),
                        i.getPrice(), i.getCount(), i.getSubtotal()))
                .toList();
        return Optional.of(new OrderDetailView(po.getId(), po.getOrderSn(), po.getMemberId(),
                po.getStatus().getValue(), po.getCancelReason(), po.getTotalAmount(), po.getDiscountAmount(),
                po.getPayAmount(), po.getPaymentMethod(), po.getCouponId(), po.getReceiverName(),
                po.getReceiverPhone(), po.getCity(), po.getDistrict(), po.getPostalCode(), po.getStreet(),
                po.getRemark(), toInstant(po.getReceiptTime()), toInstant(po.getCreatedAt()), items));
    }

    @Override
    public long countCreatedToday() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        return orderMapper.selectCount(new LambdaQueryWrapper<OrderPO>().ge(OrderPO::getCreatedAt, startOfToday));
    }

    private LambdaQueryWrapper<OrderPO> buildWrapper(Long memberId, Integer status) {
        LambdaQueryWrapper<OrderPO> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) {
            wrapper.eq(OrderPO::getMemberId, memberId);
        }
        if (status != null) {
            wrapper.eq(OrderPO::getStatus, status);
        }
        return wrapper;
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
