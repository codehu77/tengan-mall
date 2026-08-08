<template>
  <div class="bg-gray-100 min-h-screen py-6">
    <div class="max-w-7xl mx-auto px-6">

      <h1 class="text-2xl font-semibold text-gray-800 mb-4">確認訂單</h1>

      <div class="flex gap-6 items-start">

        <!-- 左欄 -->
        <div class="flex-1 space-y-4 min-w-0">

          <!-- Block 1: 商品明細 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">商品明細</h2>
            <div class="border-t border-gray-100 pt-4 space-y-4">
              <div v-for="item in cartItems" :key="item.itemId" class="flex items-center gap-4">
                <img :src="item.image" :alt="item.skuName" class="w-20 h-20 rounded border border-gray-100 object-cover shrink-0" />
                <div class="flex-1 text-base min-w-0">
                  <p class="text-gray-700 line-clamp-2">{{ item.skuName }}</p>
                  <p class="text-gray-400 mt-1">× {{ item.count }}</p>
                </div>
                <p class="text-base font-medium text-gray-800 shrink-0">NT$ {{ (item.price * item.count).toLocaleString() }}</p>
              </div>
            </div>
          </div>

          <!-- Block 2: 購買人資訊 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">購買人資訊</h2>
            <div class="border-t border-gray-100 pt-4 space-y-4">

              <div>
                <label class="block text-sm text-gray-600 mb-1">購買人姓名</label>
                <input v-model="orderer.name" type="text" placeholder="請輸入姓名"
                  class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm text-gray-600 mb-1">手機</label>
                  <input v-model="orderer.phone" type="tel" placeholder="0912345678"
                    class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
                </div>
                <div>
                  <label class="block text-sm text-gray-600 mb-1">市話（非必填）</label>
                  <input v-model="orderer.landline" type="tel" placeholder="0212345678#1234"
                    class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700 placeholder-gray-300" />
                </div>
              </div>

              <div>
                <label class="block text-sm text-gray-600 mb-1">發票地址</label>
                <div class="grid grid-cols-2 gap-4 mb-3">
                  <div class="relative">
                    <select v-model="orderer.city"
                      class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700 bg-white appearance-none pr-9">
                      <option v-for="city in taiwanCities" :key="city" :value="city">{{ city }}</option>
                    </select>
                    <UIcon name="i-heroicons-chevron-down" class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
                  </div>
                  <div class="relative">
                    <select v-model="orderer.district"
                      class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700 bg-white appearance-none pr-9">
                      <option v-for="d in districts" :key="d" :value="d">{{ d }}</option>
                    </select>
                    <UIcon name="i-heroicons-chevron-down" class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
                  </div>
                </div>
                <input v-model="orderer.addressDetail" type="text" placeholder="請輸入詳細地址"
                  class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
              </div>

            </div>
          </div>

          <!-- Block 3: 收件人資訊 -->
          <div class="bg-white rounded-lg p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-bold text-gray-800">收件人資訊</h2>
              <button
                class="text-sm text-red-500 border border-red-300 rounded-lg px-3 py-1 hover:bg-red-50 transition"
                @click="copyFromOrderer"
              >
                同訂購人
              </button>
            </div>
            <div class="border-t border-gray-100 pt-4 space-y-4">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm text-gray-500 mb-1">姓名</label>
                  <input v-model="recipient.name" type="text" placeholder="請輸入姓名"
                    class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
                </div>
                <div>
                  <label class="block text-sm text-gray-500 mb-1">手機</label>
                  <input v-model="recipient.phone" type="tel" placeholder="請輸入手機號碼"
                    class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
                </div>
              </div>
              <div>
                <label class="block text-sm text-gray-500 mb-1">地址</label>
                <div class="grid grid-cols-2 gap-4 mb-3">
                  <div class="relative">
                    <select v-model="recipient.city"
                      class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700 bg-white appearance-none pr-9">
                      <option v-for="city in taiwanCities" :key="city" :value="city">{{ city }}</option>
                    </select>
                    <UIcon name="i-heroicons-chevron-down" class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
                  </div>
                  <div class="relative">
                    <select v-model="recipient.district"
                      class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700 bg-white appearance-none pr-9">
                      <option v-for="d in recipientDistricts" :key="d" :value="d">{{ d }}</option>
                    </select>
                    <UIcon name="i-heroicons-chevron-down" class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
                  </div>
                </div>
                <input v-model="recipient.addressDetail" type="text" placeholder="請輸入詳細地址"
                  class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
              </div>
              <div>
                <label class="block text-sm text-gray-500 mb-1">備註</label>
                <textarea
                  v-model="recipient.note"
                  placeholder="填寫給賣家的留言（選填）"
                  rows="3"
                  class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 resize-none text-gray-700 placeholder-gray-300"
                />
              </div>
            </div>
            <div class="mt-4 flex items-center gap-2 text-sm text-gray-500 bg-gray-50 rounded-lg px-4 py-3">
              <UIcon name="i-heroicons-truck" class="w-4 h-4 shrink-0 text-gray-400" />
              <span>配送方式為宅配，預計 3–5 個工作天送達</span>
            </div>
          </div>

          <!-- Block 4: 付款方式 -->
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">付款方式</h2>
            <div class="border-t border-gray-100 pt-4 space-y-3">

              <label
                class="flex items-center gap-3 p-4 border rounded-lg cursor-pointer transition"
                :class="paymentMethod === 'credit_card' ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
              >
                <input type="radio" value="credit_card" v-model="paymentMethod" class="accent-red-500" />
                <UIcon name="i-heroicons-credit-card" class="w-5 h-5 text-gray-500 shrink-0" />
                <span class="text-base text-gray-700">信用卡付款</span>
              </label>

              <label
                class="flex items-center gap-3 p-4 border rounded-lg cursor-pointer transition"
                :class="paymentMethod === 'mobile' ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
              >
                <input type="radio" value="mobile" v-model="paymentMethod" class="accent-red-500" />
                <UIcon name="i-heroicons-device-phone-mobile" class="w-5 h-5 text-gray-500 shrink-0" />
                <div class="flex-1">
                  <span class="text-base text-gray-700">行動支付</span>
                  <p v-if="paymentMethod === 'mobile' && selectedMobilePay" class="text-sm text-red-500 mt-0.5">已選擇：{{ selectedMobilePay }}</p>
                  <p v-else-if="paymentMethod === 'mobile'" class="text-sm text-gray-400 mt-0.5">請選擇付款方式</p>
                </div>
                <button
                  v-if="paymentMethod === 'mobile'"
                  class="text-sm text-red-500 border border-red-300 rounded-lg px-3 py-1 hover:bg-red-50 transition shrink-0"
                  @click.prevent="showMobilePayModal = true"
                >
                  選擇
                </button>
              </label>

              <label
                class="flex items-center gap-3 p-4 border rounded-lg cursor-pointer transition"
                :class="paymentMethod === 'cod' ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
              >
                <input type="radio" value="cod" v-model="paymentMethod" class="accent-red-500" />
                <UIcon name="i-heroicons-banknotes" class="w-5 h-5 text-gray-500 shrink-0" />
                <span class="text-base text-gray-700">貨到付款</span>
              </label>

            </div>
          </div>

        </div>

        <!-- 右欄：結帳明細 + 確認付款（sticky） -->
        <div class="w-[420px] shrink-0 sticky top-6 space-y-4">
          <div class="bg-white rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">結帳明細</h2>

            <div class="border-t border-gray-100 pt-4 space-y-4">

              <!-- 商品原價總金額 -->
              <div class="flex justify-between items-center text-base text-gray-700">
                <span>商品原價總金額</span>
                <span class="font-semibold">${{ subtotal.toLocaleString() }}</span>
              </div>

              <!-- 折扣 -->
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
                  <span>${{ discount.toLocaleString() }}</span>
                </div>
              </div>

              <!-- 優惠券 -->
              <div>
                <div class="flex items-center justify-between">
                  <span class="text-base text-gray-700">優惠券</span>
                  <div class="flex items-center gap-2">
                    <span v-if="selectedCoupon" class="text-base font-semibold text-red-500">- ${{ selectedCoupon.discount }}</span>
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
                  <span>{{ selectedCoupon.name }}</span>
                  <span>- ${{ selectedCoupon.discount }}</span>
                </div>
              </div>

              <!-- 運費 -->
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

            <!-- 結帳金額 -->
            <div class="border-t border-gray-200 mt-4 pt-4 flex justify-between items-center">
              <span class="text-base font-semibold text-gray-800">結帳金額</span>
              <span class="text-red-500 text-xl font-bold">${{ total.toLocaleString() }}</span>
            </div>
          </div>

          <button
            class="w-full h-14 bg-red-500 text-white rounded-lg font-semibold text-lg hover:bg-red-600 transition"
            @click="submitOrder"
          >
            確認訂單
          </button>
        </div>

      </div>
    </div>

    <!-- 行動支付選擇彈窗 -->
    <UModal v-model="showMobilePayModal">
      <UCard>
        <template #header>
          <p class="text-base font-semibold text-gray-700">選擇行動支付方式</p>
        </template>
        <div class="space-y-3 py-2">
          <label
            class="flex items-center gap-3 p-4 border rounded-lg cursor-pointer transition"
            :class="pendingMobilePay === 'LINE Pay' ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
          >
            <input type="radio" value="LINE Pay" v-model="pendingMobilePay" class="accent-red-500" />
            <span class="w-7 h-7 rounded bg-green-500 text-white text-xs flex items-center justify-center font-bold shrink-0">L</span>
            <span class="text-base text-gray-700">LINE Pay</span>
          </label>
        </div>
        <template #footer>
          <div class="flex justify-end gap-3">
            <button
              class="px-5 py-2 text-sm text-gray-500 border border-gray-200 rounded-lg hover:bg-gray-50 transition"
              @click="showMobilePayModal = false"
            >
              取消
            </button>
            <button
              class="px-5 py-2 text-sm text-white bg-red-500 rounded-lg hover:bg-red-600 transition disabled:opacity-40"
              :disabled="!pendingMobilePay"
              @click="confirmMobilePay"
            >
              確認
            </button>
          </div>
        </template>
      </UCard>
    </UModal>

    <!-- 優惠券選擇彈窗 -->
    <UModal v-model="showCouponModal">
      <UCard>
        <template #header>
          <p class="text-base font-semibold text-gray-700">選擇優惠券</p>
        </template>

        <div class="space-y-4 py-2">

          <!-- 目前套用的優惠券 -->
          <div>
            <p class="text-sm font-semibold text-gray-500 mb-2">目前套用的優惠券</p>
            <div v-if="selectedCoupon" class="flex items-center justify-between bg-red-50 border border-red-200 rounded-lg px-4 py-3">
              <div>
                <p class="text-base text-gray-700 font-medium">{{ selectedCoupon.name }}</p>
                <p class="text-sm text-gray-400 mt-0.5">{{ selectedCoupon.description }}</p>
              </div>
              <span class="text-base font-semibold text-red-500 shrink-0">- ${{ selectedCoupon.discount }}</span>
            </div>
            <div v-else class="bg-gray-50 border border-gray-100 rounded-lg px-4 py-3 text-sm text-gray-400">
              未使用任何優惠券
            </div>
          </div>

          <div class="border-t border-gray-100" />

          <!-- 不使用優惠券 -->
          <label
            class="flex items-center gap-3 p-4 border rounded-lg cursor-pointer transition"
            :class="pendingCoupon === null ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-gray-300'"
          >
            <input type="radio" :value="null" v-model="pendingCoupon" class="accent-red-500" />
            <span class="text-base text-gray-500">不使用優惠券</span>
          </label>

          <!-- 各優惠券 -->
          <label
            v-for="coupon in mockCoupons"
            :key="coupon.id"
            class="flex items-center gap-3 p-4 border rounded-lg transition"
            :class="[
              !isCouponApplicable(coupon) ? 'opacity-40 cursor-not-allowed border-gray-100' :
              pendingCoupon?.id === coupon.id ? 'border-red-400 bg-red-50 cursor-pointer' : 'border-gray-200 hover:border-gray-300 cursor-pointer'
            ]"
          >
            <input
              type="radio"
              :value="coupon"
              v-model="pendingCoupon"
              :disabled="!isCouponApplicable(coupon)"
              class="accent-red-500"
            />
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <span class="text-base text-gray-700">{{ coupon.name }}</span>
                <span v-if="isBestCoupon(coupon)" class="text-xs bg-red-500 text-white px-1.5 py-0.5 rounded">推薦</span>
              </div>
              <p class="text-sm text-gray-400 mt-0.5">{{ coupon.description }}</p>
            </div>
            <span class="text-base font-semibold text-red-500 shrink-0">- ${{ coupon.discount }}</span>
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
definePageMeta({ middleware: 'auth' })

const { fetchCartItems } = useCart()
const allCartItems = await fetchCartItems()
const cartItems = allCartItems.filter(i => i.checked)

const orderer = reactive({
  name: '王小明',
  phone: '0912345678',
  landline: '',
  city: '新北市',
  district: '三重區',
  addressDetail: '123路1段100巷100號',
})

const districts = computed(() => districtMap[orderer.city] ?? [])

watch(() => orderer.city, () => {
  orderer.district = districts.value[0] ?? ''
})

const recipient = reactive({
  name: '',
  phone: '',
  city: '台北市',
  district: '中正區',
  addressDetail: '',
  note: '',
})

const recipientDistricts = computed(() => districtMap[recipient.city] ?? [])

watch(() => recipient.city, () => {
  recipient.district = recipientDistricts.value[0] ?? ''
})

function copyFromOrderer() {
  recipient.name = orderer.name
  recipient.phone = orderer.phone
  recipient.city = orderer.city
  recipient.district = orderer.district
  recipient.addressDetail = orderer.addressDetail
}

const paymentMethod = ref<'credit_card' | 'mobile' | 'cod'>('credit_card')
const showMobilePayModal = ref(false)
const selectedMobilePay = ref('')
const pendingMobilePay = ref('')

watch(paymentMethod, (val) => {
  if (val === 'mobile') {
    pendingMobilePay.value = selectedMobilePay.value
    showMobilePayModal.value = true
  }
})

function confirmMobilePay() {
  selectedMobilePay.value = pendingMobilePay.value
  showMobilePayModal.value = false
}

interface Coupon {
  id: number
  name: string
  discount: number
  minAmount: number
  description: string
}

const mockCoupons: Coupon[] = [
  { id: 1, name: '新會員優惠券', discount: 100, minAmount: 0, description: '無門檻折 $100' },
  { id: 2, name: '限時折扣券', discount: 50, minAmount: 0, description: '無門檻折 $50' },
  { id: 3, name: '週年慶優惠', discount: 200, minAmount: 1000, description: '滿 $1000 折 $200' },
]

const subtotal = computed(() =>
  cartItems.reduce((sum, i) => sum + i.price * i.count, 0)
)
const discount = computed(() => Math.floor(subtotal.value * 0.1))
const shippingFee = computed(() => subtotal.value < 600 ? 75 : 0)

const showCouponModal = ref(false)
const selectedCoupon = ref<Coupon | null>(null)
const pendingCoupon = ref<Coupon | null>(null)

function isCouponApplicable(coupon: Coupon) {
  return subtotal.value >= coupon.minAmount
}

const bestCoupon = computed(() => {
  const applicable = mockCoupons.filter(isCouponApplicable)
  if (!applicable.length) return null
  return applicable.reduce((a, b) => a.discount >= b.discount ? a : b)
})

function isBestCoupon(coupon: Coupon) {
  return bestCoupon.value?.id === coupon.id
}

onMounted(() => {
  selectedCoupon.value = bestCoupon.value
})

function openCouponModal() {
  pendingCoupon.value = selectedCoupon.value
  showCouponModal.value = true
}

function confirmCoupon() {
  selectedCoupon.value = pendingCoupon.value
  showCouponModal.value = false
}

const couponDiscount = computed(() => selectedCoupon.value?.discount ?? 0)
const total = computed(() => subtotal.value - discount.value - couponDiscount.value + shippingFee.value)

const showDiscount = ref(true)
const showShipping = ref(true)

function submitOrder() {
  navigateTo('/order/pay?orderSn=TG20240628000001')
}
</script>
