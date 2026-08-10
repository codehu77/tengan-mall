<template>
  <div class="space-y-4">

      <h1 class="text-2xl font-semibold text-gray-800">我的訂單</h1>

      <!-- 狀態過濾 -->
      <div class="bg-white rounded-lg px-2">
        <div class="flex border-b border-gray-100">
          <button
            v-for="tab in tabs"
            :key="tab.value ?? 'all'"
            class="px-5 py-3 text-base font-medium transition border-b-2 -mb-px"
            :class="activeTab === tab.value
              ? 'border-red-500 text-red-600'
              : 'border-transparent text-gray-500 hover:text-gray-700'"
            @click="switchTab(tab.value)"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <div v-if="loading" class="bg-white rounded-lg py-20 text-center text-gray-400">載入中...</div>

      <!-- 訂單列表 -->
      <div v-else-if="orders.length > 0" class="space-y-4">
        <div
          v-for="order in orders"
          :key="order.id"
          class="bg-white rounded-lg overflow-hidden"
        >
          <div class="flex items-center justify-between px-6 py-3 bg-gray-50 border-b border-gray-100">
            <div class="flex items-center gap-4 text-gray-500">
              <span class="text-base font-mono">{{ order.orderSn }}</span>
              <span class="text-sm text-gray-400">{{ formatTime(order.createdAt) }}</span>
            </div>
            <UBadge :color="ORDER_STATUS_META[order.status].color as any" variant="solid" size="md">
              {{ ORDER_STATUS_META[order.status].label }}
            </UBadge>
          </div>

          <div class="px-6 py-4 flex items-center justify-between">
            <div class="text-base text-gray-500">
              付款方式：{{ PAYMENT_METHOD_META[order.paymentMethod].label }}
            </div>
            <div class="flex items-center gap-4">
              <span class="text-base text-gray-500">
                應付金額：<span class="font-semibold text-red-500">NT$ {{ order.payAmount.toLocaleString() }}</span>
              </span>
              <template v-if="order.status === 1">
                <button
                  class="px-4 py-2 text-base bg-red-500 text-white rounded hover:bg-red-600 transition"
                  @click="navigateTo(`/order/pay?orderSn=${order.orderSn}`)"
                >
                  立即付款
                </button>
                <button
                  class="px-4 py-2 text-base border border-gray-200 text-gray-600 rounded hover:bg-gray-50 transition"
                  @click="handleCancel(order.orderSn)"
                >
                  取消訂單
                </button>
              </template>
              <template v-else-if="order.status === 3">
                <button
                  class="px-4 py-2 text-base bg-red-500 text-white rounded hover:bg-red-600 transition"
                  @click="handleConfirmReceipt(order.orderSn)"
                >
                  確認收貨
                </button>
              </template>
              <button class="text-base text-gray-400 hover:text-gray-600 transition" @click="navigateTo(`/order/${order.orderSn}`)">查看詳情</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空狀態 -->
      <div v-else class="bg-white rounded-lg py-20 text-center">
        <UIcon name="i-heroicons-clipboard-document-list" class="w-14 h-14 text-gray-200 mx-auto mb-3" />
        <p class="text-gray-400 text-base">暫無相關訂單</p>
        <NuxtLink to="/" class="mt-4 inline-block">
          <UButton color="red" size="sm" variant="outline">去購物</UButton>
        </NuxtLink>
      </div>

  </div>
</template>

<script setup lang="ts">
import { ORDER_STATUS_META, PAYMENT_METHOD_META } from '~/types/order'
import type { OrderStatus, OrderSummary } from '~/types/order'

definePageMeta({ middleware: 'auth', layout: 'member' })

const toast = useToast()
const { fetchOrders, cancelOrder, confirmReceipt } = useOrder()

const tabs: Array<{ label: string; value: OrderStatus | undefined }> = [
  { label: '全部訂單', value: undefined },
  { label: '待付款', value: 1 },
  { label: '已付款', value: 2 },
  { label: '已出貨', value: 3 },
  { label: '已完成', value: 4 },
  { label: '已取消', value: 5 },
]

const activeTab = ref<OrderStatus | undefined>(undefined)
const orders = ref<OrderSummary[]>([])
const loading = ref(true)

async function loadOrders() {
  loading.value = true
  try {
    const { items } = await fetchOrders(activeTab.value, 1, 50)
    orders.value = items
  } catch (e: any) {
    toast.add({ title: '訂單載入失敗', description: e.data?.data?.message ?? e.message, color: 'red', timeout: 3000 })
  } finally {
    loading.value = false
  }
}

function switchTab(value: OrderStatus | undefined) {
  activeTab.value = value
  loadOrders()
}

function formatTime(iso: string) {
  return new Date(iso).toLocaleString('zh-TW', { hour12: false })
}

async function handleCancel(orderSn: string) {
  try {
    await cancelOrder(orderSn)
    toast.add({ title: '訂單已取消', color: 'gray', timeout: 2000 })
    loadOrders()
  } catch (e: any) {
    toast.add({ title: '取消失敗', description: e.data?.data?.message ?? e.message, color: 'red', timeout: 3000 })
  }
}

async function handleConfirmReceipt(orderSn: string) {
  try {
    await confirmReceipt(orderSn)
    toast.add({ title: '已確認收貨', description: '感謝您的購買', color: 'green', timeout: 2000 })
    loadOrders()
  } catch (e: any) {
    toast.add({ title: '確認收貨失敗', description: e.data?.data?.message ?? e.message, color: 'red', timeout: 3000 })
  }
}

onMounted(loadOrders)
</script>
