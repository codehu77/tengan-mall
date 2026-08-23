package com.tengan.mall.product.application.port;

import java.util.List;

/** 呼叫 tengan-media 清掉商品圖片，best-effort（外部貼的圖片網址由 tengan-media 那邊安靜略過）。 */
public interface MediaPort {

    void deleteImages(List<String> urls);
}
