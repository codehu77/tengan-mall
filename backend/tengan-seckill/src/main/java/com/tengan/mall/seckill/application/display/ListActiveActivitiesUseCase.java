package com.tengan.mall.seckill.application.display;

/** 供 /api/public/seckill/activities 使用：首頁輪播/獨立列表頁/商品詳情頁徽章/購物車顯示共用同一支。 */
public interface ListActiveActivitiesUseCase {

    SeckillDisplayView list();
}
