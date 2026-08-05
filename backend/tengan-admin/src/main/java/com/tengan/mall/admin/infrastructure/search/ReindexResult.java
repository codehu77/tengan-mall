package com.tengan.mall.admin.infrastructure.search;

/** tengan-search /internal/search/reindex 回應的反序列化目標，欄位名稱對齊即可，不用額外包 Envelope。 */
public record ReindexResult(int indexedCount) {
}
