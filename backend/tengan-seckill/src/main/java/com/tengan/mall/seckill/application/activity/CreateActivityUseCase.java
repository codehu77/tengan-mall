package com.tengan.mall.seckill.application.activity;

public interface CreateActivityUseCase {

    /** 回傳新建立活動的 id。 */
    Long create(CreateActivityCommand command);
}
