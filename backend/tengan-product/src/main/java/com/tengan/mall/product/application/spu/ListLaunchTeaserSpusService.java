package com.tengan.mall.product.application.spu;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListLaunchTeaserSpusService implements ListLaunchTeaserSpusUseCase {

    private final LaunchTeaserQueryPort launchTeaserQueryPort;

    public ListLaunchTeaserSpusService(LaunchTeaserQueryPort launchTeaserQueryPort) {
        this.launchTeaserQueryPort = launchTeaserQueryPort;
    }

    @Override
    public List<LaunchTeaserSpuView> listForHome(int limit) {
        return launchTeaserQueryPort.listForHome(limit);
    }

    @Override
    public ListLaunchTeaserSpusResult search(int pageNum, int pageSize) {
        var items = launchTeaserQueryPort.search(pageNum, pageSize);
        return new ListLaunchTeaserSpusResult(items, launchTeaserQueryPort.count());
    }
}
