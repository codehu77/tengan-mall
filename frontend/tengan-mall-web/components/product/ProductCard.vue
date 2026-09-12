<template>
  <NuxtLink :to="`/item/${product.spuId ?? product.skuId}`" class="block h-full">
    <div
      class="flex flex-col h-full bg-card border border-border rounded-xl shadow-card hover:shadow-card-hover hover:-translate-y-0.5 transition-all duration-200 overflow-hidden group"
    >
      <!-- 商品圖片：固定高度，滿版鋪滿(cover)不留白邊；不同圖片比例都裁切塞進同一個框，Card 高度不受影響 -->
      <div class="relative h-[170px] shrink-0 bg-background overflow-hidden">
        <img
          :src="product.skuDefaultImg"
          :alt="product.skuName"
          class="w-full h-full object-cover group-hover:scale-105 transition duration-300"
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
      <div class="flex flex-col flex-1 p-3">
        <p ref="nameEl" class="text-sm text-body leading-[1.4] h-10 mb-1.5 overflow-hidden break-all" :title="product.skuName">
          {{ displayName }}
        </p>

        <div class="h-5 flex items-baseline">
          <span class="text-danger font-bold text-lg leading-none">
            NT$ {{ product.price.toLocaleString() }}<template v-if="product.maxPrice && product.maxPrice > product.price"> 起</template>
          </span>
        </div>

        <div class="h-4 leading-none">
          <span v-if="product.isSeckill && product.originalPrice" class="text-muted text-xs line-through">
            NT$ {{ product.originalPrice.toLocaleString() }}
          </span>
        </div>

        <div class="mt-auto h-4 text-xs">
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

const props = defineProps<{
  product: Product
}>()

// 商品名稱要把兩行都填滿（中英混排時單純用 line-clamp 常常因為英文單字不能斷行，
// 硬把整個單字擠到第二行，導致第一行留一大截空白）——改成量測實際渲染高度，
// 超過兩行時只把最後 3 個字元換成「...」，盡量維持兩行都是滿的。
const nameEl = ref<HTMLElement | null>(null)
const displayName = ref(props.product.skuName)

function computeDisplayName() {
  const el = nameEl.value
  const full = props.product.skuName
  if (!el) {
    displayName.value = full
    return
  }

  const clone = el.cloneNode() as HTMLElement
  clone.style.position = 'absolute'
  clone.style.visibility = 'hidden'
  clone.style.pointerEvents = 'none'
  clone.style.height = 'auto'
  clone.style.width = `${el.clientWidth}px`
  document.body.appendChild(clone)

  const maxHeight = el.clientHeight
  const fits = (text: string) => {
    clone.textContent = text
    return clone.scrollHeight <= maxHeight + 1
  }

  if (fits(full)) {
    displayName.value = full
  } else {
    let lo = 0
    let hi = full.length
    while (lo < hi) {
      const mid = Math.ceil((lo + hi) / 2)
      if (fits(full.slice(0, mid))) lo = mid
      else hi = mid - 1
    }
    displayName.value = full.slice(0, Math.max(0, lo - 3)) + '...'
  }

  document.body.removeChild(clone)
}

if (import.meta.client) {
  onMounted(() => {
    computeDisplayName()
    window.addEventListener('resize', computeDisplayName)
  })
  onBeforeUnmount(() => {
    window.removeEventListener('resize', computeDisplayName)
  })
}
watch(() => props.product.skuName, async () => {
  await nextTick()
  computeDisplayName()
})
</script>
