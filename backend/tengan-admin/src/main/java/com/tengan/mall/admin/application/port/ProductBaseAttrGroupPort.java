package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-product 的規格屬性分組 internal 端點，跟 {@link ProductCategoryPort} 同樣的純代理原則。 */
public interface ProductBaseAttrGroupPort {

    List<BaseAttrGroupItem> listBaseAttrGroups(Long categoryId);

    Long createBaseAttrGroup(CreateBaseAttrGroupPayload payload, String operatorToken);

    void updateBaseAttrGroup(Long id, UpdateBaseAttrGroupPayload payload, String operatorToken);

    void deleteBaseAttrGroup(Long id, String operatorToken);
}
