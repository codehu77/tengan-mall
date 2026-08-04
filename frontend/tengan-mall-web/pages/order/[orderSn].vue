<template>
  <div class="bg-gray-100 min-h-screen py-6">
    <div class="max-w-7xl mx-auto px-6">

      <!-- 頁首 -->
      <div class="flex items-center gap-3 mb-4">
        <button class="text-gray-400 hover:text-gray-600 transition" @click="navigateTo('/order/list')">
          <UIcon name="i-heroicons-arrow-left" class="w-5 h-5" />
        </button>
        <h1 class="text-2xl font-semibold text-gray-800">訂單詳情</h1>
        <span class="text-base text-gray-400 font-mono">{{ order?.orderSn }}</span>
        <UBadge v-if="order" :color="ORDER_STATUS[order.status].color as any" variant="solid" size="md" class="ml-1">
          {{ ORDER_STATUS[order.status].label }}
        </UBadge>
      </div>

      <div v-if="order" class="flex gap-6 items-start">

        <!-- 左欄 -->
        <div class="flex-1 space-y-4 min-w-0">

          <!-- Block 1: 物流狀態 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">物流狀態</h2>
            <div class="border-t border-gray-100 pt-6">
              <div v-if="order.status === 4" class="flex items-center gap-3">
                <UIcon name="i-heroicons-x-circle" class="w-6 h-6 text-gray-300 shrink-0" />
                <div>
                  <p class="text-base text-gray-500">訂單不成立</p>
                  <p v-if="order.cancelReason" class="text-sm text-gray-400 mt-0.5">原因：{{ order?.cancelReason }}</p>
                </div>
              </div>
              <div v-else class="relative flex items-start justify-between">
                <!-- 連接線 -->
                <div class="absolute top-4 left-0 right-0 h-0.5 bg-gray-200 z-0" />
                <div
                  class="absolute top-4 left-0 h-0.5 bg-red-400 z-0 transition-all"
                  :style="{ width: progressWidth }"
                />
                <!-- 步驟點 -->
                <div v-for="(step, i) in steps" :key="i" class="relative z-10 flex flex-col items-center gap-2 flex-1">
                  <div
                    class="w-8 h-8 rounded-full flex items-center justify-center border-2 transition"
                    :class="i <= currentStep
                      ? 'bg-red-500 border-red-500 text-white'
                      : 'bg-white border-gray-300 text-gray-300'"
                  >
                    <UIcon v-if="i < currentStep" name="i-heroicons-check" class="w-4 h-4" />
                    <span v-else class="text-xs font-bold">{{ i + 1 }}</span>
                  </div>
                  <p class="text-sm text-center font-medium" :class="i <= currentStep ? 'text-gray-700' : 'text-gray-400'">
                    {{ step }}
                  </p>
                  <p v-if="i <= currentStep" class="text-xs text-gray-400 text-center leading-tight">
                    {{ stepTimes[i] }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Block 2: 商品明細 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">商品明細</h2>
            <div class="border-t border-gray-100 pt-4 space-y-4">
              <div v-for="item in order?.items" :key="item.skuId" class="flex items-center gap-4">
                <img :src="item.image" :alt="item.skuName" class="w-20 h-20 rounded border border-gray-100 object-cover shrink-0" />
                <div class="flex-1 min-w-0">
                  <p class="text-base text-gray-700 line-clamp-2">{{ item.skuName }}</p>
                  <p class="text-sm text-gray-400 mt-1">× {{ item.count }}</p>
                </div>
                <p class="text-base font-medium text-gray-800 shrink-0">NT$ {{ (item.price * item.count).toLocaleString() }}</p>
              </div>
            </div>
          </div>

          <!-- Block 3: 收件人資訊 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">收件人資訊</h2>
            <div class="border-t border-gray-100 pt-4 space-y-3">
              <div class="flex gap-2 text-base">
                <span class="text-gray-400 w-16 shrink-0">姓名</span>
                <span class="text-gray-700">{{ order?.receiverName }}</span>
              </div>
              <div class="flex gap-2 text-base">
                <span class="text-gray-400 w-16 shrink-0">手機</span>
                <span class="text-gray-700">{{ order?.receiverPhone }}</span>
              </div>
              <div class="flex gap-2 text-base">
                <span class="text-gray-400 w-16 shrink-0">地址</span>
                <span class="text-gray-700">{{ order?.receiverAddress }}</span>
              </div>
            </div>
          </div>

          <!-- Block 4: 付款方式 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">付款方式</h2>
            <div class="border-t border-gray-100 pt-4 flex items-center gap-3">
              <UIcon :name="paymentIcon" class="w-5 h-5 text-gray-500" />
              <span class="text-base text-gray-700">{{ paymentLabel }}</span>
            </div>
          </div>

        </div>

        <!-- 右欄：結帳明細 -->
        <div class="w-[420px] shrink-0 sticky top-6">
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">結帳明細</h2>
            <div class="border-t border-gray-100 pt-4 space-y-4">

              <div class="flex justify-between items-center text-base text-gray-700">
                <span>商品原價總金額</span>
                <span class="font-semibold">${{ subtotal.toLocaleString() }}</span>
              </div>

              <div>
                <button class="flex items-center justify-between w-full" @click="showDiscount = !showDiscount">
                  <span class="text-base text-gray-700">折扣</span>
                  <div class="flex items-center gap-1">
                    <span class="text-base font-semibold text-red-500">- ${{ discount.toLocaleString() }}</span>
                    <UIcon :name="showDiscount ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'" class="w-4 h-4 text-gray-400" />
                  </div>
                </button>
                <div v-if="showDiscount" class="mt-2 bg-gray-50 rounded-lg px-3 py-2.5 flex justify-between text-sm text-gray-500">
                  <span>滿1件享9折</span>
                  <span>- ${{ discount.toLocaleString() }}</span>
                </div>
              </div>

              <div v-if="order?.couponDiscount">
                <button class="flex items-center justify-between w-full" @click="showCoupon = !showCoupon">
                  <span class="text-base text-gray-700">優惠券</span>
                  <div class="flex items-center gap-1">
                    <span class="text-base font-semibold text-red-500">- ${{ order?.couponDiscount }}</span>
                    <UIcon :name="showCoupon ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'" class="w-4 h-4 text-gray-400" />
                  </div>
                </button>
                <div v-if="showCoupon" class="mt-2 bg-gray-50 rounded-lg px-3 py-2.5 flex justify-between text-sm text-gray-500">
                  <span>{{ order?.couponName }}</span>
                  <span>- ${{ order?.couponDiscount }}</span>
                </div>
              </div>

              <div>
                <button class="flex items-center justify-between w-full" @click="showShipping = !showShipping">
                  <span class="text-base text-gray-700">運費總計</span>
                  <div class="flex items-center gap-1">
                    <span class="text-base font-semibold text-gray-700">
                      {{ shippingFee > 0 ? `+ $${shippingFee}` : '免運' }}
                    </span>
                    <UIcon :name="showShipping ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'" class="w-4 h-4 text-gray-400" />
                  </div>
                </button>
                <div v-if="showShipping" class="mt-2 bg-gray-50 rounded-lg px-3 py-2.5 flex justify-between text-sm text-gray-500">
                  <span>宅配到府（滿$600免運）</span>
                  <span>{{ shippingFee > 0 ? `$${shippingFee}` : '免費' }}</span>
                </div>
              </div>

            </div>

            <div class="border-t border-gray-200 mt-4 pt-4 flex justify-between items-center">
              <span class="text-base font-semibold text-gray-800">結帳金額</span>
              <span class="text-red-500 text-xl font-bold">${{ total.toLocaleString() }}</span>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { MOCK_ORDERS, ORDER_STATUS } from '~/mocks/order'
import type { Order } from '~/mocks/order'

definePageMeta({ middleware: 'auth' })

const route = useRoute()
const order = ref<Order | undefined>(
  MOCK_ORDERS.find(o => o.orderSn === String(route.params.orderSn))
)

onMounted(() => {
  if (!order.value) navigateTo('/order/list')
})

const steps = ['訂單成立', '付款完成', '商品出貨', '完成收貨']

function addMinutes(base: string, minutes: number) {
  const d = new Date(base.replace(' ', 'T'))
  d.setMinutes(d.getMinutes() + minutes)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const stepTimes = computed(() => {
  const base = order.value?.createTime ?? ''
  return [
    base,
    addMinutes(base, 28),
    addMinutes(base, 60 * 18),
    addMinutes(base, 60 * 24 * 4),
  ]
})

const currentStep = computed(() => {
  switch (order.value?.status) {
    case 0: return 0
    case 1: return 1
    case 2: return 2
    case 3: return 3
    default: return 0
  }
})

const progressWidth = computed(() => {
  const pct = (currentStep.value / (steps.length - 1)) * 100
  return `${pct}%`
})

const paymentMap = {
  credit_card: { label: '信用卡付款', icon: 'i-heroicons-credit-card' },
  mobile:      { label: '行動支付（LINE Pay）', icon: 'i-heroicons-device-phone-mobile' },
  cod:         { label: '貨到付款', icon: 'i-heroicons-banknotes' },
}

const paymentLabel = computed(() => order.value ? paymentMap[order.value.paymentMethod].label : '')
const paymentIcon  = computed(() => order.value ? paymentMap[order.value.paymentMethod].icon : 'i-heroicons-credit-card')

const subtotal    = computed(() => order.value?.items.reduce((s, i) => s + i.price * i.count, 0) ?? 0)
const discount    = computed(() => Math.floor(subtotal.value * 0.1))
const shippingFee = computed(() => subtotal.value < 600 ? 75 : 0)
const total       = computed(() => subtotal.value - discount.value - (order.value?.couponDiscount ?? 0) + shippingFee.value)

const showDiscount = ref(true)
const showCoupon   = ref(true)
const showShipping = ref(true)
</script>
