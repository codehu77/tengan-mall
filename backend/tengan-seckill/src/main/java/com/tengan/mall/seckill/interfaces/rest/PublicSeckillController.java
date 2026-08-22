package com.tengan.mall.seckill.interfaces.rest;

import com.tengan.mall.seckill.application.display.ActiveProductView;
import com.tengan.mall.seckill.application.display.ListActiveActivitiesUseCase;
import com.tengan.mall.seckill.interfaces.rest.dto.PublicFlashSaleSessionResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.PublicLaunchResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.PublicProductResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.PublicSeckillDisplayResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.PublicSkuResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 不需要登入——首頁輪播/獨立列表頁/商品詳情頁徽章/購物車顯示（透過 BFF 二次查詢）共用同一支。 */
@RestController
@RequestMapping("/api/public/seckill")
public class PublicSeckillController {

    private final ListActiveActivitiesUseCase listActiveActivitiesUseCase;

    public PublicSeckillController(ListActiveActivitiesUseCase listActiveActivitiesUseCase) {
        this.listActiveActivitiesUseCase = listActiveActivitiesUseCase;
    }

    @GetMapping("/activities")
    public PublicSeckillDisplayResponse listActivities() {
        var display = listActiveActivitiesUseCase.list();
        var flashSaleSessions = display.flashSaleSessions().stream()
                .map(s -> new PublicFlashSaleSessionResponse(s.activityId(), s.sessionId(), s.sessionName(),
                        s.startTime(), s.endTime(), s.status(), toProductResponses(s.products())))
                .toList();
        var launches = display.launches().stream()
                .map(l -> new PublicLaunchResponse(l.activityId(), l.startTime(), l.endTime(),
                        toProductResponses(l.products())))
                .toList();
        return new PublicSeckillDisplayResponse(flashSaleSessions, launches);
    }

    private List<PublicProductResponse> toProductResponses(List<ActiveProductView> products) {
        return products.stream()
                .map(p -> new PublicProductResponse(p.spuId(), p.name(), p.mainImage(),
                        p.skus().stream()
                                .map(s -> new PublicSkuResponse(s.skuId(), s.variantLabel(), s.originalPrice(),
                                        s.seckillPrice(), s.limitPerUser(), s.remaining()))
                                .toList()))
                .toList();
    }
}
