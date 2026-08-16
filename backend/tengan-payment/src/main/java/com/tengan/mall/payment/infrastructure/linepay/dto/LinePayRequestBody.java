package com.tengan.mall.payment.infrastructure.linepay.dto;

import java.util.List;

public record LinePayRequestBody(long amount, String currency, String orderId, List<Package> packages,
        RedirectUrls redirectUrls) {

    public record Package(String id, long amount, List<Product> products) {
    }

    public record Product(String name, int quantity, long price) {
    }

    public record RedirectUrls(String confirmUrl, String cancelUrl) {
    }
}
