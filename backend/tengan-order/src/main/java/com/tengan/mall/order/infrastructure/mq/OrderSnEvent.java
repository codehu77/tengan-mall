package com.tengan.mall.order.infrastructure.mq;

/** order.created 業務事件跟 order.delay 逾時觸發訊息共用同一種 payload 形狀（見規劃文件六、）。 */
public record OrderSnEvent(String orderSn) {
}
