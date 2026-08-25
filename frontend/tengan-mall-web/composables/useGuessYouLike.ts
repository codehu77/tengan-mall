import type { Product } from '~/mocks/products'
import type { SearchItem, SearchResponse } from '~/composables/useProductSearch'
import type { CategoryAffinityItem } from '~/composables/useCategoryInterest'

const PAGE_SIZE = 10

function toProduct(item: SearchItem): Product {
  return {
    skuId: item.skuId,
    spuId: item.spuId,
    skuName: item.spuName || item.skuName,
    price: item.price,
    skuDefaultImg: item.mainImage,
    saleCount: item.saleCount,
    categoryId: 0,
  }
}

/**
 * 「猜你喜歡」的無限捲動狀態機——分類走訪順序決定「該撈哪個分類」，每個分類撈到撈完（page
 * 超過 total）才換下一個分類，全部分類都撈完就是底（exhausted），不是真的無限捲動。因為每個
 * 商品只屬於一個最上層分類，天生不會重複撈到同一個商品，這裡不用另外做去重邏輯。
 *
 * 走訪順序只在 init() 決定一次、存在這個 composable 實例的記憶體裡，不會存到後端/cursor
 * 字串——重新整理頁面等於整個狀態歸零，會重新從第一個分類開始撈（這是刻意接受的取捨，見
 * 猜你喜歡規劃文件的說明，不是 bug）。
 */
export function useGuessYouLike() {
  const items = ref<Product[]>([])
  const exhausted = ref(false)
  const loading = ref(false)

  let walkOrder: number[] = []
  let categoryIdx = 0
  let pageInCategory = 1

  async function init() {
    const authStore = useAuthStore()

    // 這裡刻意不用 useCategories()——那支包的是 useFetch()，設計給元件 setup 時同步呼叫、
    // 靠 Suspense 等資料回來；在 onMounted 裡呼叫（這個 composable 的使用場景）不會被等到，
    // categories.value 常常還是空陣列，導致 walkOrder 算出來是空的、猜你喜歡永遠撈不到東西
    // （只有網路夠快、資料剛好在讀取前回來時才會正常，這是「要重整好幾次才出現」的真正原因）。
    // 改用一般 $fetch 直接等資料回來，跟下面 fetchAffinity 平行呼叫，兩個都是輕量查詢。
    const [treeRes, affinity] = await Promise.all([
      $fetch<{ items: { id: number }[] }>('/api/public/products/categories/tree'),
      authStore.isLoggedIn
        ? useCategoryInterest().fetchAffinity().catch(() => [] as CategoryAffinityItem[])
        : Promise.resolve([] as CategoryAffinityItem[]),
    ])
    const allIds = treeRes.items.map(c => c.id)

    if (authStore.isLoggedIn) {
      const affinityIds = affinity.map(a => a.categoryId)
      // 走訪順序：有分數的分類排前面（依分數高到低，後端已經排好序），其餘分類接在後面
      // （維持分類樹原本順序）。
      walkOrder = [...affinityIds, ...allIds.filter(id => !affinityIds.includes(id))]
    } else {
      // 訪客沒有任何偏好資料可用，跟零資料新會員是同一種退化情況——差別是額外洗牌一次，
      // 讓訪客每次重新整理都看到不同組合，不會每次都固定同一個順序、感覺很無聊。
      walkOrder = [...allIds].sort(() => Math.random() - 0.5)
    }
    await loadMore()
  }

  async function loadMore() {
    if (exhausted.value || loading.value || walkOrder.length === 0) return
    loading.value = true
    try {
      // 有些分類可能完全沒有商品（demo 資料沒塞、或剛好被下架光了）——撈到空結果不能就此
      // 停在這裡等下次觸發，因為區塊是 v-if="guessItems.length > 0" 才會出現，第一批要是
      // 撈到空分類，畫面上完全沒有東西可以觸發下一次捲動，會卡死。這裡改成迴圈，撈到空的
      // 就自動接著撈下一個分類，直到真的撈到商品，或者全部分類都撈完（真的到底）為止。
      while (categoryIdx < walkOrder.length) {
        const res = await $fetch<SearchResponse>('/api/public/search', {
          query: { catId: walkOrder[categoryIdx], sort: 'sale', order: 'desc', page: pageInCategory, pageSize: PAGE_SIZE },
        })
        if (pageInCategory * PAGE_SIZE >= res.total) {
          categoryIdx++
          pageInCategory = 1
        } else {
          pageInCategory++
        }
        if (res.items.length > 0) {
          items.value.push(...res.items.map(toProduct))
          return
        }
      }
      exhausted.value = true
    } finally {
      loading.value = false
    }
  }

  return { items, exhausted, loading, init, loadMore }
}
