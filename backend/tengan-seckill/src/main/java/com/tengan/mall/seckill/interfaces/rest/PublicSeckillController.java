package com.tengan.mall.seckill.interfaces.rest;

import com.tengan.mall.seckill.application.display.ListActiveActivitiesUseCase;
import com.tengan.mall.seckill.interfaces.rest.dto.PublicActivityListResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.PublicActivityResponse;
import com.tengan.mall.seckill.interfaces.rest.dto.PublicSkuResponse;
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
    public PublicActivityListResponse listActivities() {
        var activities = listActiveActivitiesUseCase.list().stream()
                .map(a -> new PublicActivityResponse(a.id(), a.activityType().name(), a.startTime(), a.endTime(),
                        a.skus().stream()
                                .map(s -> new PublicSkuResponse(s.skuId(), s.spuId(), s.name(), s.mainImage(),
                                        s.originalPrice(), s.seckillPrice(), s.limitPerUser(), s.remaining()))
                                .toList()))
                .toList();
        return new PublicActivityListResponse(activities);
    }
}
