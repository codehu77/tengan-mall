<script setup lang="ts">
import type { FlashSaleSession, SeckillProduct, SeckillSku } from '~/composables/useSeckill'
import type { Product } from '~/mocks/products'

const props = defineProps<{
  flashSaleSessions: FlashSaleSession[]
}>()

/** 預設選中 ACTIVE 那一場（現正瘋搶），沒有的話選第一個待開賣場次。 */
const defaultSession = props.flashSaleSessions.find(s => s.status === 'ACTIVE') ?? props.flashSaleSessions[0]
const selectedActivityId = ref(defaultSession?.activityId ?? null)

const currentSession = computed(() =>
  props.flashSaleSessions.find(s => s.activityId === selectedActivityId.value) ?? props.flashSaleSessions[0]
)

/** 一個商品（SPU）一張卡，不是一個規格一張卡；全部規格都賣完的商品不顯示（瀏覽用的預告區，跟商品詳情頁
 * 用同一份原始資料但不同呈現目的——詳情頁要顯示已售完狀態，這裡直接跳過）。實際筆數不固定，用橫向
 * 捲動而不是限制成一排，資料多的時候也不會被裁掉。 */
const visibleProducts = computed(() =>
  (currentSession.value?.products ?? []).filter(p => p.skus.some(s => s.remaining > 0))
)

function selectSession(activityId: number) {
  selectedActivityId.value = activityId
}

function sessionTabLabel(session: FlashSaleSession) {
  const hhmm = new Date(session.startTime).toLocaleTimeString('zh-TW', { hour: '2-digit', minute: '2-digit', hour12: false })
  return session.status === 'ACTIVE' ? `${hhmm} 現正瘋搶` : `${hhmm} 準時開搶`
}

/** ACTIVE 倒數到結束時間；PUBLISHED（還沒開賣）倒數到開賣時間。 */
const countdownTarget = computed(() => {
  const session = currentSession.value
  if (!session) return null
  return session.status === 'ACTIVE' ? session.endTime : session.startTime
})
const { hh, mm, ss } = useCountdown(countdownTarget)

const countdownLabel = computed(() => currentSession.value?.status === 'ACTIVE' ? '距結束' : '距開賣')

/** 卡片代表價：同一活動同一商品理論上共用同一個秒殺價，取第一個還有貨的規格。 */
function representativeSku(product: SeckillProduct) {
  return product.skus.find(s => s.remaining > 0) ?? product.skus[0]
}

function discountLabel(sku: SeckillSku) {
  if (sku.originalPrice <= 0) return ''
  const off = Math.round((1 - sku.seckillPrice / sku.originalPrice) * 10)
  return `${off}折`
}

function totalRemaining(product: SeckillProduct) {
  return product.skus.reduce((sum, s) => sum + s.remaining, 0)
}

/** 轉成共用 ProductCard 吃的資料格狀，不改變任何底層資料，只是換一種形狀給共用元件用。 */
function toCardProduct(product: SeckillProduct): Product {
  const sku = representativeSku(product)
  return {
    skuId: sku.skuId,
    spuId: product.spuId,
    skuName: product.name,
    price: sku.seckillPrice,
    skuDefaultImg: product.mainImage,
    saleCount: 0,
    categoryId: 0,
    isSeckill: true,
    originalPrice: sku.originalPrice,
  }
}
</script>

<template>
  <section v-if="currentSession" class="mb-10">
    <SectionHeader icon="i-heroicons-bolt" title="限時搶購" accent="danger" to="/seckill" toLabel="看更多">
      <!-- 倒數 -->
      <div class="flex items-center gap-1.5 text-sm shrink-0">
        <span class="text-muted text-xs">{{ countdownLabel }}</span>
        <div class="flex items-center gap-1">
          <span class="bg-heading text-white text-xs font-mono px-1.5 py-0.5 rounded">{{ hh }}</span>
          <span class="text-subtle font-bold text-xs">:</span>
          <span class="bg-heading text-white text-xs font-mono px-1.5 py-0.5 rounded">{{ mm }}</span>
          <span class="text-subtle font-bold text-xs">:</span>
          <span class="bg-heading text-white text-xs font-mono px-1.5 py-0.5 rounded">{{ ss }}</span>
        </div>
      </div>

      <!-- 今天有多個場次才顯示切換 tab（現正瘋搶 + 其餘準時開搶） -->
      <div v-if="flashSaleSessions.length > 1" class="flex items-center gap-2">
        <button
          v-for="session in flashSaleSessions"
          :key="session.activityId"
          class="flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-medium transition-all whitespace-nowrap"
          :class="session.activityId === currentSession.activityId
            ? 'border border-danger text-danger bg-danger/10'
            : 'bg-background text-subtle hover:bg-border/60'"
          @click="selectSession(session.activityId)"
        >
          {{ sessionTabLabel(session) }}
        </button>
      </div>
    </SectionHeader>

    <ProductCarousel>
      <div
        v-for="product in visibleProducts"
        :key="product.spuId"
        class="shrink-0 snap-start w-[46%] sm:w-[31%] lg:w-[calc((100%-60px)/6)]"
      >
        <ProductCard :product="toCardProduct(product)">
          <template #badge>
            <span class="inline-flex items-center bg-danger text-white text-xs font-bold px-1.5 py-0.5 rounded">
              {{ discountLabel(representativeSku(product)) }}
            </span>
          </template>
          <template #meta>
            <span :class="currentSession.status === 'ACTIVE' ? 'text-danger' : 'text-muted'">
              {{ currentSession.status === 'ACTIVE' ? `剩餘 ${totalRemaining(product)} 件` : '尚未開賣' }}
            </span>
          </template>
        </ProductCard>
      </div>
    </ProductCarousel>
  </section>
</template>
