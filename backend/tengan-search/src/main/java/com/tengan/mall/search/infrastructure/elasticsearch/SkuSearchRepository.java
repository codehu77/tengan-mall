package com.tengan.mall.search.infrastructure.elasticsearch;

import com.tengan.mall.search.application.SkuSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/** upsert/依 id 刪除這種簡單操作交給 Spring Data，複雜的搜尋+聚合查詢改用 ElasticsearchOperations。 */
public interface SkuSearchRepository extends ElasticsearchRepository<SkuSearchDocument, Long> {
}
