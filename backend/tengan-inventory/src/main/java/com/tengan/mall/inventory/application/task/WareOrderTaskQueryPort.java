package com.tengan.mall.inventory.application.task;

import java.time.Instant;
import java.util.List;

/** 供客服排查卡單問題用的 CQRS-lite 唯讀查詢，不透過 WareOrderTaskRepository（那個介面服務 lock/release/deduct 流程）。 */
public interface WareOrderTaskQueryPort {

    List<WareOrderTaskQueryItem> search(Integer status, Instant from, Instant to, int pageNum, int pageSize);

    long countSearch(Integer status, Instant from, Instant to);
}
