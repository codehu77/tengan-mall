package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-product 的標準聚合值 internal 端點，跟 {@link ProductBaseAttrPort} 同樣的純代理原則。 */
public interface ProductBaseAttrStandardValuePort {

    List<BaseAttrStandardValueItem> listByAttr(Long attrId);

    List<BaseAttrStandardValueItem> listByCategory(Long categoryId);

    Long create(Long attrId, CreateBaseAttrStandardValuePayload payload, String operatorToken);

    void update(Long id, UpdateBaseAttrStandardValuePayload payload, String operatorToken);
}
