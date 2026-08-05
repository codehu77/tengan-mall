package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.model.SpuStatus;
import com.tengan.mall.product.domain.repository.SpuRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchExportSpusService implements SearchExportSpusUseCase {

    private final SpuSearchPort spuSearchPort;
    private final SpuRepository spuRepository;
    private final SpuSearchDocumentAssembler assembler;

    public SearchExportSpusService(SpuSearchPort spuSearchPort, SpuRepository spuRepository,
            SpuSearchDocumentAssembler assembler) {
        this.spuSearchPort = spuSearchPort;
        this.spuRepository = spuRepository;
        this.assembler = assembler;
    }

    @Override
    public SearchExportSpusResult export(int pageNum, int pageSize) {
        var criteria = new SpuSearchCriteria(null, null, null, SpuStatus.ON_SHELF.getValue());
        List<SpuSummary> page = spuSearchPort.search(criteria, pageNum, pageSize);
        long total = spuSearchPort.countSearch(criteria);
        boolean hasNext = (long) pageNum * pageSize < total;

        List<SkuSearchDocumentPayload> skus = new ArrayList<>();
        for (SpuSummary summary : page) {
            spuRepository.findById(summary.id()).ifPresent(spu -> skus.addAll(assembler.assemble(spu)));
        }
        return new SearchExportSpusResult(skus, hasNext);
    }
}
