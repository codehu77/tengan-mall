package com.tengan.mall.search.interfaces.rest;

import com.tengan.mall.search.application.SearchSkusQuery;
import com.tengan.mall.search.application.SearchSkusUseCase;
import com.tengan.mall.search.interfaces.rest.dto.AttrAggResponse;
import com.tengan.mall.search.interfaces.rest.dto.AttrValueCountResponse;
import com.tengan.mall.search.interfaces.rest.dto.BrandAggResponse;
import com.tengan.mall.search.interfaces.rest.dto.SearchAggregationsResponse;
import com.tengan.mall.search.interfaces.rest.dto.SearchItemResponse;
import com.tengan.mall.search.interfaces.rest.dto.SearchResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 單一端點同時回傳 items + aggregations，減少一次往返（.docs 前台API清單已寫好的合併建議）。 */
@RestController
@RequestMapping("/api/public/search")
public class PublicSearchController {

    /** attrKey 是 "BASE-10"/"SALE-10" 這種組合鍵（見 SkuSearchAttrValue 的說明），不是純數字。 */
    private static final Pattern ATTR_PARAM_PATTERN = Pattern.compile("^attrs\\[([^\\]]+)]$");

    private final SearchSkusUseCase searchSkusUseCase;

    public PublicSearchController(SearchSkusUseCase searchSkusUseCase) {
        this.searchSkusUseCase = searchSkusUseCase;
    }

    @GetMapping
    public SearchResponse search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long catId,
            @RequestParam(required = false) List<Long> brandId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam MultiValueMap<String, String> allParams) {
        Map<String, List<String>> attrs = parseAttrParams(allParams);
        var result = searchSkusUseCase
                .search(new SearchSkusQuery(keyword, catId, brandId, attrs, sort, order, page, pageSize));
        return toResponse(result);
    }

    /** query string 用 attrs[attrKey]=value（可重複）表示，Spring 沒有內建的巢狀 map binder，手動解析。 */
    private Map<String, List<String>> parseAttrParams(MultiValueMap<String, String> allParams) {
        Map<String, List<String>> attrs = new HashMap<>();
        for (String key : allParams.keySet()) {
            Matcher matcher = ATTR_PARAM_PATTERN.matcher(key);
            if (matcher.matches()) {
                attrs.put(matcher.group(1), allParams.get(key));
            }
        }
        return attrs;
    }

    private SearchResponse toResponse(com.tengan.mall.search.application.SearchSkusResult result) {
        var items = result.items().stream()
                .map(i -> new SearchItemResponse(i.skuId(), i.spuId(), i.skuName(), i.price(), i.mainImage(),
                        i.saleCount(), i.brandId(), i.brandName()))
                .toList();
        var brands = result.aggregations().brands().stream()
                .map(b -> new BrandAggResponse(b.brandId(), b.brandName(), b.count()))
                .toList();
        var attrs = result.aggregations().attrs().stream()
                .map(a -> new AttrAggResponse(a.attrKey(), a.attrName(),
                        a.values().stream().map(v -> new AttrValueCountResponse(v.value(), v.count())).toList()))
                .toList();
        return new SearchResponse(items, result.total(), result.page(), result.pageSize(),
                new SearchAggregationsResponse(brands, attrs));
    }
}
