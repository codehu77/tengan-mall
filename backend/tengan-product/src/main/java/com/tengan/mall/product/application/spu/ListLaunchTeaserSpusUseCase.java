package com.tengan.mall.product.application.spu;

import java.util.List;

public interface ListLaunchTeaserSpusUseCase {

    /** 首頁預告用：不分頁，伺服器端 cap 在 limit 筆內，前端自己 shuffle+裁切成兩列。 */
    List<LaunchTeaserSpuView> listForHome(int limit);

    /** 「看更多」整頁用：分頁版本。 */
    ListLaunchTeaserSpusResult search(int pageNum, int pageSize);
}
