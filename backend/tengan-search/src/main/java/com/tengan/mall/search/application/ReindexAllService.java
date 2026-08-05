package com.tengan.mall.search.application;

import com.tengan.mall.search.infrastructure.elasticsearch.SkuSearchRepository;
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
    private final SkuSearchRepository skuSearchRepository;

    public ReindexAllService(ProductCatalogPort productCatalogPort, SkuSearchRepository skuSearchRepository) {
        this.productCatalogPort = productCatalogPort;
        this.skuSearchRepository = skuSearchRepository;
    }

    @Override
    public int reindexAll() {
        List<SkuSearchDocument> documents = new ArrayList<>();
        int pageNum = 1;
        while (true) {
            ProductCatalogPage page = productCatalogPort.fetchPage(pageNum, EXPORT_PAGE_SIZE);
            page.skus().stream().map(SkuSearchDocumentFactory::from).forEach(documents::add);
            if (!page.hasNext()) {
                break;
            }
            pageNum++;
        }

        skuSearchRepository.deleteAll();
        if (!documents.isEmpty()) {
            skuSearchRepository.saveAll(documents);
        }
        return documents.size();
    }
}
