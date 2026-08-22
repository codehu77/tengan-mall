package com.tengan.mall.product.application.spu;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BatchGetSpuSummariesService implements BatchGetSpuSummariesUseCase {

    private final SpuSummaryPort spuSummaryPort;

    public BatchGetSpuSummariesService(SpuSummaryPort spuSummaryPort) {
        this.spuSummaryPort = spuSummaryPort;
    }

    @Override
    public List<SpuSummaryView> get(List<Long> spuIds) {
        return spuSummaryPort.findByIds(spuIds);
    }
}
