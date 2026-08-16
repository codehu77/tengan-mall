package com.tengan.mall.wallet.interfaces.rest.dto;

import java.util.List;

public record TransactionCountsResponse(List<Item> items) {

    public record Item(String type, String status, long count) {
    }
}
