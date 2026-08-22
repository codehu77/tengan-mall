<template>
  <div class="max-w-7xl mx-auto px-6 py-8">

    <!-- Banner 佔位 -->
    <div class="bg-red-50 border border-red-100 rounded-xl h-[280px] flex items-center justify-center mb-10">
      <p class="text-red-400 text-xl">Banner 區域（待設計）</p>
    </div>

    <!-- 限時搶購 -->
    <SeckillSection v-if="flashSaleSessions.length > 0" :flash-sale-sessions="flashSaleSessions" />

    <!-- 熱門商品 -->
    <section>
      <h2 class="text-2xl font-bold text-gray-800 mb-4">熱門商品</h2>
      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-6">
        <ProductCard
          v-for="product in mockProducts"
          :key="product.skuId"
          :product="product"
        />
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { MOCK_PRODUCTS } from '~/mocks/products'

// 首頁「熱門商品」區塊維持 mock，不在這次秒殺前台整合範圍內（後端還沒有對應的推薦商品端點）。
const mockProducts = MOCK_PRODUCTS

const { data: seckillData } = await useSeckill()
const flashSaleSessions = computed(() => seckillData.value?.flashSaleSessions ?? [])
</script>
