package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-coupon 的模板 internal 端點，跟 {@link ProductBrandPort} 同樣的純代理原則。 */
public interface CouponTemplatePort {

    List<TemplateItem> listTemplates();

    Long createTemplate(CreateTemplatePayload payload, String operatorToken);

    void updateTemplate(Long id, UpdateTemplatePayload payload, String operatorToken);

    void delistTemplate(Long id, String operatorToken);
}
