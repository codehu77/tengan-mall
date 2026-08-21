package com.tengan.mall.seckill.application.activity;

public interface ListActivitiesUseCase {

    ActivityPageResult list(int pageNum, int pageSize);
}
