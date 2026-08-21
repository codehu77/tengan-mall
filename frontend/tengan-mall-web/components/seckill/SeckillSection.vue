<script setup lang="ts">
import type { SeckillActivity } from '~/composables/useSeckill'

const props = defineProps<{
  activities: SeckillActivity[]
}>()

// 輪播常數：w-48 = 192px, gap-4 = 16px
const VISIBLE = 5
const CARD_STEP = 192 + 16

const selectedActivityId = ref(props.activities[0]?.id ?? null)
const currentIndex = ref(0)

const currentActivity = computed(() =>
  props.activities.find(a => a.id === selectedActivityId.value) ?? props.activities[0]
)

const currentSkus = computed(() => currentActivity.value?.skus ?? [])

const hasPrev = computed(() => currentIndex.value > 0)
const hasNext = computed(() => currentIndex.value + VISIBLE < currentSkus.value.length)

function prev() { if (hasPrev.value) currentIndex.value-- }
function next() { if (hasNext.value) currentIndex.value++ }

function selectActivity(id: number) {
  selectedActivityId.value = id
  currentIndex.value = 0
  updateRemaining()
}

const activityLabel: Record<SeckillActivity['activityType'], string> = {
  FLASH_SALE: '限時搶購',
  LAUNCH: '首發',
}

const remaining = ref(0)

function updateRemaining() {
  const activity = currentActivity.value
  if (!activity) return
  remaining.value = Math.max(0, Math.floor((new Date(activity.endTime).getTime() - Date.now()) / 1000))
}

const hh = computed(() => String(Math.floor(remaining.value / 3600)).padStart(2, '0'))
const mm = computed(() => String(Math.floor((remaining.value % 3600) / 60)).padStart(2, '0'))
const ss = computed(() => String(remaining.value % 60).padStart(2, '0'))

function discountLabel(sku: { seckillPrice: number; originalPrice: number }) {
  if (sku.originalPrice <= 0) return ''
  const off = Math.round((1 - sku.seckillPrice / sku.originalPrice) * 10)
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

<template>
  <section v-if="currentActivity" class="mb-10">

    <!-- 標題列 -->
    <div class="flex items-center gap-4 mb-4">

      <!-- 左：badge + 倒計時 -->
      <div class="flex items-center gap-3 shrink-0">
        <div class="flex items-center gap-2 bg-red-500 text-white px-3 py-1.5 rounded-lg">
          <UIcon name="i-heroicons-bolt" class="w-4 h-4" />
          <span class="font-bold text-sm tracking-wide">{{ activityLabel[currentActivity.activityType] }}</span>
        </div>
        <div class="flex items-center gap-1.5 text-sm">
          <span class="text-gray-400 text-xs">距結束</span>
          <div class="flex items-center gap-1">
            <span class="bg-gray-800 text-white text-xs font-mono px-1.5 py-0.5 rounded">{{ hh }}</span>
            <span class="text-gray-500 font-bold text-xs">:</span>
            <span class="bg-gray-800 text-white text-xs font-mono px-1.5 py-0.5 rounded">{{ mm }}</span>
            <span class="text-gray-500 font-bold text-xs">:</span>
            <span class="bg-gray-800 text-white text-xs font-mono px-1.5 py-0.5 rounded">{{ ss }}</span>
          </div>
        </div>
      </div>

      <!-- 中：多場活動同時進行時才顯示切換 tab -->
      <div v-if="activities.length > 1" class="flex items-center gap-2 flex-1">
        <button
          v-for="activity in activities"
          :key="activity.id"
          class="flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-medium transition-all whitespace-nowrap"
          :class="activity.id === currentActivity.id
            ? 'border border-red-500 text-red-500 bg-red-50'
            : 'bg-gray-100 text-gray-500 hover:bg-gray-200'"
          @click="selectActivity(activity.id)"
        >
          {{ activityLabel[activity.activityType] }}
        </button>
      </div>
      <div v-else class="flex-1" />

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
            v-for="sku in currentSkus"
            :key="sku.skuId"
            class="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow cursor-pointer shrink-0 w-48 overflow-hidden"
            @click="navigateTo(`/item/${sku.spuId}`)"
          >
            <!-- 商品圖 -->
            <div class="relative">
              <img
                :src="sku.mainImage"
                :alt="sku.name"
                class="w-full h-48 object-cover"
              />
              <span class="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-1.5 py-0.5 rounded">
                {{ discountLabel(sku) }}
              </span>
            </div>

            <!-- 商品資訊 -->
            <div class="p-3">
              <p class="text-sm text-gray-700 line-clamp-2 mb-2 leading-relaxed">
                {{ sku.name }}
              </p>
              <p class="text-red-500 font-bold text-lg leading-none mb-1">
                NT$ {{ sku.seckillPrice.toLocaleString() }}
              </p>
              <p class="text-gray-400 text-xs line-through mb-2">
                NT$ {{ sku.originalPrice.toLocaleString() }}
              </p>
              <p class="text-xs text-gray-500">
                剩餘 <span class="font-semibold text-gray-700">{{ sku.remaining }}</span> 件
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
