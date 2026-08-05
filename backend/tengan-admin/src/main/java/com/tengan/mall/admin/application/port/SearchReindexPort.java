package com.tengan.mall.admin.application.port;

/** 呼叫端命名——轉發到 tengan-search 的全量重建索引端點，純代理，不重做業務規則。 */
public interface SearchReindexPort {

    int reindex();
}
