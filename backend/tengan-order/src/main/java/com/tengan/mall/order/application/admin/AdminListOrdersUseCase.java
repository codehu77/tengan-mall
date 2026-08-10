package com.tengan.mall.order.application.admin;

import com.tengan.mall.order.application.order.MyOrderPageResult;

public interface AdminListOrdersUseCase {

    MyOrderPageResult list(AdminListOrdersQuery query);
}
