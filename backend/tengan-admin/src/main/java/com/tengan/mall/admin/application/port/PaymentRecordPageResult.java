package com.tengan.mall.admin.application.port;

import java.util.List;

public record PaymentRecordPageResult(List<PaymentRecordItem> items, long total) {
}
