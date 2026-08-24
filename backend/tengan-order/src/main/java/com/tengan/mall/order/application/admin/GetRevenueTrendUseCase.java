package com.tengan.mall.order.application.admin;

import com.tengan.mall.order.application.order.DailyRevenue;
import java.util.List;

public interface GetRevenueTrendUseCase {

    List<DailyRevenue> get(int days);
}
