package com.tengan.mall.seckill.application.reservation;

/**
 * 三道防線本體，供 tengan-order 的訂單建立 Saga 呼叫（比照 InventoryPort.lock 的既有模式）。
 * 失敗會拋出 {@code SeckillNotActiveException}/{@code SeckillPurchaseLimitExceededException}/
 * {@code SeckillSoldOutException}，呼叫端據此決定整筆訂單是否失敗（見規劃第 4.2 節）。
 */
public interface ReserveQuotaUseCase {

    ReserveQuotaResult reserve(ReserveQuotaCommand command);
}
