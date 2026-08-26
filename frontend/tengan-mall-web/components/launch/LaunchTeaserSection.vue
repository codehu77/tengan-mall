<script setup lang="ts">
import type { LaunchTeaserProduct } from '~/composables/useLaunchTeaser'

const props = defineProps<{
  products: LaunchTeaserProduct[]
}>()

// 兩列、一列 5 欄，跟 SeckillSection.vue 的 ROW_SIZE 版面概念一致。
const ROW_SIZE = 10

// 只在掛載時洗牌一次，存進 local ref——避免畫面渲染中途（例如使用者互動觸發 re-render）
// 又重新排一次順序，卡片位置一直跳動。重新整理頁面才會重新洗牌一次。
const shuffled = ref<LaunchTeaserProduct[]>([])
onMounted(() => {
  const arr = [...props.products]
  for (let i = arr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[arr[i], arr[j]] = [arr[j], arr[i]]
  }
  shuffled.value = arr
})
// SSR 階段還沒跑 onMounted，先原始順序渲染，避免首屏是空的；到了 client 才會洗牌一次。
const visibleProducts = computed(() => (shuffled.value.length > 0 ? shuffled.value : props.products).slice(0, ROW_SIZE))
</script>

<template>
  <section class="mb-10">

    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center gap-2">
        <div class="flex items-center gap-2 bg-gray-800 text-white px-3 py-1.5 rounded-lg">
          <UIcon name="i-heroicons-sparkles" class="w-4 h-4" />
          <span class="font-bold text-sm tracking-wide">即將開賣</span>
        </div>
      </div>
      <NuxtLink to="/launch" class="text-sm text-gray-500 hover:text-red-500 flex items-center gap-1 transition-colors shrink-0">
        看更多
        <UIcon name="i-heroicons-chevron-right" class="w-4 h-4" />
      </NuxtLink>
    </div>

    <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
      <div
        v-for="product in visibleProducts"
        :key="product.spuId"
        class="bg-white rounded-xl shadow-sm hover:shadow-md transition overflow-hidden cursor-pointer"
        @click="navigateTo(`/item/${product.spuId}`)"
      >
        <div class="relative aspect-square overflow-hidden bg-gray-50">
          <img :src="product.mainImage" :alt="product.name" class="w-full h-full object-cover" />
        </div>
        <div class="p-3">
          <p class="text-base text-gray-700 line-clamp-2 mb-2 min-h-[2.5rem]">
            {{ product.name }}
          </p>
          <p class="text-gray-700 font-bold text-xl leading-none mb-2">
            NT$ {{ product.price.toLocaleString() }}
          </p>
          <LaunchTeaserBadge :sale-start-time="product.saleStartTime" />
        </div>
      </div>
    </div>

  </section>
</template>
