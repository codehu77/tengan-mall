<template>
  <div class="bg-gray-100 min-h-screen py-6">
    <div class="max-w-2xl mx-auto px-6 space-y-4">

      <h1 class="text-xl font-semibold text-gray-800">選擇付款方式</h1>

      <!-- 訂單資訊 -->
      <div class="bg-white rounded-lg p-6">
        <div class="flex justify-between items-center text-sm mb-2">
          <span class="text-gray-400">訂單編號</span>
          <span class="font-mono text-gray-700">{{ orderSn }}</span>
        </div>
        <div class="flex justify-between items-center">
          <span class="text-sm text-gray-400">應付金額</span>
          <span class="text-2xl font-bold text-red-600">NT$ {{ amount.toLocaleString() }}</span>
        </div>
      </div>

      <!-- 付款方式選擇 -->
      <div class="bg-white rounded-lg p-6">
        <h2 class="text-sm font-semibold text-gray-700 mb-4">付款方式</h2>
        <div class="space-y-3">
          <label
            v-for="method in paymentMethods"
            :key="method.id"
            class="flex items-center gap-3 p-4 border rounded-lg cursor-pointer transition"
            :class="selectedMethod === method.id ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
          >
            <input type="radio" :value="method.id" v-model="selectedMethod" class="accent-red-500" />
            <span class="text-xl">{{ method.icon }}</span>
            <div>
              <p class="text-sm font-medium text-gray-700">{{ method.label }}</p>
              <p class="text-xs text-gray-400">{{ method.desc }}</p>
            </div>
          </label>
        </div>
      </div>

      <!-- 付款倒計時提示 -->
      <div class="bg-orange-50 border border-orange-200 rounded-lg px-5 py-3 flex items-center gap-3">
        <UIcon name="i-heroicons-clock" class="w-5 h-5 text-orange-400 shrink-0" />
        <p class="text-sm text-orange-600">請在 <b>30 分鐘</b> 內完成付款，逾時訂單將自動取消</p>
      </div>

      <!-- 確認付款按鈕 -->
      <button
        class="w-full h-14 bg-red-500 text-white rounded-lg font-medium text-base hover:bg-red-600 transition"
        @click="confirmPay"
      >
        確認付款
      </button>

      <p class="text-center text-xs text-gray-400">
        點擊「確認付款」即表示您同意本平台的
        <span class="text-red-400">服務條款</span>及<span class="text-red-400">隱私政策</span>
      </p>

    </div>
  </div>
</template>

<script setup lang="ts">
import { MOCK_ORDERS } from '~/mocks/order'

definePageMeta({ middleware: 'auth' })

const route = useRoute()
const toast = useToast()

const order = computed(() =>
  MOCK_ORDERS.find(o => o.orderSn === (route.query.orderSn as string))
)

const orderSn = computed(() => order.value?.orderSn ?? (route.query.orderSn as string ?? ''))
const amount = computed(() => order.value?.totalAmount ?? 0)
const selectedMethod = ref('linepay')

onMounted(() => {
  if (!order.value) navigateTo('/order/list')
})

const paymentMethods = [
  { id: 'linepay', icon: '💚', label: 'LINE Pay', desc: '使用 LINE Pay 掃碼付款' },
  { id: 'credit', icon: '💳', label: '信用卡 / 金融卡', desc: 'Visa、Mastercard、JCB 均可' },
  { id: 'atm', icon: '🏧', label: 'ATM 轉帳', desc: '全台各銀行 ATM' },
  { id: 'cvs', icon: '🏪', label: '超商代碼繳費', desc: '7-11、全家、萊爾富、OK 超商' },
]

async function confirmPay() {
  await new Promise(r => setTimeout(r, 800))
  toast.add({ title: '付款成功！', description: '訂單已成立，感謝您的購買', color: 'green', timeout: 3000 })
  navigateTo('/order/list')
}
</script>
