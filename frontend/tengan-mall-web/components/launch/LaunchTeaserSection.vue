<script setup lang="ts">
import type { LaunchTeaserProduct } from '~/composables/useLaunchTeaser'
import type { Product } from '~/mocks/products'

const props = defineProps<{
  products: LaunchTeaserProduct[]
}>()

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
// 實際筆數不固定，全部交給 ProductCarousel 橫向捲動，不用切成兩排。
const visibleProducts = computed(() => (shuffled.value.length > 0 ? shuffled.value : props.products))

function toCardProduct(product: LaunchTeaserProduct): Product {
  return {
    skuId: product.spuId,
    spuId: product.spuId,
    skuName: product.name,
    price: product.price,
    skuDefaultImg: product.mainImage,
    saleCount: 0,
    categoryId: 0,
  }
}
</script>

<template>
  <section class="mb-12">
    <SectionHeader icon="i-heroicons-sparkles" title="即將開賣" accent="success" to="/launch" toLabel="看更多" />

    <ProductCarousel>
      <div
        v-for="product in visibleProducts"
        :key="product.spuId"
        class="shrink-0 snap-start w-[46%] sm:w-[31%] lg:w-[19%]"
      >
        <ProductCard :product="toCardProduct(product)">
          <template #badge>
            <span class="inline-flex items-center bg-success text-white text-xs font-bold px-1.5 py-0.5 rounded">
              預告
            </span>
          </template>
          <template #meta>
            <LaunchTeaserBadge :sale-start-time="product.saleStartTime" />
          </template>
        </ProductCard>
      </div>
    </ProductCarousel>
  </section>
</template>
