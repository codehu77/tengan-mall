package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.application.port.MediaPort;
import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.exception.SpuOnShelfException;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.model.SpuStatus;
import com.tengan.mall.product.domain.repository.SpuRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteSpuService implements DeleteSpuUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeleteSpuService.class);

    private final SpuRepository spuRepository;
    private final ProductSearchEventPublisherPort searchEventPublisher;
    private final MediaPort mediaPort;

    public DeleteSpuService(SpuRepository spuRepository, ProductSearchEventPublisherPort searchEventPublisher,
            MediaPort mediaPort) {
        this.spuRepository = spuRepository;
        this.searchEventPublisher = searchEventPublisher;
        this.mediaPort = mediaPort;
    }

    @Override
    @Transactional
    public void delete(DeleteSpuCommand command) {
        Spu spu = spuRepository.findById(command.spuId())
                .orElseThrow(() -> new SpuNotFoundException(command.spuId()));
        if (spu.getStatus() == SpuStatus.ON_SHELF) {
            throw new SpuOnShelfException(command.spuId());
        }
        spuRepository.deleteById(command.spuId());
        // 防禦性發送：上架中不可刪的守衛已經擋在前面，這裡多半是刪除本來就不在索引裡的 spu，
        // tengan-search 收到刪除一個不存在的文件是冪等操作，不會出錯。
        searchEventPublisher.publishRemoved(spu.getId(), spu.getSkus().stream().map(sku -> sku.getId()).toList());

        // best-effort：清 MinIO 圖片失敗不影響 SPU 本身已經刪除成功，記 log 讓人工介入即可
        // （跟 CloseOrderIfUnpaidService 的補償失敗處理是同一種已知限制等級）。
        try {
            mediaPort.deleteImages(collectImageUrls(spu));
        } catch (RuntimeException e) {
            log.error("刪除 SPU 後清理 tengan-media 圖片失敗，spuId={}，需要人工到 MinIO 手動清理", spu.getId(), e);
        }
    }

    private List<String> collectImageUrls(Spu spu) {
        List<String> urls = new ArrayList<>();
        urls.add(spu.getMainImage());
        spu.getImages().forEach(image -> urls.add(image.imageUrl()));
        spu.getSkus().forEach(sku -> {
            urls.add(sku.getMainImage());
            sku.getImages().forEach(image -> urls.add(image.imageUrl()));
        });
        return urls;
    }
}
