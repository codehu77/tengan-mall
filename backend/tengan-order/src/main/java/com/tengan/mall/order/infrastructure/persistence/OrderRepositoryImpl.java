package com.tengan.mall.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.order.domain.model.Order;
import com.tengan.mall.order.domain.model.OrderItem;
import com.tengan.mall.order.domain.repository.OrderRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * root+多筆 child insert 需要原子性，這裡自己標 @Transactional（比照 SpuRepositoryImpl 的既有慣例）；
 * 呼叫這個 save() 的 CreateOrderService 本身不標 @Transactional——避免把 HTTP 呼叫（鎖庫存/核銷優惠券）
 * 包進資料庫交易範圍，見規劃文件三、的說明。
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderRepositoryImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public Optional<Order> findByOrderSn(String orderSn) {
        OrderPO po = orderMapper.selectOne(new LambdaQueryWrapper<OrderPO>().eq(OrderPO::getOrderSn, orderSn));
        if (po == null) {
            return Optional.empty();
        }
        var items = orderItemMapper.findByOrderId(po.getId()).stream()
                .map(i -> new OrderItem(i.getId(), i.getSkuId(), i.getSpuId(), i.getSkuName(), i.getSkuImage(),
                        i.getPrice(), i.getCount(), i.getSubtotal()))
                .toList();
        return Optional.of(Order.reconstitute(po.getId(), po.getOrderSn(), po.getMemberId(), po.getStatus(),
                po.getCancelReason(), po.getTotalAmount(), po.getDiscountAmount(), po.getPayAmount(),
                po.getPaymentMethod(), po.getCouponId(), po.getReceiverName(), po.getReceiverPhone(), po.getCity(),
                po.getDistrict(), po.getPostalCode(), po.getStreet(), po.getRemark(), toInstant(po.getReceiptTime()),
                toInstant(po.getCreatedAt()), items));
    }

    @Override
    @Transactional
    public Order save(Order order) {
        OrderPO po = new OrderPO();
        po.setOrderSn(order.getOrderSn());
        po.setMemberId(order.getMemberId());
        po.setStatus(order.getStatus());
        po.setTotalAmount(order.getTotalAmount());
        po.setDiscountAmount(order.getDiscountAmount());
        po.setPayAmount(order.getPayAmount());
        po.setPaymentMethod(order.getPaymentMethod());
        po.setCouponId(order.getCouponId());
        po.setReceiverName(order.getReceiverName());
        po.setReceiverPhone(order.getReceiverPhone());
        po.setCity(order.getCity());
        po.setDistrict(order.getDistrict());
        po.setPostalCode(order.getPostalCode());
        po.setStreet(order.getStreet());
        po.setRemark(order.getRemark());
        orderMapper.insert(po);
        order.assignId(po.getId());

        for (OrderItem item : order.getItems()) {
            OrderItemPO itemPO = new OrderItemPO();
            itemPO.setOrderId(po.getId());
            itemPO.setSkuId(item.skuId());
            itemPO.setSpuId(item.spuId());
            itemPO.setSkuName(item.skuName());
            itemPO.setSkuImage(item.skuImage());
            itemPO.setPrice(item.price());
            itemPO.setCount(item.count());
            itemPO.setSubtotal(item.subtotal());
            orderItemMapper.insert(itemPO);
        }
        return order;
    }

    @Override
    public boolean markCancelled(String orderSn, String reason) {
        return orderMapper.markCancelled(orderSn, reason) > 0;
    }

    @Override
    public boolean markPaid(String orderSn) {
        return orderMapper.markPaid(orderSn) > 0;
    }

    @Override
    public boolean markShipped(String orderSn) {
        return orderMapper.markShipped(orderSn) > 0;
    }

    @Override
    public boolean markCompleted(String orderSn) {
        return orderMapper.markCompleted(orderSn) > 0;
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
