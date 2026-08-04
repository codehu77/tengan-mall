package com.tengan.mall.product.domain.repository;

import com.tengan.mall.product.domain.model.ProductOperLog;

/** 只有 save——這次範圍沒有做查詢/列表頁，之後真的需要稽核紀錄檢視功能再補讀取方法。 */
public interface ProductOperLogRepository {

    ProductOperLog save(ProductOperLog operLog);
}
