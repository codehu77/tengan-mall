package com.tengan.mall.search.application;

import com.tengan.mall.search.infrastructure.elasticsearch.SpuSearchRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * demo 規模資料量不大，直接 delete-all + 重新 saveAll 就夠，不做 alias+reindex-into-new-index
 * 那套零停機切換（YAGNI，跟這個專案一貫的取捨一致）。
 */
@Service
public class ReindexAllService implements ReindexAllUseCase {

    private static final int EXPORT_PAGE_SIZE = 50;

    private final ProductCatalogPort productCatalogPort;
    private final SpuSearchRepository spuSearchRepository;

    public ReindexAllService(ProductCatalogPort productCatalogPort, SpuSearchRepository spuSearchRepository) {
        this.productCatalogPort = productCatalogPort;
        this.spuSearchRepository = spuSearchRepository;
    }

    @Override
    public int reindexAll() {
        List<SpuSearchDocument> documents = new ArrayList<>();
        int pageNum = 1;
        while (true) {
            ProductCatalogPage page = productCatalogPort.fetchPage(pageNum, EXPORT_PAGE_SIZE);
            page.spus().stream().map(SpuSearchDocumentFactory::from).forEach(documents::add);
            if (!page.hasNext()) {
                break;
            }
            pageNum++;
        }

        spuSearchRepository.deleteAll();
        if (!documents.isEmpty()) {
            spuSearchRepository.saveAll(documents);
        }
        return documents.size();
    }
}
