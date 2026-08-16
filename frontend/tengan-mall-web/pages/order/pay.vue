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

        <!-- 待付款 -->
        <template v-if="order.status === 1">
          <div v-if="confirmingLinePay" class="bg-white rounded-lg py-16 text-center text-gray-400">
            正在確認 LINE Pay 付款結果...
          </div>

          <template v-else>
            <div class="bg-orange-50 border border-orange-200 rounded-lg px-5 py-3 flex items-center gap-3">
              <UIcon name="i-heroicons-clock" class="w-5 h-5 text-orange-400 shrink-0" />
              <p class="text-sm text-orange-600">請盡快完成付款，逾時訂單將自動取消並釋放庫存</p>
            </div>

            <button
              class="w-full h-14 bg-red-500 text-white rounded-lg font-semibold text-lg hover:bg-red-600 transition disabled:opacity-50"
              :disabled="initiating"
              @click="handlePay"
            >
              {{ initiating ? '處理中...' : payButtonLabel }}
            </button>

            <button
              v-if="order.paymentMethod !== 'cod'"
              class="w-full h-10 text-sm text-gray-400 hover:text-gray-600 transition"
              @click="handleRefreshStatus"
            >
              已完成付款？重新查詢付款狀態
            </button>

            <button
              class="w-full h-12 border border-gray-200 text-gray-600 rounded-lg font-medium text-base hover:bg-gray-50 transition"
              @click="handleCancel"
            >
              取消訂單
            </button>
          </template>
        </template>

        <!-- 非待付款狀態：導向訂單詳情 -->
        <template v-else>
          <div class="bg-white rounded-lg p-6 text-center space-y-3">
            <UBadge :color="ORDER_STATUS_META[order.status].color as any" variant="solid" size="lg">
              {{ ORDER_STATUS_META[order.status].label }}
            </UBadge>
            <p class="text-sm text-gray-400">{{ nonPendingStatusMessage }}</p>
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
import type { OrderDetail, OrderStatus } from '~/types/order'
import type { EcpayFormData } from '~/types/payment'

definePageMeta({ middleware: 'auth' })

const route = useRoute()
const toast = useToast()
const { fetchOrderDetail, cancelOrder } = useOrder()
const { initiatePayment, confirmLinePayPayment } = usePayment()

const loading = ref(true)
const order = ref<OrderDetail | null>(null)
const initiating = ref(false)
const confirmingLinePay = ref(false)

const payButtonLabel = computed(() => {
  if (!order.value) return ''
  if (order.value.paymentMethod === 'cod') return '確認訂單（貨到付款）'
  return `前往付款（${PAYMENT_METHOD_META[order.value.paymentMethod].label}）`
})

const NON_PENDING_STATUS_MESSAGE: Record<Exclude<OrderStatus, 1>, string> = {
  2: '付款成功，感謝您的購買！',
  3: '商品已出貨',
  4: '訂單已完成',
  5: '此訂單已取消',
}
const nonPendingStatusMessage = computed(() => {
  if (!order.value) return ''
  return NON_PENDING_STATUS_MESSAGE[order.value.status as Exclude<OrderStatus, 1>]
})

onMounted(async () => {
  const orderSn = route.query.orderSn as string
  if (!orderSn) return navigateTo('/order/list')

  // LINE Pay 授權完成後把使用者導回這頁，官方會在 confirmUrl 後面自動附上 transactionId query 參數
  // ——這一步才是真正扣款（Request API 當下只是「授權」），所以要先呼叫 confirm 再顯示結果。
  const transactionId = route.query.transactionId as string | undefined
  const cancelled = route.query.cancelled as string | undefined

  if (transactionId) {
    confirmingLinePay.value = true
    try {
      await confirmLinePayPayment(orderSn, transactionId)
      toast.add({ title: 'LINE Pay 付款成功', color: 'green', timeout: 3000 })
    } catch (e: any) {
      toast.add({
        title: 'LINE Pay 付款確認失敗',
        description: e.data?.data?.message ?? e.message,
        color: 'red',
        timeout: 5000,
      })
    } finally {
      confirmingLinePay.value = false
    }
  } else if (cancelled) {
    toast.add({ title: '已取消 LINE Pay 付款', color: 'gray', timeout: 3000 })
  }

  try {
    order.value = await fetchOrderDetail(orderSn)
  } catch {
    toast.add({ title: '找不到此訂單', color: 'red', timeout: 3000 })
    navigateTo('/order/list')
  } finally {
    loading.value = false
  }
})

async function handlePay() {
  if (!order.value) return
  initiating.value = true
  try {
    const result = await initiatePayment(order.value.orderSn, order.value.paymentMethod)
    if (result.method === 'credit_card' && result.ecpayForm) {
      submitEcpayForm(result.ecpayForm)
    } else if (result.method === 'linepay' && result.linePayPaymentUrl) {
      window.location.href = result.linePayPaymentUrl
    } else {
      // cod：tengan-payment 同步完成，不用等任何金流商回呼
      toast.add({ title: '訂單已確認，將以貨到付款方式出貨', color: 'green', timeout: 3000 })
      navigateTo(`/order/${order.value.orderSn}`)
    }
  } catch (e: any) {
    toast.add({
      title: '發起付款失敗',
      description: e.data?.data?.message ?? e.message,
      color: 'red',
      timeout: 4000,
    })
  } finally {
    initiating.value = false
  }
}

/** ECPay 的付款頁是瀏覽器整頁導向，動態組一個隱藏表單立即 submit，跟結帳頁組資料的方式不同。 */
function submitEcpayForm(form: EcpayFormData) {
  const formEl = document.createElement('form')
  formEl.method = 'POST'
  formEl.action = form.actionUrl
  for (const [key, value] of Object.entries(form.fields)) {
    const input = document.createElement('input')
    input.type = 'hidden'
    input.name = key
    input.value = value
    formEl.appendChild(input)
  }
  document.body.appendChild(formEl)
  formEl.submit()
}

/**
 * ECPay 的 ReturnURL 是後端 server-to-server 通知，跟使用者瀏覽器導回的時間點不一定同步——
 * 提供手動重新查詢，不用強迫使用者整頁重新整理。
 */
async function handleRefreshStatus() {
  if (!order.value) return
  order.value = await fetchOrderDetail(order.value.orderSn)
}

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
