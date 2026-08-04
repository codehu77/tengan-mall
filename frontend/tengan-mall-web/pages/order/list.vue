<template>
  <div class="bg-gray-100 min-h-screen py-6">
    <div class="max-w-7xl mx-auto px-6 space-y-4">

      <h1 class="text-2xl font-semibold text-gray-800">我的訂單</h1>

      <!-- 狀態過濾 -->
      <div class="bg-white rounded-lg px-2">
        <div class="flex border-b border-gray-100">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            class="px-5 py-3 text-base font-medium transition border-b-2 -mb-px"
            :class="activeTab === tab.value
              ? 'border-red-500 text-red-600'
              : 'border-transparent text-gray-500 hover:text-gray-700'"
            @click="activeTab = tab.value"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <!-- 訂單列表 -->
      <div v-if="filteredOrders.length > 0" class="space-y-4">
        <div
          v-for="order in filteredOrders"
          :key="order.orderId"
          class="bg-white rounded-lg overflow-hidden"
        >
          <!-- 訂單標題列 -->
          <div class="flex items-center justify-between px-6 py-3 bg-gray-50 border-b border-gray-100">
            <div class="flex items-center gap-4 text-gray-500">
              <span class="text-base font-mono">{{ order.orderSn }}</span>
            </div>
            <div class="flex items-center gap-3">
              <span v-if="order.cancelReason" class="text-sm text-gray-400">{{ order.cancelReason }}</span>
              <UBadge
                :color="ORDER_STATUS[order.status].color as any"
                variant="solid"
                size="md"
              >
                {{ ORDER_STATUS[order.status].label }}
              </UBadge>
            </div>
          </div>

          <!-- 商品列表 -->
          <div class="px-6 py-4 space-y-3">
            <div
              v-for="item in order.items"
              :key="item.skuId"
              class="flex items-center gap-4"
            >
              <img :src="item.image" :alt="item.skuName" class="w-20 h-20 rounded border border-gray-100 object-cover" />
              <div class="flex-1">
                <p class="text-base text-gray-700 line-clamp-2">{{ item.skuName }}</p>
                <p class="text-sm text-gray-400 mt-1">× {{ item.count }}</p>
              </div>
              <p class="text-base text-gray-700">NT$ {{ (item.price * item.count).toLocaleString() }}</p>
            </div>
          </div>

          <!-- 訂單底部 -->
          <div class="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
            <div class="text-base text-gray-500">
              共 {{ order.items.reduce((s, i) => s + i.count, 0) }} 件商品
            </div>
            <div class="flex items-center gap-4">
              <span class="text-base text-gray-500">
                訂單金額：<span class="font-semibold text-red-500">NT$ {{ order.totalAmount.toLocaleString() }}</span>
              </span>
              <template v-if="order.status === 0">
                <button
                  class="px-4 py-2 text-base bg-red-500 text-white rounded hover:bg-red-600 transition"
                  @click="navigateTo(`/order/pay?orderSn=${order.orderSn}`)"
                >
                  立即付款
                </button>
                <button
                  class="px-4 py-2 text-base border border-gray-200 text-gray-600 rounded hover:bg-gray-50 transition"
                  @click="cancelOrder(order.orderId)"
                >
                  取消訂單
                </button>
              </template>
              <template v-else-if="order.status === 2">
                <button
                  class="px-4 py-2 text-base bg-red-500 text-white rounded hover:bg-red-600 transition"
                  @click="confirmReceipt(order.orderId)"
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
  </div>
</template>

<script setup lang="ts">
import { MOCK_ORDERS, ORDER_STATUS, type Order } from '~/mocks/order'

definePageMeta({ middleware: 'auth' })

const toast = useToast()

const orders = ref<Order[]>(JSON.parse(JSON.stringify(MOCK_ORDERS)))

const tabs = [
  { label: '全部訂單', value: -1 },
  { label: '待付款', value: 0 },
  { label: '待出貨', value: 1 },
  { label: '待收貨', value: 2 },
  { label: '已完成', value: 3 },
  { label: '不成立', value: 4 },
]

const activeTab = ref(-1)

const filteredOrders = computed(() =>
  activeTab.value === -1
    ? orders.value
    : orders.value.filter(o => o.status === activeTab.value)
)

function cancelOrder(orderId: number) {
  const order = orders.value.find(o => o.orderId === orderId)
  if (!order) return
  order.status = 4
  order.cancelReason = '買家自主取消'
  toast.add({ title: '訂單已取消', color: 'gray', timeout: 2000 })
}

function confirmReceipt(orderId: number) {
  const order = orders.value.find(o => o.orderId === orderId)
  if (!order) return
  order.status = 3
  toast.add({ title: '已確認收貨', description: '感謝您的購買', color: 'green', timeout: 2000 })
}
</script>
