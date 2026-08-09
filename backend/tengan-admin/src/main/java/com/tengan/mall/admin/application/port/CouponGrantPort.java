package com.tengan.mall.admin.application.port;

public interface CouponGrantPort {

    GrantCouponsResult grant(GrantCouponsPayload payload, String operatorToken);
}
