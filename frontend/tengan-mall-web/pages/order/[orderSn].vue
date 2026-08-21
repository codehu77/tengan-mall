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
        <UBadge v-if="order" :color="ORDER_STATUS_META[order.status].color as any" variant="solid" size="md" class="ml-1">
          {{ ORDER_STATUS_META[order.status].label }}
        </UBadge>
      </div>

      <div v-if="loading" class="bg-white rounded-lg py-20 text-center text-gray-400">載入中...</div>

      <div v-else-if="order" class="flex gap-6 items-start">

        <!-- 左欄 -->
        <div class="flex-1 space-y-4 min-w-0">

          <!-- Block 1: 物流狀態 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">訂單狀態</h2>
            <div class="border-t border-gray-100 pt-6">
              <div v-if="order.status === 5" class="flex items-center gap-3">
                <UIcon name="i-heroicons-x-circle" class="w-6 h-6 text-gray-300 shrink-0" />
                <div>
                  <p class="text-base text-gray-500">訂單已取消</p>
                  <p v-if="order.cancelReason" class="text-sm text-gray-400 mt-0.5">原因：{{ cancelReasonLabel }}</p>
                </div>
              </div>
              <div v-else class="relative flex items-start justify-between">
                <div class="absolute top-4 left-0 right-0 h-0.5 bg-gray-200 z-0" />
                <div
                  class="absolute top-4 left-0 h-0.5 bg-red-400 z-0 transition-all"
                  :style="{ width: progressWidth }"
                />
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
                </div>
              </div>
            </div>
          </div>

          <!-- Block 2: 商品明細 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">商品明細</h2>
            <div class="border-t border-gray-100 pt-4 space-y-4">
              <div v-for="item in order.items" :key="item.skuId" class="flex items-center gap-4">
                <img v-if="item.skuImage" :src="item.skuImage" :alt="item.skuName" class="w-20 h-20 rounded border border-gray-100 object-cover shrink-0" />
                <div class="flex-1 min-w-0">
                  <p class="text-base text-gray-700 line-clamp-2">{{ item.skuName }}</p>
                  <p class="text-sm text-gray-400 mt-1">× {{ item.count }}</p>
                </div>
                <p class="text-base font-medium text-gray-800 shrink-0">NT$ {{ item.subtotal.toLocaleString() }}</p>
              </div>
            </div>
          </div>

          <!-- Block 3: 收件人資訊 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">收件人資訊</h2>
            <div class="border-t border-gray-100 pt-4 space-y-3">
              <div class="flex gap-2 text-base">
                <span class="text-gray-400 w-16 shrink-0">姓名</span>
                <span class="text-gray-700">{{ order.receiverName }}</span>
              </div>
              <div class="flex gap-2 text-base">
                <span class="text-gray-400 w-16 shrink-0">手機</span>
                <span class="text-gray-700">{{ order.receiverPhone }}</span>
              </div>
              <div class="flex gap-2 text-base">
                <span class="text-gray-400 w-16 shrink-0">地址</span>
                <span class="text-gray-700">{{ order.city }}{{ order.district }}{{ order.street }}</span>
              </div>
              <div v-if="order.remark" class="flex gap-2 text-base">
                <span class="text-gray-400 w-16 shrink-0">備註</span>
                <span class="text-gray-700">{{ order.remark }}</span>
              </div>
            </div>
          </div>

          <!-- Block 4: 付款方式 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">付款方式</h2>
            <div class="border-t border-gray-100 pt-4 flex items-center gap-3">
              <UIcon :name="PAYMENT_METHOD_META[order.paymentMethod].icon" class="w-5 h-5 text-gray-500" />
              <span class="text-base text-gray-700">{{ PAYMENT_METHOD_META[order.paymentMethod].label }}</span>
            </div>
          </div>

        </div>

        <!-- 右欄：結帳明細 -->
        <div class="w-[420px] shrink-0 sticky top-6 space-y-4">
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">結帳明細</h2>
            <div class="border-t border-gray-100 pt-4 space-y-4">

              <div class="flex justify-between items-center text-base text-gray-700">
                <span>商品原價總金額</span>
                <span class="font-semibold">NT$ {{ order.totalAmount.toLocaleString() }}</span>
              </div>

              <div v-if="order.discountAmount > 0" class="flex justify-between items-center text-base text-gray-700">
                <span>優惠券折抵</span>
                <span class="font-semibold text-red-500">- NT$ {{ order.discountAmount.toLocaleString() }}</span>
              </div>

              <div v-if="order.pointsDiscountAmount > 0" class="flex justify-between items-center text-base text-gray-700">
                <span>點數折抵（{{ order.pointsUsed }} 點）</span>
                <span class="font-semibold text-red-500">- NT$ {{ order.pointsDiscountAmount.toLocaleString() }}</span>
              </div>

            </div>

            <div class="border-t border-gray-200 mt-4 pt-4 flex justify-between items-center">
              <span class="text-base font-semibold text-gray-800">應付金額</span>
              <span class="text-red-500 text-xl font-bold">NT$ {{ order.payAmount.toLocaleString() }}</span>
            </div>
          </div>

          <template v-if="order.status === 1">
            <UButton block color="red" @click="navigateTo(`/order/pay?orderSn=${order.orderSn}`)">前往付款</UButton>
            <UButton block color="gray" variant="outline" @click="handleCancel">取消訂單</UButton>
          </template>
          <template v-else-if="order.status === 3">
            <UButton block color="red" @click="handleConfirmReceipt">確認收貨</UButton>
          </template>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ORDER_STATUS_META, PAYMENT_METHOD_META } from '~/types/order'
import type { OrderDetail } from '~/types/order'

definePageMeta({ middleware: 'auth' })

const route = useRoute()
const toast = useToast()
const { fetchOrderDetail, cancelOrder, confirmReceipt } = useOrder()

const loading = ref(true)
const order = ref<OrderDetail | null>(null)

async function load() {
  loading.value = true
  try {
    const result = await fetchOrderDetail(String(route.params.orderSn))
    // 秒殺訂單非同步落地中極少見會被連到這頁（一般都先經過 /order/pay），保守起見還是要處理型別。
    if ('processing' in result) {
      toast.add({ title: '訂單處理中，請稍後再查看', color: 'orange', timeout: 3000 })
      navigateTo('/order/list')
      return
    }
    order.value = result
  } catch {
    toast.add({ title: '找不到此訂單', color: 'red', timeout: 3000 })
    navigateTo('/order/list')
  } finally {
    loading.value = false
  }
}

onMounted(load)

const steps = ['訂單成立', '付款完成', '商品出貨', '完成收貨']

const currentStep = computed(() => {
  switch (order.value?.status) {
    case 1: return 0
    case 2: return 1
    case 3: return 2
    case 4: return 3
    default: return 0
  }
})

const progressWidth = computed(() => `${(currentStep.value / (steps.length - 1)) * 100}%`)

const cancelReasonLabel = computed(() => {
  switch (order.value?.cancelReason) {
    case 'USER_CANCELLED': return '買家自主取消'
    case 'TIMEOUT': return '逾時未付款，系統自動取消'
    case 'ADMIN_CANCELLED': return '客服代為取消'
    default: return order.value?.cancelReason
  }
})

async function handleCancel() {
  if (!order.value) return
  try {
    await cancelOrder(order.value.orderSn)
    toast.add({ title: '訂單已取消', color: 'gray', timeout: 2000 })
    load()
  } catch (e: any) {
    toast.add({ title: '取消失敗', description: e.data?.data?.message ?? e.message, color: 'red', timeout: 3000 })
  }
}

async function handleConfirmReceipt() {
  if (!order.value) return
  try {
    await confirmReceipt(order.value.orderSn)
    toast.add({ title: '已確認收貨', description: '感謝您的購買', color: 'green', timeout: 2000 })
    load()
  } catch (e: any) {
    toast.add({ title: '確認收貨失敗', description: e.data?.data?.message ?? e.message, color: 'red', timeout: 3000 })
  }
}
</script>
