package com.tengan.mall.media.application.lifecycle;

import java.time.LocalDateTime;

public interface CleanupOrphanMediaAssetsUseCase {

    /** 回收 status=PENDING 且 updated_at 早於 cutoff 的物件，回傳實際刪除筆數。 */
    int cleanup(LocalDateTime cutoff);
}
