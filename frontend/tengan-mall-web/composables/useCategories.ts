import type { CategoryTree } from '~/mocks/products'

interface CategoryTreeNodeResponse {
  id: number
  name: string
  icon: string
  sort: number
  status: number
  children: CategoryTreeNodeResponse[]
}

interface CategoryTreeResponse {
  items: CategoryTreeNodeResponse[]
}

function toCategoryTree(node: CategoryTreeNodeResponse): CategoryTree {
  return {
    catId: node.id,
    name: node.name,
    icon: node.icon,
    children: node.children?.map(toCategoryTree),
  }
}

export function useCategories() {
  const { data } = useFetch<CategoryTreeResponse>('/api/public/products/categories/tree')

  const categories = computed<CategoryTree[]>(() =>
    data.value?.items.map(toCategoryTree) ?? []
  )

  return { categories }
}
