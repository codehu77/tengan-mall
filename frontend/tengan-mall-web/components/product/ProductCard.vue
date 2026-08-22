<template>
  <NuxtLink :to="`/item/${product.spuId ?? product.skuId}`" class="block">
    <div class="bg-white rounded-xl shadow-sm hover:shadow-md transition overflow-hidden group">

      <!-- 商品圖片 -->
      <div class="relative aspect-square overflow-hidden bg-gray-50">
        <img
          :src="product.skuDefaultImg"
          :alt="product.skuName"
          class="w-full h-full object-cover group-hover:scale-105 transition duration-300"
        />
        <span
          v-if="product.isSeckill"
          class="absolute top-2 left-2 flex items-center gap-1 bg-red-500 text-white text-xs font-bold px-1.5 py-0.5 rounded"
        >
          <UIcon name="i-heroicons-bolt" class="w-3 h-3" />
          限時搶購
        </span>
      </div>

      <!-- 商品資訊 -->
      <div class="p-3">
        <p class="text-base text-gray-700 line-clamp-2 mb-2 min-h-[2.5rem]">
          {{ product.skuName }}
        </p>
        <div class="flex items-baseline gap-1.5">
          <span class="text-red-600 font-bold text-xl">
            NT$ {{ product.price.toLocaleString() }}
          </span>
          <span v-if="product.isSeckill && product.originalPrice" class="text-gray-400 text-xs line-through">
            NT$ {{ product.originalPrice.toLocaleString() }}
          </span>
        </div>
        <div class="text-sm text-gray-400 mt-1">
          已售 {{ product.saleCount.toLocaleString() }}
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
