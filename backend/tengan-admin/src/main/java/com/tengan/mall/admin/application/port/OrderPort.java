package com.tengan.mall.admin.application.port;

/** 呼叫 tengan-order 的訂單管理 internal 端點，跟 {@link ProductBrandPort} 同樣的純代理原則（不重做業務規則）。 */
public interface OrderPort {

    OrderPageResult listOrders(Integer status, int page, int pageSize);

    OrderDetail getOrderDetail(String orderSn);

    void shipOrder(String orderSn, String operatorToken);

    void cancelOrder(String orderSn, String reason, String operatorToken);
}
