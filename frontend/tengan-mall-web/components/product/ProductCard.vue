<template>
  <NuxtLink :to="`/item/${product.spuId ?? product.skuId}`" class="block h-full">
    <div
      class="flex flex-col h-full bg-card border border-border rounded-xl shadow-card hover:shadow-card-hover hover:-translate-y-0.5 transition-all duration-200 overflow-hidden group"
    >
      <!-- 商品圖片：固定高度，不同圖片比例都用 contain 塞進同一個框，Card 高度不受影響 -->
      <div class="relative h-[190px] shrink-0 bg-background flex items-center justify-center overflow-hidden">
        <img
          :src="product.skuDefaultImg"
          :alt="product.skuName"
          class="max-w-full max-h-full object-contain group-hover:scale-105 transition duration-300"
        />
        <div class="absolute top-2 left-2">
          <slot name="badge">
            <span
              v-if="product.isSeckill"
              class="inline-flex items-center gap-1 bg-danger text-white text-xs font-bold px-1.5 py-0.5 rounded"
            >
              <UIcon name="i-heroicons-bolt" class="w-3 h-3" />
              限時搶購
            </span>
          </slot>
        </div>
      </div>

      <!-- 商品資訊：名稱／價格／原價／次要資訊都固定高度，同一排 Card 底部才會對齊 -->
      <div class="flex flex-col flex-1 p-4">
        <p class="text-sm text-body leading-[1.5] line-clamp-2 h-[42px] mb-2">
          {{ product.skuName }}
        </p>

        <div class="h-7 flex items-baseline gap-1.5">
          <span class="text-danger font-bold text-lg leading-none">
            NT$ {{ product.price.toLocaleString() }}
          </span>
        </div>

        <div class="h-5 mb-1">
          <span v-if="product.isSeckill && product.originalPrice" class="text-muted text-xs line-through">
            NT$ {{ product.originalPrice.toLocaleString() }}
          </span>
        </div>

        <div class="mt-auto h-5 text-xs">
          <slot name="meta">
            <span class="text-muted">已售 {{ product.saleCount.toLocaleString() }}</span>
          </slot>
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
