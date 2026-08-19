package com.tengan.mall.admin.application.port;

public record ReconcileNowResult(int paymentChecked, int paymentConverged, int paymentFailed,
        int subscriptionChecked, int subscriptionConverged, int subscriptionFailed, int renewalChecked,
        int renewalRecovered) {
}
