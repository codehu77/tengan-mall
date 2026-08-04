<template>
  <section class="mb-10">

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

      <!-- 中：搶購時段 -->
      <div class="flex items-center gap-2 flex-1">
        <button
          v-for="session in activeSessions"
          :key="session.sessionId"
          class="flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-medium transition-all whitespace-nowrap"
          :class="session.active
            ? 'border border-red-500 text-red-500 bg-red-50'
            : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
          @click="selectSession(session.sessionId)"
        >
          <span class="font-mono">{{ session.time }}</span>
          <span>{{ session.label }}</span>
        </button>
      </div>

      <!-- 右：看更多 -->
      <NuxtLink to="/seckill" class="text-sm text-gray-500 hover:text-red-500 flex items-center gap-1 transition-colors shrink-0">
        看更多
        <UIcon name="i-heroicons-chevron-right" class="w-4 h-4" />
      </NuxtLink>

    </div>

    <!-- 商品輪播 -->
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
            v-for="item in currentSessionItems"
            :key="item.id"
            class="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow cursor-pointer shrink-0 w-48 overflow-hidden"
            @click="navigateTo(`/item/${item.skuId}`)"
          >
            <!-- 商品圖 -->
            <div class="relative">
              <img
                :src="item.skuInfoVo.skuDefaultImg"
                :alt="item.skuInfoVo.skuTitle"
                class="w-full h-48 object-cover"
              />
              <span class="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-1.5 py-0.5 rounded">
                {{ discountLabel(item) }}
              </span>
            </div>

            <!-- 商品資訊 -->
            <div class="p-3">
              <p class="text-sm text-gray-700 line-clamp-2 mb-2 leading-relaxed">
                {{ item.skuInfoVo.skuName }}
              </p>
              <p class="text-red-500 font-bold text-lg leading-none mb-1">
                NT$ {{ item.seckillPrice.toLocaleString() }}
              </p>
              <p class="text-gray-400 text-xs line-through mb-2">
                NT$ {{ item.skuInfoVo.price.toLocaleString() }}
              </p>
              <p class="text-xs text-gray-500">
                剩餘 <span class="font-semibold text-gray-700">{{ item.seckillCount }}</span> 件
              </p>
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

<script setup lang="ts">
import type { SeckillItem, SeckillSession } from '~/mocks/seckill'

const props = defineProps<{
  itemsBySession: Record<number, SeckillItem[]>
  sessions: SeckillSession[]
}>()

// 輪播常數：w-48 = 192px, gap-4 = 16px
const VISIBLE = 5
const CARD_STEP = 192 + 16  // 每次移動一張卡片的寬度

const activeSessions = ref(props.sessions.map(s => ({ ...s })))
const currentIndex = ref(0)

const currentSession = computed(() =>
  activeSessions.value.find(s => s.active) ?? activeSessions.value[0]
)

const currentSessionItems = computed(() => {
  const id = currentSession.value?.sessionId ?? 1
  return props.itemsBySession[id] ?? []
})

const hasPrev = computed(() => currentIndex.value > 0)
const hasNext = computed(() => currentIndex.value + VISIBLE < currentSessionItems.value.length)

function prev() { if (hasPrev.value) currentIndex.value-- }
function next() { if (hasNext.value) currentIndex.value++ }

const countdownLabel = computed(() =>
  currentSession.value.status === 'ongoing' ? '距結束' : '距開始'
)

const remaining = ref(0)

function updateRemaining() {
  const s = currentSession.value
  const target = s.status === 'ongoing' ? s.endTime : s.startTime
  remaining.value = Math.max(0, Math.floor((target - Date.now()) / 1000))
}

function selectSession(id: number) {
  activeSessions.value.forEach(s => { s.active = s.sessionId === id })
  currentIndex.value = 0
  updateRemaining()
}

const hh = computed(() => String(Math.floor(remaining.value / 3600)).padStart(2, '0'))
const mm = computed(() => String(Math.floor((remaining.value % 3600) / 60)).padStart(2, '0'))
const ss = computed(() => String(remaining.value % 60).padStart(2, '0'))

function discountLabel(item: SeckillItem) {
  const off = Math.round((1 - item.seckillPrice / item.skuInfoVo.price) * 10)
  return `${off}折`
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

<style scoped>
</style>
