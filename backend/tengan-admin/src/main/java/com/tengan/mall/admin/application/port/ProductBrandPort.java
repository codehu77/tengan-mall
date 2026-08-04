package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-product 的品牌 internal 端點，跟 {@link ProductCategoryPort} 同樣的純代理原則。 */
public interface ProductBrandPort {

    List<BrandItem> listBrands();

    Long createBrand(CreateBrandPayload payload, String operatorToken);

    void updateBrand(Long id, UpdateBrandPayload payload, String operatorToken);

    void deleteBrand(Long id, String operatorToken);

    void showBrand(Long id, String operatorToken);

    void hideBrand(Long id, String operatorToken);
}
