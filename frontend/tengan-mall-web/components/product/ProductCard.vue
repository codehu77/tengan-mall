<template>
  <NuxtLink :to="`/item/${product.skuId}`" class="block">
    <div class="bg-white rounded-xl shadow-sm hover:shadow-md transition overflow-hidden group">

      <!-- 商品圖片 -->
      <div class="aspect-square overflow-hidden bg-gray-50">
        <img
          :src="product.skuDefaultImg"
          :alt="product.skuName"
          class="w-full h-full object-cover group-hover:scale-105 transition duration-300"
        />
      </div>

      <!-- 商品資訊 -->
      <div class="p-3">
        <p class="text-base text-gray-700 line-clamp-2 mb-2 min-h-[2.5rem]">
          {{ product.skuName }}
        </p>
        <div class="flex items-baseline justify-between">
          <span class="text-red-600 font-bold text-xl">
            NT$ {{ product.price.toLocaleString() }}
          </span>
          <span class="text-sm text-gray-400">
            已售 {{ product.saleCount.toLocaleString() }}
          </span>
        </div>
      </div>

    </div>
  </NuxtLink>
</template>

<script setup lang="ts">
import type { Product } from '~/mocks/products'

defineProps<{
  product: Product
}>()
</script>
