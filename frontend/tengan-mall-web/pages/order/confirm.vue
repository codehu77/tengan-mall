<template>
  <div class="bg-gray-100 min-h-screen py-6">
    <div class="max-w-7xl mx-auto px-6">

      <h1 class="text-2xl font-semibold text-gray-800 mb-4">確認訂單</h1>

      <div v-if="loading" class="bg-white rounded-lg py-20 text-center text-gray-400">載入中...</div>

      <div v-else class="flex gap-6 items-start">

        <!-- 左欄 -->
        <div class="flex-1 space-y-4 min-w-0">

          <!-- Block 1: 商品明細 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">商品明細</h2>
            <div class="border-t border-gray-100 pt-4 space-y-4">
              <div v-for="item in confirmResult?.items" :key="item.skuId" class="flex items-center gap-4">
                <img :src="item.mainImage" :alt="item.name" class="w-20 h-20 rounded border border-gray-100 object-cover shrink-0" />
                <div class="flex-1 text-base min-w-0">
                  <p class="text-gray-700 line-clamp-2">{{ item.name }}</p>
                  <p class="text-gray-400 mt-1">× {{ item.count }}</p>
                </div>
                <p class="text-base font-medium text-gray-800 shrink-0">NT$ {{ item.subtotal.toLocaleString() }}</p>
              </div>
            </div>
          </div>

          <!-- Block 2: 收件地址 -->
          <div class="bg-white rounded-lg p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-bold text-gray-800">收件地址</h2>
              <NuxtLink to="/member/address" class="text-sm text-red-500 border border-red-300 rounded-lg px-3 py-1 hover:bg-red-50 transition">
                管理地址
              </NuxtLink>
            </div>
            <div class="border-t border-gray-100 pt-4">
              <div v-if="addressStore.addresses.length === 0" class="text-base text-gray-400 py-4 text-center">
                尚未新增收件地址，請先
                <NuxtLink to="/member/address" class="text-red-500">新增地址</NuxtLink>
              </div>
              <div v-else class="space-y-3">
                <label
                  v-for="addr in addressStore.addresses"
                  :key="addr.id"
                  class="flex items-start gap-3 p-4 border rounded-lg cursor-pointer transition"
                  :class="selectedAddressId === addr.id ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
                >
                  <input type="radio" :value="addr.id" v-model="selectedAddressId" class="accent-red-500 mt-1" />
                  <div class="text-base">
                    <p class="text-gray-700">
                      <span class="font-medium">{{ addr.receiverName }}</span>
                      <span class="text-gray-400 ml-2">{{ addr.receiverPhone }}</span>
                      <UBadge v-if="addr.isDefault" color="red" variant="outline" size="xs" class="ml-2">預設</UBadge>
                    </p>
                    <p class="text-gray-500 mt-1">{{ addr.city }}{{ addr.district }}{{ addr.street }}</p>
                  </div>
                </label>
              </div>
            </div>
          </div>

          <!-- Block 3: 備註 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">備註</h2>
            <div class="border-t border-gray-100 pt-4">
              <textarea
                v-model="remark"
                placeholder="填寫給賣家的留言（選填）"
                rows="3"
                class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 resize-none text-gray-700 placeholder-gray-300"
              />
            </div>
          </div>

          <!-- Block 4: 付款方式 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">付款方式</h2>
            <div class="border-t border-gray-100 pt-4 space-y-3">
              <label
                v-for="method in paymentMethods"
                :key="method.value"
                class="flex items-center gap-3 p-4 border rounded-lg cursor-pointer transition"
                :class="paymentMethod === method.value ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
              >
                <input type="radio" :value="method.value" v-model="paymentMethod" class="accent-red-500" />
                <UIcon :name="method.icon" class="w-5 h-5 text-gray-500 shrink-0" />
                <span class="text-base text-gray-700">{{ method.label }}</span>
              </label>
            </div>
          </div>

        </div>

        <!-- 右欄：結帳明細 + 確認訂單（sticky） -->
        <div class="w-[420px] shrink-0 sticky top-6 space-y-4">
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">結帳明細</h2>

            <div class="border-t border-gray-100 pt-4 space-y-4">

              <div class="flex justify-between items-center text-base text-gray-700">
                <span>商品原價總金額</span>
                <span class="font-semibold">NT$ {{ (confirmResult?.totalAmount ?? 0).toLocaleString() }}</span>
              </div>

              <!-- 優惠券 -->
              <div>
                <div class="flex items-center justify-between">
                  <span class="text-base text-gray-700">優惠券</span>
                  <div class="flex items-center gap-2">
                    <span v-if="selectedCoupon" class="text-base font-semibold text-red-500">- NT$ {{ selectedCoupon.discountAmount }}</span>
                    <span v-else class="text-sm text-gray-400">未使用</span>
                    <button
                      class="text-sm text-red-500 border border-red-300 rounded-lg px-2.5 py-1 hover:bg-red-50 transition"
                      @click="openCouponModal"
                    >
                      {{ selectedCoupon ? '更換' : '選擇' }}
                    </button>
                  </div>
                </div>
                <div v-if="selectedCoupon" class="mt-2 bg-gray-50 rounded-lg px-3 py-2.5 flex justify-between text-sm text-gray-500">
                  <span>{{ selectedCoupon.templateName }}</span>
                  <span>- NT$ {{ selectedCoupon.discountAmount }}</span>
                </div>
              </div>

            </div>

            <div class="border-t border-gray-200 mt-4 pt-4 flex justify-between items-center">
              <span class="text-base font-semibold text-gray-800">結帳金額</span>
              <span class="text-red-500 text-xl font-bold">NT$ {{ payableAmount.toLocaleString() }}</span>
            </div>
          </div>

          <button
            class="w-full h-14 bg-red-500 text-white rounded-lg font-semibold text-lg hover:bg-red-600 transition disabled:opacity-50"
            :disabled="!canSubmit || submitting"
            @click="submitOrder"
          >
            {{ submitting ? '送出中...' : '確認訂單' }}
          </button>
        </div>

      </div>
    </div>

    <!-- 優惠券選擇彈窗 -->
    <UModal v-model="showCouponModal">
      <UCard>
        <template #header>
          <p class="text-base font-semibold text-gray-700">選擇優惠券</p>
        </template>

        <div class="space-y-3 py-2">
          <label
            class="flex items-center gap-3 p-4 border rounded-lg cursor-pointer transition"
            :class="pendingCouponId === null ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
          >
            <input type="radio" :value="null" v-model="pendingCouponId" class="accent-red-500" />
            <span class="text-base text-gray-500">不使用優惠券</span>
          </label>

          <div v-if="availableCoupons.length === 0" class="text-sm text-gray-400 text-center py-4">
            目前沒有可用的優惠券
          </div>

          <label
            v-for="coupon in availableCoupons"
            :key="coupon.id"
            class="flex items-center gap-3 p-4 border rounded-lg cursor-pointer transition"
            :class="pendingCouponId === coupon.id ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
          >
            <input type="radio" :value="coupon.id" v-model="pendingCouponId" class="accent-red-500" />
            <div class="flex-1 min-w-0">
              <span class="text-base text-gray-700">{{ coupon.templateName }}</span>
              <p class="text-sm text-gray-400 mt-0.5">滿 NT$ {{ coupon.thresholdAmount }} 折 NT$ {{ coupon.discountAmount }}</p>
            </div>
            <span class="text-base font-semibold text-red-500 shrink-0">- NT$ {{ coupon.discountAmount }}</span>
          </label>
        </div>

        <template #footer>
          <div class="flex justify-end gap-3">
            <button
              class="px-5 py-2 text-sm text-gray-500 border border-gray-200 rounded-lg hover:bg-gray-50 transition"
              @click="showCouponModal = false"
            >
              取消
            </button>
            <button
              class="px-5 py-2 text-sm text-white bg-red-500 rounded-lg hover:bg-red-600 transition"
              @click="confirmCoupon"
            >
              確認
            </button>
          </div>
        </template>
      </UCard>
    </UModal>

  </div>
</template>

<script setup lang="ts">
import type { MyCoupon } from '~/types/coupon'
import type { PaymentMethod } from '~/types/order'

definePageMeta({ middleware: 'auth' })

const { confirmOrder, createOrder, fetchAvailableCoupons } = useOrder()
const { fetchCartCount } = useCart()
const addressStore = useAddressStore()
const cartStore = useCartStore()
const toast = useToast()

const loading = ref(true)
const submitting = ref(false)
const confirmResult = ref<Awaited<ReturnType<typeof confirmOrder>> | null>(null)

const selectedAddressId = ref<number | null>(null)
const remark = ref('')

const paymentMethods: Array<{ value: PaymentMethod; label: string; icon: string }> = [
  { value: 'linepay', label: 'LINE Pay', icon: 'i-heroicons-device-phone-mobile' },
  { value: 'credit_card', label: '信用卡付款', icon: 'i-heroicons-credit-card' },
  { value: 'cod', label: '貨到付款', icon: 'i-heroicons-banknotes' },
]
const paymentMethod = ref<PaymentMethod>('linepay')

const availableCoupons = ref<MyCoupon[]>([])
const showCouponModal = ref(false)
const selectedCoupon = ref<MyCoupon | null>(null)
const pendingCouponId = ref<number | null>(null)

function openCouponModal() {
  pendingCouponId.value = selectedCoupon.value?.id ?? null
  showCouponModal.value = true
}

function confirmCoupon() {
  selectedCoupon.value = availableCoupons.value.find(c => c.id === pendingCouponId.value) ?? null
  showCouponModal.value = false
}

const payableAmount = computed(() => {
  const total = confirmResult.value?.totalAmount ?? 0
  return total - (selectedCoupon.value?.discountAmount ?? 0)
})

const canSubmit = computed(() =>
  !!confirmResult.value && confirmResult.value.items.length > 0 && !!selectedAddressId.value
)

/**
 * 重新拿一顆 orderToken + 用最新庫存/價格重新組資料。orderToken 是一次性的（POST /orders 第一步就
 * 原子消費掉，見 CreateOrderService），下單失敗（缺貨/優惠券失效/token 過期）之後那顆 token 已經燒掉，
 * 不會自動恢復——這裡負責讓使用者不用手動整頁重新整理就能拿到新 token 再試一次。
 * 已選的優惠券如果在新的可用清單裡不見了（例如額度被搶完），順便清掉，避免送出時再撞一次錯誤。
 */
async function refreshConfirm() {
  confirmResult.value = await confirmOrder()
  availableCoupons.value = confirmResult.value.items.length > 0
    ? await fetchAvailableCoupons(confirmResult.value.totalAmount)
    : []
  if (selectedCoupon.value && !availableCoupons.value.some(c => c.id === selectedCoupon.value!.id)) {
    selectedCoupon.value = null
  }
}

onMounted(async () => {
  try {
    await addressStore.fetchAddresses()
    selectedAddressId.value = addressStore.addresses.find(a => a.isDefault)?.id
      ?? addressStore.addresses[0]?.id
      ?? null

    await refreshConfirm()
    if (confirmResult.value?.items.length === 0) {
      toast.add({ title: '購物車沒有已勾選的商品', color: 'orange', timeout: 3000 })
      return navigateTo('/cart')
    }
  } catch (e: any) {
    toast.add({ title: '載入結帳資訊失敗', description: e.data?.data?.message ?? e.message, color: 'red', timeout: 3000 })
  } finally {
    loading.value = false
  }
})

async function submitOrder() {
  if (!confirmResult.value || !selectedAddressId.value) return
  const address = addressStore.addresses.find(a => a.id === selectedAddressId.value)
  if (!address) return

  submitting.value = true
  try {
    const result = await createOrder({
      orderToken: confirmResult.value.orderToken,
      receiverInfo: {
        receiverName: address.receiverName,
        receiverPhone: address.receiverPhone,
        city: address.city,
        district: address.district,
        postalCode: address.postalCode,
        street: address.street,
      },
      paymentMethod: paymentMethod.value,
      couponId: selectedCoupon.value?.id ?? null,
      remark: remark.value || undefined,
    })
    // 下單成功後 tengan-order 已經呼叫 tengan-cart 把已下單項目移除，Header 的購物車徽章數量
    // 是頁面載入時 SSR 讀進 store 的舊值，不會自己知道購物車變空了——比照 cart/index.vue
    // 既有的 cartStore.setCount() 模式，主動刷新一次，不然使用者會看到明明結完帳徽章卻沒變。
    cartStore.setCount(await fetchCartCount())
    navigateTo(`/order/pay?orderSn=${result.orderSn}`)
  } catch (e: any) {
    // 後端錯誤訊息是給工程師 debug 用的（帶 skuId/couponId 這種內部編號），不能直接塞給使用者看，
    // 這裡改用商品名稱（從重新整理前的 confirmResult 撈）重寫成看得懂的說法。
    // 注意巢狀層級：Nuxt server route 用 createError({data:...}) 包一層，H3 的錯誤回應本身
    // 又是 {statusCode,statusMessage,data} 的信封格式，所以後端原始訊息在 e.data.data，不是 e.data
    // （這裡踩到的坑：這個專案好幾個地方都寫成 e.data?.message，一路都在讀到 undefined 沒發現，
    // 因為都有 fallback 字串接住，不會直接壞掉，只是訊息一直是錯的）。
    const shortageSkuIds: number[] | undefined = e.data?.data?.shortageSkuIds
    const rawMessage: string = e.data?.data?.message ?? ''
    let title: string
    let description: string
    if (shortageSkuIds?.length) {
      const names = shortageSkuIds
        .map(id => confirmResult.value?.items.find(i => i.skuId === id)?.name)
        .filter((n): n is string => !!n)
      title = '商品庫存不足'
      description = `${names.length ? `「${names.join('、')}」` : '您選購的商品'}庫存不足，已為您更新最新資訊，請重新確認後再送出一次`
    } else if (rawMessage.includes('優惠券')) {
      title = '優惠券無法使用'
      description = '選擇的優惠券可能已被使用或不符合資格，已為您重新整理，請重新選擇後再送出一次'
    } else {
      title = '下單失敗'
      description = '訂單資訊可能已經過期，已為您重新整理，請確認內容後再送出一次'
    }
    toast.add({ title, description, color: 'red', timeout: 6000 })

    // 這次嘗試已經失敗，orderToken 已被消費掉且不會恢復——重新拿一顆新的,讓使用者不用手動整頁重新整理
    try {
      await refreshConfirm()
    } catch {
      // 重新整理本身失敗就不疊加第二個錯誤 toast 了，維持原本的下單失敗訊息就好
    }
  } finally {
    submitting.value = false
  }
}
</script>
