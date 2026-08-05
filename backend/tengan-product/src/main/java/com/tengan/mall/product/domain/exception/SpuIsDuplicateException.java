package com.tengan.mall.product.domain.exception;

public class SpuIsDuplicateException extends RuntimeException {

    public SpuIsDuplicateException(Long spuId) {
        super("Spu 是尚未編輯確認的複製草稿，無法直接上架: " + spuId);
    }
}
