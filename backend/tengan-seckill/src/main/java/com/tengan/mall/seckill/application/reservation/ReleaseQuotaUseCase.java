package com.tengan.mall.seckill.application.reservation;

/** 補償動作：訂單 Saga 裡其他步驟失敗時，把已經保留成功的秒殺配額還回去（見規劃第 4.2 節）。 */
public interface ReleaseQuotaUseCase {

    void release(ReleaseQuotaCommand command);
}
