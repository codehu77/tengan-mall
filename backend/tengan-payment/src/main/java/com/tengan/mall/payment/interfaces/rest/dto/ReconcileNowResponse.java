package com.tengan.mall.payment.interfaces.rest.dto;

public record ReconcileNowResponse(int paymentChecked, int paymentConverged, int paymentFailed,
        int subscriptionChecked, int subscriptionConverged, int subscriptionFailed, int renewalChecked,
        int renewalRecovered) {
}
