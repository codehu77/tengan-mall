<template>
  <div class="max-w-7xl mx-auto px-4 py-6">

    <!-- 搜尋標題 -->
    <div class="mb-4">
      <h1 class="text-lg text-gray-700">
        搜尋「<span class="font-bold text-gray-900">{{ keyword }}</span>」的結果
        <span class="text-sm text-gray-400 ml-2">共 {{ sortedProducts.length }} 件商品</span>
      </h1>
    </div>

    <!-- 進階篩選 -->
    <SearchFilter v-if="sortedProducts.length > 0" @change="onFilterChange" />

    <!-- 排序列 -->
    <div v-if="sortedProducts.length > 0" class="flex items-center gap-2 mb-4 bg-white rounded-lg px-4 py-2 shadow-sm">
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
    <div v-if="paginatedProducts.length > 0" class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
      <ProductCard
        v-for="product in paginatedProducts"
        :key="product.skuId"
        :product="product"
      />
    </div>

    <!-- 無結果 -->
    <div v-else class="text-center py-24 text-gray-400">
      <UIcon name="i-heroicons-magnifying-glass" class="w-12 h-12 mx-auto mb-3 opacity-30" />
      <p>找不到「{{ keyword }}」相關商品</p>
    </div>

  </div>
</template>

<script setup lang="ts">
import { MOCK_PRODUCTS } from '~/mocks/products'
import type { FilterState } from '~/components/search/SearchFilter.vue'

const route = useRoute()
const keyword = computed(() => String(route.query.keyword ?? ''))

const sortBy = ref<'default' | 'new' | 'sale'>('default')
const priceSort = ref<'none' | 'asc' | 'desc'>('none')
const currentPage = ref(1)
const itemsPerPage = 20

const filterState = ref<FilterState>({
  categories: [],
  brands: [],
  types: [],
  styles: [],
})

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

const sortedProducts = computed(() => {
  let result = MOCK_PRODUCTS.filter(p =>
    keyword.value ? p.skuName.includes(keyword.value) : true
  )

  if (priceSort.value === 'asc')                            result = [...result].sort((a, b) => a.price - b.price)
  else if (priceSort.value === 'desc')                      result = [...result].sort((a, b) => b.price - a.price)
  else if (sortBy.value === 'sale')                         result = [...result].sort((a, b) => b.saleCount - a.saleCount)
  else if (sortBy.value === 'new')                          result = [...result].reverse()

  return result
})

const totalPages = computed(() => Math.max(1, Math.ceil(sortedProducts.value.length / itemsPerPage)))

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return sortedProducts.value.slice(start, start + itemsPerPage)
})

watch([keyword, filterState], () => { currentPage.value = 1 })
</script>
