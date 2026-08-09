package com.tengan.mall.admin.application.port;

public interface InventoryTaskPort {

    TaskPageResult listTasks(Integer status, String from, String to, int page, int pageSize);
}
