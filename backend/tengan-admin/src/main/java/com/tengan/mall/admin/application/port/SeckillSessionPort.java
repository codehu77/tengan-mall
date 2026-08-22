package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-seckill 的場次範本 internal 端點，跟 {@link SeckillActivityPort} 同樣的純代理原則。 */
public interface SeckillSessionPort {

    List<SeckillSessionItem> listSessions();

    Long createSession(SeckillSessionPayload payload);

    void updateSession(Long id, SeckillSessionPayload payload);

    void deleteSession(Long id);
}
