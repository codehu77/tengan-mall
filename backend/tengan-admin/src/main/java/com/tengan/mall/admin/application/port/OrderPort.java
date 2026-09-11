package com.tengan.mall.admin.application.port;

import java.time.Instant;
import java.util.List;

/** 呼叫 tengan-order 的訂單管理 internal 端點，跟 {@link ProductBrandPort} 同樣的純代理原則（不重做業務規則）。 */
public interface OrderPort {

    OrderPageResult listOrders(Integer status, Instant from, Instant to, int page, int pageSize);

    OrderTodayStats getTodayStats();

    List<RevenueTrendPoint> getRevenueTrend(int days);

    OrderDetail getOrderDetail(String orderSn);

    void shipOrder(String orderSn, String operatorToken);

    void cancelOrder(String orderSn, String reason, String operatorToken);

    FreightRuleItem getFreightRule();

    void updateFreightRule(FreightRuleItem item, String operatorToken);
}
