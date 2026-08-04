package com.tengan.mall.admin.application.port;

import java.util.List;

/**
 * 呼叫 tengan-product 的商品分類 internal 端點。BFF 純代理，不複製 tengan-product 的業務規則
 * 進 tengan-admin（見 admin_backend_design 記憶）——分類三層限制、刪除有子節點擋 409 等規則的
 * 真相都在 tengan-product 自己，這裡只轉發跟映射資料形狀。
 *
 * <p>operatorToken 是發起這個請求的管理員原始 JWT（未加 "Bearer " 前綴），轉發成
 * X-Identity-Assertion 讓 tengan-product 記自己的 product_oper_log。</p>
 */
public interface ProductCategoryPort {

    List<CategoryTreeItem> listCategories();

    Long createCategory(CreateCategoryPayload payload, String operatorToken);

    void updateCategory(Long id, UpdateCategoryPayload payload, String operatorToken);

    void deleteCategory(Long id, String operatorToken);

    void showCategory(Long id, String operatorToken);

    void hideCategory(Long id, String operatorToken);
}
