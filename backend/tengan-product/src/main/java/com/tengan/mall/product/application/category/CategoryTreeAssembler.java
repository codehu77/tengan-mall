package com.tengan.mall.product.application.category;

import com.tengan.mall.product.domain.model.Category;
import com.tengan.mall.product.domain.model.CategoryStatus;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 把攤平的 Category 清單依 parentId 分組、遞迴組成巢狀樹（避免對自參考表下遞迴 SQL）。 */
final class CategoryTreeAssembler {

    private CategoryTreeAssembler() {
    }

    static List<CategoryTreeNode> assemble(List<Category> all, boolean visibleOnly) {
        List<Category> filtered = visibleOnly
                ? all.stream().filter(c -> c.getStatus() == CategoryStatus.VISIBLE).toList()
                : all;
        Map<Long, List<Category>> byParentId = filtered.stream().collect(Collectors.groupingBy(Category::getParentId));
        return buildChildren(0L, byParentId);
    }

    private static List<CategoryTreeNode> buildChildren(Long parentId, Map<Long, List<Category>> byParentId) {
        return byParentId.getOrDefault(parentId, List.of()).stream()
                .sorted(Comparator.comparingInt(Category::getSort))
                .map(c -> new CategoryTreeNode(c.getId(), c.getName(), c.getIcon(), c.getSort(),
                        c.getStatus().getValue(), buildChildren(c.getId(), byParentId)))
                .toList();
    }
}
