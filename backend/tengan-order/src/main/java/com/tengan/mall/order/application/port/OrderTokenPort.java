package com.tengan.mall.order.application.port;

/**
 * 下單防重複提交 token（見規劃文件一、5.）。issue 只是單純寫入 Redis，不需要原子性；
 * consume 才是關鍵——「比對是否相符+相符就刪除」必須是單一原子操作（Redis Lua script），
 * 不能拆成兩次 Redis 呼叫，否則兩個併發請求可能都讀到「尚未被刪除」而重複通過。
 */
public interface OrderTokenPort {

    String issue(Long memberId);

    boolean consume(Long memberId, String token);
}
