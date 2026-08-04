import { MOCK_CATEGORY_TREE } from '~/mocks/products'
import type { CategoryTree } from '~/mocks/products'

export function useCategories() {
  // P7 串接後替換為:
  // const { data: categories } = useFetch<CategoryTree[]>('/api/product/category/list/tree')
  const categories = ref<CategoryTree[]>(MOCK_CATEGORY_TREE)
  return { categories }
}
