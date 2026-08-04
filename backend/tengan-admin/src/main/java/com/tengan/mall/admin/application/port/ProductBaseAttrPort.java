package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-product 的規格屬性 internal 端點，跟 {@link ProductCategoryPort} 同樣的純代理原則。 */
public interface ProductBaseAttrPort {

    List<BaseAttrItem> listBaseAttrs(Long categoryId);

    Long createBaseAttr(CreateBaseAttrPayload payload, String operatorToken);

    void updateBaseAttr(Long id, UpdateBaseAttrPayload payload, String operatorToken);

    void deleteBaseAttr(Long id, String operatorToken);
}
