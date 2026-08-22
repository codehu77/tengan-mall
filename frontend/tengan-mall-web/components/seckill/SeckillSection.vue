<script setup lang="ts">
import type { FlashSaleSession, SeckillProduct } from '~/composables/useSeckill'

const props = defineProps<{
  flashSaleSessions: FlashSaleSession[]
}>()

// 輪播常數：w-48 = 192px, gap-4 = 16px
const VISIBLE = 5
const CARD_STEP = 192 + 16

/** 預設選中 ACTIVE 那一場（現正瘋搶），沒有的話選第一個待開賣場次。 */
const defaultSession = props.flashSaleSessions.find(s => s.status === 'ACTIVE') ?? props.flashSaleSessions[0]
const selectedActivityId = ref(defaultSession?.activityId ?? null)
const currentIndex = ref(0)

const currentSession = computed(() =>
  props.flashSaleSessions.find(s => s.activityId === selectedActivityId.value) ?? props.flashSaleSessions[0]
)

/** 一個商品（SPU）一張卡，不是一個規格一張卡；全部規格都賣完的商品不顯示（瀏覽用的輪播，跟商品詳情頁
 * 用同一份原始資料但不同呈現目的——詳情頁要顯示已售完狀態，這裡直接跳過）。 */
const visibleProducts = computed(() =>
  (currentSession.value?.products ?? []).filter(p => p.skus.some(s => s.remaining > 0))
)

const hasPrev = computed(() => currentIndex.value > 0)
const hasNext = computed(() => currentIndex.value + VISIBLE < visibleProducts.value.length)

function prev() { if (hasPrev.value) currentIndex.value-- }
function next() { if (hasNext.value) currentIndex.value++ }

function selectSession(activityId: number) {
  selectedActivityId.value = activityId
  currentIndex.value = 0
  updateRemaining()
}

function sessionTabLabel(session: FlashSaleSession) {
  const hhmm = new Date(session.startTime).toLocaleTimeString('zh-TW', { hour: '2-digit', minute: '2-digit', hour12: false })
  return session.status === 'ACTIVE' ? `${hhmm} 現正瘋搶` : `${hhmm} 準時開搶`
}

/** ACTIVE 倒數到結束時間；PUBLISHED（還沒開賣）倒數到開賣時間。 */
const remaining = ref(0)

function updateRemaining() {
  const session = currentSession.value
  if (!session) return
  const target = session.status === 'ACTIVE' ? session.endTime : session.startTime
  remaining.value = Math.max(0, Math.floor((new Date(target).getTime() - Date.now()) / 1000))
}

const countdownLabel = computed(() => currentSession.value?.status === 'ACTIVE' ? '距結束' : '距開賣')

const hh = computed(() => String(Math.floor(remaining.value / 3600)).padStart(2, '0'))
const mm = computed(() => String(Math.floor((remaining.value % 3600) / 60)).padStart(2, '0'))
const ss = computed(() => String(remaining.value % 60).padStart(2, '0'))

/** 卡片代表價：同一活動同一商品理論上共用同一個秒殺價，取第一個還有貨的規格。 */
function representativeSku(product: SeckillProduct) {
  return product.skus.find(s => s.remaining > 0) ?? product.skus[0]
}

function discountLabel(sku: { seckillPrice: number; originalPrice: number }) {
  if (sku.originalPrice <= 0) return ''
  const off = Math.round((1 - sku.seckillPrice / sku.originalPrice) * 10)
  return `${off}折`
}

function totalRemaining(product: SeckillProduct) {
  return product.skus.reduce((sum, s) => sum + s.remaining, 0)
}

let timer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  updateRemaining()
  timer = setInterval(updateRemaining, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<template>
  <section v-if="currentSession" class="mb-10">

    <!-- 標題列 -->
    <div class="flex items-center gap-4 mb-4">

      <!-- 左：badge + 倒計時 -->
      <div class="flex items-center gap-3 shrink-0">
        <div class="flex items-center gap-2 bg-red-500 text-white px-3 py-1.5 rounded-lg">
          <UIcon name="i-heroicons-bolt" class="w-4 h-4" />
          <span class="font-bold text-sm tracking-wide">限時搶購</span>
        </div>
        <div class="flex items-center gap-1.5 text-sm">
          <span class="text-gray-400 text-xs">{{ countdownLabel }}</span>
          <div class="flex items-center gap-1">
            <span class="bg-gray-800 text-white text-xs font-mono px-1.5 py-0.5 rounded">{{ hh }}</span>
            <span class="text-gray-500 font-bold text-xs">:</span>
            <span class="bg-gray-800 text-white text-xs font-mono px-1.5 py-0.5 rounded">{{ mm }}</span>
            <span class="text-gray-500 font-bold text-xs">:</span>
            <span class="bg-gray-800 text-white text-xs font-mono px-1.5 py-0.5 rounded">{{ ss }}</span>
          </div>
        </div>
      </div>

      <!-- 中：今天有多個場次才顯示切換 tab（現正瘋搶 + 其餘準時開搶） -->
      <div v-if="flashSaleSessions.length > 1" class="flex items-center gap-2 flex-1 overflow-x-auto">
        <button
          v-for="session in flashSaleSessions"
          :key="session.activityId"
          class="flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-medium transition-all whitespace-nowrap"
          :class="session.activityId === currentSession.activityId
            ? 'border border-red-500 text-red-500 bg-red-50'
            : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
          @click="selectSession(session.activityId)"
        >
          {{ sessionTabLabel(session) }}
        </button>
      </div>
      <div v-else class="flex-1" />

      <!-- 右：看更多 -->
      <NuxtLink to="/seckill" class="text-sm text-gray-500 hover:text-red-500 flex items-center gap-1 transition-colors shrink-0">
        看更多
        <UIcon name="i-heroicons-chevron-right" class="w-4 h-4" />
      </NuxtLink>

    </div>

    <!-- 商品輪播：一個商品一張卡 -->
    <div class="relative">

      <!-- 左箭頭 -->
      <button
        v-if="hasPrev"
        class="absolute left-0 top-1/2 -translate-x-1/2 -translate-y-1/2 z-10 w-9 h-9 bg-white shadow-md rounded-full flex items-center justify-center hover:bg-gray-50 hover:scale-125 transition-all duration-200 border border-gray-100"
        @click="prev"
      >
        <UIcon name="i-heroicons-chevron-left" class="w-5 h-5 text-gray-500" />
      </button>

      <div class="overflow-hidden">
        <div
          class="flex gap-4 transition-transform duration-300"
          :style="{ transform: `translateX(-${currentIndex * CARD_STEP}px)` }"
        >
          <div
            v-for="product in visibleProducts"
            :key="product.spuId"
            class="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow cursor-pointer shrink-0 w-48 overflow-hidden"
            @click="navigateTo(`/item/${product.spuId}`)"
          >
            <!-- 商品圖 -->
            <div class="relative">
              <img
                :src="product.mainImage"
                :alt="product.name"
                class="w-full h-48 object-cover"
              />
              <span class="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-1.5 py-0.5 rounded">
                {{ discountLabel(representativeSku(product)) }}
              </span>
            </div>

            <!-- 商品資訊：標題固定保留兩行高度，卡片內容高度才會一致，不會因標題長短而參差不齊 -->
            <div class="p-3">
              <p class="text-sm text-gray-700 line-clamp-2 mb-2 min-h-[2.5rem] leading-5">
                {{ product.name }}
              </p>
              <p class="text-red-500 font-bold text-lg leading-none mb-1">
                NT$ {{ representativeSku(product).seckillPrice.toLocaleString() }}
              </p>
              <p class="text-gray-400 text-xs line-through mb-2">
                NT$ {{ representativeSku(product).originalPrice.toLocaleString() }}
              </p>
              <span
                class="inline-block text-xs px-2 py-0.5 rounded-full"
                :class="currentSession.status === 'ACTIVE' ? 'bg-red-50 text-red-500' : 'bg-gray-100 text-gray-400'"
              >
                {{ currentSession.status === 'ACTIVE' ? `剩餘 ${totalRemaining(product)} 件` : '尚未開賣' }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右箭頭 -->
      <button
        v-if="hasNext"
        class="absolute right-0 top-1/2 translate-x-1/2 -translate-y-1/2 z-10 w-9 h-9 bg-white shadow-md rounded-full flex items-center justify-center hover:bg-gray-50 hover:scale-125 transition-all duration-200 border border-gray-100"
        @click="next"
      >
        <UIcon name="i-heroicons-chevron-right" class="w-5 h-5 text-gray-500" />
      </button>

    </div>

  </section>
</template>
