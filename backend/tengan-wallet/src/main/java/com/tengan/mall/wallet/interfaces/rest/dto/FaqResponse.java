package com.tengan.mall.wallet.interfaces.rest.dto;

import java.util.List;

public record FaqResponse(List<PointFaqItemResponse> items) {

    public record PointFaqItemResponse(String id, String question, String answer) {
    }
}
