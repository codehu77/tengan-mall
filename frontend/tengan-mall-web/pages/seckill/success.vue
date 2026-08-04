<template>
  <div class="bg-gray-100 min-h-screen py-6">
    <div class="max-w-2xl mx-auto px-6 space-y-4">

      <!-- 成功 Banner -->
      <div class="bg-white rounded-lg p-8 flex flex-col items-center text-center">
        <div class="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mb-4">
          <UIcon name="i-heroicons-check-circle" class="w-10 h-10 text-green-500" />
        </div>
        <h1 class="text-2xl font-bold text-gray-800 mb-1">恭喜搶購成功！</h1>
        <p class="text-sm text-gray-400">{{ order.sessionName }} · 限量商品已為您保留</p>
      </div>

      <!-- 倒計時提示 -->
      <div
        class="rounded-lg px-5 py-4 flex items-center gap-3 transition-colors"
        :class="isExpired ? 'bg-gray-100 border border-gray-200' : 'bg-orange-50 border border-orange-200'"
      >
        <UIcon
          name="i-heroicons-clock"
          class="w-5 h-5 shrink-0"
          :class="isExpired ? 'text-gray-400' : 'text-orange-400'"
        />
        <div class="text-sm" :class="isExpired ? 'text-gray-500' : 'text-orange-700'">
          <template v-if="isExpired">
            付款時間已截止，訂單已自動取消
          </template>
          <template v-else>
            請在
            <span class="font-mono font-bold text-base" :class="countdown <= 180 ? 'text-red-600' : 'text-orange-600'">
              {{ countdownDisplay }}
            </span>
            內完成付款，逾時訂單將自動取消
          </template>
        </div>
      </div>

      <!-- 商品資訊 -->
      <div class="bg-white rounded-lg p-6">
        <h2 class="text-sm font-semibold text-gray-700 mb-4">搶購商品</h2>
        <div class="flex items-center gap-4">
          <img :src="order.skuImage" :alt="order.skuName" class="w-20 h-20 rounded border border-gray-100 object-cover" />
          <div class="flex-1">
            <p class="text-sm text-gray-700 line-clamp-2 mb-2">{{ order.skuName }}</p>
            <div class="flex items-baseline gap-2">
              <span class="text-xl font-bold text-red-600">NT$ {{ order.seckillPrice.toLocaleString() }}</span>
              <span class="text-xs text-gray-400 line-through">原價 NT$ {{ order.originalPrice.toLocaleString() }}</span>
            </div>
            <p class="text-xs text-gray-400 mt-1">× {{ order.count }}</p>
          </div>
          <div class="text-right shrink-0">
            <p class="text-xs text-orange-500 bg-orange-50 px-2 py-0.5 rounded font-medium">省 NT$ {{ (order.originalPrice - order.seckillPrice).toLocaleString() }}</p>
          </div>
        </div>
      </div>

      <!-- 訂單資訊 -->
      <div class="bg-white rounded-lg p-6 space-y-3">
        <h2 class="text-sm font-semibold text-gray-700 mb-1">訂單資訊</h2>
        <div class="flex justify-between text-sm">
          <span class="text-gray-400">訂單編號</span>
          <span class="font-mono text-gray-700">{{ order.orderSn }}</span>
        </div>
        <div class="flex justify-between text-sm">
          <span class="text-gray-400">下單時間</span>
          <span class="text-gray-700">{{ formatTime(order.createTime) }}</span>
        </div>
        <div class="border-t border-gray-100 pt-3 flex justify-between text-sm font-semibold">
          <span class="text-gray-700">應付金額</span>
          <span class="text-red-600 text-base">NT$ {{ (order.seckillPrice * order.count).toLocaleString() }}</span>
        </div>
      </div>

      <!-- 操作按鈕 -->
      <div class="space-y-3">
        <button
          class="w-full h-14 rounded-lg font-medium text-base transition"
          :class="isExpired
            ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
            : 'bg-red-500 text-white hover:bg-red-600'"
          :disabled="isExpired"
          @click="goToPay"
        >
          {{ isExpired ? '訂單已取消' : '立即付款' }}
        </button>
        <button
          class="w-full h-12 rounded-lg font-medium text-sm text-gray-600 bg-white border border-gray-200 hover:border-gray-300 transition"
          @click="navigateTo('/order/list')"
        >
          查看訂單列表
        </button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { MOCK_SECKILL_ORDER } from '~/mocks/seckill'

definePageMeta({ middleware: 'auth' })

const route = useRoute()
const orderSn = computed(() => route.query.orderSn as string || MOCK_SECKILL_ORDER.orderSn)

const order = { ...MOCK_SECKILL_ORDER, orderSn: orderSn.value }

// 15 分鐘倒計時（900 秒）
const PAYMENT_LIMIT = 15 * 60
const countdown = ref(PAYMENT_LIMIT)
const isExpired = computed(() => countdown.value <= 0)

const countdownDisplay = computed(() => {
  const m = Math.floor(countdown.value / 60).toString().padStart(2, '0')
  const s = (countdown.value % 60).toString().padStart(2, '0')
  return `${m}:${s}`
})

let timer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      clearInterval(timer!)
    }
  }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

function formatTime(iso: string) {
  return new Date(iso).toLocaleString('zh-TW', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit', second: '2-digit',
    hour12: false,
  })
}

function goToPay() {
  navigateTo(`/order/pay?orderSn=${order.orderSn}`)
}
</script>
