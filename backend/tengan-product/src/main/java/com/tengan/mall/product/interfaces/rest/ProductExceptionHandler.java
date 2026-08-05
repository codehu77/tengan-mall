package com.tengan.mall.product.interfaces.rest;

import com.tengan.mall.product.domain.exception.BaseAttrCategoryMismatchException;
import com.tengan.mall.product.domain.exception.BaseAttrGroupCategoryMismatchException;
import com.tengan.mall.product.domain.exception.BaseAttrGroupNotFoundException;
import com.tengan.mall.product.domain.exception.BaseAttrNotFoundException;
import com.tengan.mall.product.domain.exception.BrandNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryHasChildrenException;
import com.tengan.mall.product.domain.exception.CategoryLevelLimitExceededException;
import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotLeafException;
import com.tengan.mall.product.domain.exception.SaleAttrCategoryMismatchException;
import com.tengan.mall.product.domain.exception.SaleAttrNotFoundException;
import com.tengan.mall.product.domain.exception.SkuNotFoundException;
import com.tengan.mall.product.domain.exception.SpuHasNoSkuException;
import com.tengan.mall.product.domain.exception.SpuIsDuplicateException;
import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.exception.SpuNotOnShelfException;
import com.tengan.mall.product.domain.exception.SpuOnShelfException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionHandler {

    /** X-Identity-Assertion 簽章無效/過期/格式不對——跟 Spring Security 驗 Service JWT 失敗回應同一種狀態碼。 */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleInvalidIdentityAssertion(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({CategoryNotFoundException.class, BrandNotFoundException.class,
            BaseAttrGroupNotFoundException.class, BaseAttrNotFoundException.class, SaleAttrNotFoundException.class,
            SpuNotFoundException.class, SkuNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler({CategoryHasChildrenException.class, CategoryLevelLimitExceededException.class,
            BaseAttrGroupCategoryMismatchException.class, BaseAttrCategoryMismatchException.class,
            SaleAttrCategoryMismatchException.class, SpuHasNoSkuException.class, SpuNotOnShelfException.class,
            SpuOnShelfException.class, SpuIsDuplicateException.class})
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }

    /**
     * CategoryNotLeafException 是明確定義的業務規則；IllegalArgumentException/IllegalStateException
     * 涵蓋 BaseAttr/SaleAttr 聚合根建構子/方法擋下的不變條件——這幾種都是「請求本身不合法」，不是找不到
     * 資源或狀態衝突。BASE/SALE 拆成兩個聚合根之後，原本的 AttrTypeMismatchException（型別不符）
     * 已經沒有存在的必要——查哪張表就決定了型別，結構上不可能發生型別不符。
     */
    @ExceptionHandler({CategoryNotLeafException.class, IllegalArgumentException.class,
            IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}
