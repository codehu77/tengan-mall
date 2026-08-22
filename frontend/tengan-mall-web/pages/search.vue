<template>
  <div class="max-w-7xl mx-auto px-4 py-6">

    <!-- 搜尋標題 -->
    <div class="mb-4">
      <h1 class="text-lg text-gray-700">
        <template v-if="keyword">
          搜尋「<span class="font-bold text-gray-900">{{ keyword }}</span>」的結果
        </template>
        <template v-else-if="catName">
          <span class="font-bold text-gray-900">{{ catName }}</span> 的商品
        </template>
        <template v-else>
          商品搜尋結果
        </template>
        <span class="text-sm text-gray-400 ml-2">共 {{ total }} 件商品</span>
      </h1>
    </div>

    <!-- 進階篩選 -->
    <SearchFilter v-if="total > 0" :aggregations="aggregations" @change="onFilterChange" />

    <!-- 排序列 -->
    <div v-if="total > 0" class="flex items-center gap-2 mb-4 bg-white rounded-lg px-4 py-2 shadow-sm">
      <span class="text-sm text-gray-500 mr-1">篩選</span>

      <button
        v-for="opt in sortOptions"
        :key="opt.value"
        class="text-sm px-4 py-1.5 rounded-md transition"
        :class="sortBy === opt.value && priceSort === 'none' ? 'bg-red-500 text-white' : 'text-gray-600 hover:bg-gray-100'"
        @click="setSortBy(opt.value)"
      >
        {{ opt.label }}
      </button>

      <!-- 價格三態按鈕 -->
      <button
        class="flex items-center gap-1.5 text-sm px-4 py-1.5 rounded-md transition"
        :class="priceSort !== 'none' ? 'text-red-500' : 'text-gray-600 hover:bg-gray-100'"
        @click="cyclePriceSort"
      >
        價格
        <span class="flex flex-col leading-none gap-px">
          <UIcon
            name="i-heroicons-chevron-up"
            class="w-3 h-3 transition-opacity"
            :class="priceSort === 'desc' ? 'opacity-20' : 'opacity-60'"
          />
          <UIcon
            name="i-heroicons-chevron-down"
            class="w-3 h-3 transition-opacity"
            :class="priceSort === 'asc' ? 'opacity-20' : 'opacity-60'"
          />
        </span>
      </button>

      <!-- 右側分頁 -->
      <div class="ml-auto flex items-center gap-1 text-sm text-gray-500">
        <span>{{ currentPage }}/{{ totalPages }}</span>
        <button
          class="p-1 rounded hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed"
          :disabled="currentPage <= 1"
          @click="currentPage--"
        >
          <UIcon name="i-heroicons-chevron-left" class="w-4 h-4" />
        </button>
        <button
          class="p-1 rounded hover:bg-gray-100 disabled:opacity-30 disabled:cursor-not-allowed"
          :disabled="currentPage >= totalPages"
          @click="currentPage++"
        >
          <UIcon name="i-heroicons-chevron-right" class="w-4 h-4" />
        </button>
      </div>
    </div>

    <!-- 商品格線 -->
    <div v-if="products.length > 0" class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
      <ProductCard
        v-for="product in products"
        :key="product.skuId"
        :product="product"
      />
    </div>

    <!-- 無結果 -->
    <div v-else-if="!pending" class="text-center py-24 text-gray-400">
      <UIcon name="i-heroicons-magnifying-glass" class="w-12 h-12 mx-auto mb-3 opacity-30" />
      <p v-if="keyword">找不到「{{ keyword }}」相關商品</p>
      <p v-else>找不到相關商品</p>
    </div>

  </div>
</template>

<script setup lang="ts">
import type { Product } from '~/mocks/products'
import type { ProductSearchQuery, SearchAggregations, SearchItem } from '~/composables/useProductSearch'
import type { FilterState } from '~/components/search/SearchFilter.vue'

const route = useRoute()
const keyword = computed(() => route.query.keyword ? String(route.query.keyword) : '')
const catId = computed(() => route.query.catId ? Number(route.query.catId) : undefined)
const catName = computed(() => route.query.catName ? String(route.query.catName) : '')

const sortBy = ref<'default' | 'new' | 'sale'>('default')
const priceSort = ref<'none' | 'asc' | 'desc'>('none')
const currentPage = ref(1)
const itemsPerPage = 20

const filterState = ref<FilterState>({ brandIds: [], attrs: {} })

const sortOptions = [
  { label: '綜合排名', value: 'default' as const },
  { label: '最新',    value: 'new'     as const },
  { label: '月銷熱賣', value: 'sale'    as const },
]

function setSortBy(value: 'default' | 'new' | 'sale') {
  sortBy.value = value
  priceSort.value = 'none'
  currentPage.value = 1
}

function cyclePriceSort() {
  if (priceSort.value === 'none')     priceSort.value = 'asc'
  else if (priceSort.value === 'asc') priceSort.value = 'desc'
  else                                priceSort.value = 'none'
  currentPage.value = 1
}

function onFilterChange(state: FilterState) {
  filterState.value = state
  currentPage.value = 1
}

watch([keyword, catId], () => { currentPage.value = 1 })

// ── 組後端查詢參數 ──
const searchQuery = computed<ProductSearchQuery>(() => {
  const q: ProductSearchQuery = {
    page: currentPage.value,
    pageSize: itemsPerPage,
  }
  if (keyword.value) q.keyword = keyword.value
  if (catId.value) q.catId = catId.value
  if (filterState.value.brandIds.length > 0) q.brandId = filterState.value.brandIds
  for (const [attrKey, values] of Object.entries(filterState.value.attrs)) {
    if (values.length > 0) q[`attrs[${attrKey}]`] = values
  }

  if (priceSort.value !== 'none') {
    q.sort = 'price'
    q.order = priceSort.value
  } else if (sortBy.value === 'sale') {
    q.sort = 'sale'
    q.order = 'desc'
  }
  // sortBy === 'new' 沒有對應後端欄位（ES 文件沒存建立時間），退化成預設排序

  return q
})

const { data, pending } = useProductSearch(searchQuery)

const total = computed(() => data.value?.total ?? 0)
const aggregations = computed<SearchAggregations>(() => data.value?.aggregations ?? { brands: [], attrs: [] })
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / itemsPerPage)))

// 搜尋結果要在還有貨的商品卡上標「限時搶購」徽章+改顯示秒殺價，跟首頁限時搶購卡邏輯一致：只要這個
// SPU「任一規格」還有名額就算——不能只認搜尋索引到的那一顆 sku，秒殺活動很可能是特賣別的顏色，
// 索引到的代表 sku 沒貨不代表整個商品沒得搶（真實 bug：首頁用 spuId 判斷、這裡原本用 skuId 判斷，
// 兩邊對不上，見使用者實測回報「首頁4個、搜尋頁只有2個」）。
const { data: seckillData } = useSeckill()
const activeSeckillProductMap = computed(() => {
  const map = new Map<number, { seckillPrice: number; originalPrice: number }>()
  for (const session of seckillData.value?.flashSaleSessions ?? []) {
    if (session.status !== 'ACTIVE') continue
    for (const product of session.products) {
      const sku = product.skus.find(s => s.remaining > 0)
      if (sku) map.set(product.spuId, sku)
    }
  }
  return map
})

function toProduct(item: SearchItem): Product {
  const seckillSku = activeSeckillProductMap.value.get(item.spuId)
  return {
    skuId: item.skuId,
    spuId: item.spuId,
    skuName: item.spuName || item.skuName,
    price: seckillSku ? seckillSku.seckillPrice : item.price,
    skuDefaultImg: item.mainImage,
    saleCount: item.saleCount,
    categoryId: 0,
    isSeckill: !!seckillSku,
    originalPrice: seckillSku?.originalPrice,
  }
}

const products = computed(() => (data.value?.items ?? []).map(toProduct))
</script>
