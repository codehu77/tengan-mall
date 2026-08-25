<template>
  <div class="max-w-7xl mx-auto px-6 py-8">

    <!-- 首頁輪播 Banner -->
    <BannerCarousel v-if="banners.length > 0" :banners="banners" />
    <div
      v-else
      class="bg-red-50 border border-red-100 rounded-xl h-[470px] flex items-center justify-center mb-10"
    >
      <p class="text-red-400 text-xl">尚未設定輪播圖</p>
    </div>

    <!-- 限時搶購 -->
    <SeckillSection v-if="flashSaleSessions.length > 0" :flash-sale-sessions="flashSaleSessions" />

    <!-- 熱門商品 -->
    <section v-if="hotProducts.length > 0">
      <h2 class="text-2xl font-bold text-gray-800 mb-4">熱門商品</h2>
      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
        <ProductCard
          v-for="product in hotProducts"
          :key="product.skuId"
          :product="product"
        />
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import type { Product } from '~/mocks/products'
import type { ProductSearchQuery, SearchItem } from '~/composables/useProductSearch'

useHead({ title: '首頁' })

// 直接把泛用搜尋端點當「銷量前 N 名」用（sort=sale，不帶 keyword/catId），不用新增後端端點。
// 首頁熱門商品區塊不需要 search.vue 那支 toProduct 的秒殺價覆蓋邏輯，用一個簡化版本就好。
const hotQuery = ref<ProductSearchQuery>({ sort: 'sale', order: 'desc', pageSize: 10 })
const { data: hotData } = await useProductSearch(hotQuery)

function toHotProduct(item: SearchItem): Product {
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
const hotProducts = computed(() => (hotData.value?.items ?? []).map(toHotProduct))

const { data: seckillData } = await useSeckill()
const flashSaleSessions = computed(() => seckillData.value?.flashSaleSessions ?? [])

const { data: bannerData } = await useBanners()
const banners = computed(() => bannerData.value?.banners ?? [])
</script>
