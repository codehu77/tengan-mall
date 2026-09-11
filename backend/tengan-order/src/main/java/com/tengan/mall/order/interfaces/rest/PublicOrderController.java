package com.tengan.mall.order.interfaces.rest;

import com.tengan.mall.order.application.rule.GetFreightRuleUseCase;
import com.tengan.mall.order.interfaces.rest.dto.FreightRuleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 無需登入即可查詢，供購物車頁（訪客也能看）顯示免運門檻提示。 */
@RestController
@RequestMapping("/api/public/orders")
public class PublicOrderController {

    private final GetFreightRuleUseCase getFreightRuleUseCase;

    public PublicOrderController(GetFreightRuleUseCase getFreightRuleUseCase) {
        this.getFreightRuleUseCase = getFreightRuleUseCase;
    }

    @GetMapping("/freight-rule")
    public FreightRuleResponse freightRule() {
        var r = getFreightRuleUseCase.get();
        return new FreightRuleResponse(r.freeShippingThreshold(), r.shippingFee());
    }
}
