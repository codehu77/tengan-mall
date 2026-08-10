<template>
  <div class="bg-gray-100 min-h-screen py-6">
    <div class="max-w-2xl mx-auto px-6 space-y-4">

      <h1 class="text-xl font-semibold text-gray-800">訂單付款</h1>

      <div v-if="loading" class="bg-white rounded-lg py-16 text-center text-gray-400">載入中...</div>

      <template v-else-if="order">
        <!-- 訂單資訊 -->
        <div class="bg-white rounded-lg p-6">
          <div class="flex justify-between items-center text-sm mb-2">
            <span class="text-gray-400">訂單編號</span>
            <span class="font-mono text-gray-700">{{ order.orderSn }}</span>
          </div>
          <div class="flex justify-between items-center text-sm mb-2">
            <span class="text-gray-400">付款方式</span>
            <span class="text-gray-700">{{ PAYMENT_METHOD_META[order.paymentMethod].label }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-sm text-gray-400">應付金額</span>
            <span class="text-2xl font-bold text-red-600">NT$ {{ order.payAmount.toLocaleString() }}</span>
          </div>
        </div>

        <!-- 待付款：金流尚未串接的提示 -->
        <template v-if="order.status === 1">
          <div class="bg-orange-50 border border-orange-200 rounded-lg px-5 py-3 flex items-center gap-3">
            <UIcon name="i-heroicons-clock" class="w-5 h-5 text-orange-400 shrink-0" />
            <p class="text-sm text-orange-600">請盡快完成付款，逾時訂單將自動取消並釋放庫存</p>
          </div>

          <button
            class="w-full h-14 bg-gray-300 text-gray-500 rounded-lg font-medium text-base cursor-not-allowed"
            disabled
          >
            付款功能即將開放
          </button>

          <button
            class="w-full h-12 border border-gray-200 text-gray-600 rounded-lg font-medium text-base hover:bg-gray-50 transition"
            @click="handleCancel"
          >
            取消訂單
          </button>
        </template>

        <!-- 非待付款狀態：導向訂單詳情 -->
        <template v-else>
          <div class="bg-white rounded-lg p-6 text-center space-y-3">
            <UBadge :color="ORDER_STATUS_META[order.status].color as any" variant="solid" size="lg">
              {{ ORDER_STATUS_META[order.status].label }}
            </UBadge>
            <p class="text-sm text-gray-400">此訂單目前不是待付款狀態</p>
            <UButton color="red" variant="outline" @click="navigateTo(`/order/${order.orderSn}`)">
              查看訂單詳情
            </UButton>
          </div>
        </template>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ORDER_STATUS_META, PAYMENT_METHOD_META } from '~/types/order'
import type { OrderDetail } from '~/types/order'

definePageMeta({ middleware: 'auth' })

const route = useRoute()
const toast = useToast()
const { fetchOrderDetail, cancelOrder } = useOrder()

const loading = ref(true)
const order = ref<OrderDetail | null>(null)

onMounted(async () => {
  const orderSn = route.query.orderSn as string
  if (!orderSn) return navigateTo('/order/list')
  try {
    order.value = await fetchOrderDetail(orderSn)
  } catch {
    toast.add({ title: '找不到此訂單', color: 'red', timeout: 3000 })
    navigateTo('/order/list')
  } finally {
    loading.value = false
  }
})

async function handleCancel() {
  if (!order.value) return
  try {
    await cancelOrder(order.value.orderSn)
    toast.add({ title: '訂單已取消', color: 'gray', timeout: 2000 })
    navigateTo('/order/list')
  } catch (e: any) {
    toast.add({ title: '取消失敗', description: e.data?.data?.message ?? e.message, color: 'red', timeout: 3000 })
  }
}
</script>
