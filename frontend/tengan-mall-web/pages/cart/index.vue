<template>
  <div class="bg-gray-100 min-h-screen py-6">
    <div class="max-w-7xl mx-auto px-6">

      <h1 class="text-2xl font-semibold text-gray-800 mb-5">購物車</h1>

      <div v-if="items.length > 0" class="flex gap-6 items-start">

        <!-- 商品列表 -->
        <div class="flex-1 space-y-3">

          <!-- 標題列 -->
          <div class="bg-white rounded-lg px-6 py-3 flex items-center gap-4 text-sm text-gray-400">
            <input type="checkbox" :checked="allChecked" :disabled="checkableItems.length === 0" @change="toggleAll" class="w-4 h-4 accent-red-500 disabled:accent-gray-500 disabled:cursor-not-allowed" />
            <span class="flex-1">商品資訊</span>
            <span class="w-24 text-center">單價</span>
            <span class="w-28 text-center">數量</span>
            <span class="w-24 text-center">小計</span>
            <span class="w-12 text-center">操作</span>
          </div>

          <!-- 商品列 -->
          <div
            v-for="item in items"
            :key="item.itemId"
            class="bg-white rounded-lg px-6 py-4 flex items-center gap-4"
          >
            <input
              type="checkbox"
              :checked="item.checked"
              :disabled="stockShortage(item) === 'out'"
              @change="toggleItem(item)"
              class="w-4 h-4 accent-red-500 shrink-0 disabled:accent-gray-500 disabled:cursor-not-allowed"
            />
            <NuxtLink :to="item.spuId ? `/item/${item.spuId}` : '#'" class="w-20 h-20 rounded border border-gray-100 overflow-hidden shrink-0">
              <img :src="item.image" :alt="item.skuName" class="w-full h-full object-cover" />
            </NuxtLink>
            <NuxtLink :to="item.spuId ? `/item/${item.spuId}` : '#'" class="flex-1 text-sm text-gray-700 hover:text-red-500 transition line-clamp-2">
              <UBadge v-if="item.seckillPrice != null" color="red" variant="solid" size="xs" class="mr-1">限時搶購</UBadge>
              {{ item.skuName }}
              <span v-if="!item.available" class="text-xs text-gray-400">（已下架）</span>
              <span v-else-if="item.seckillPrice == null && stockShortage(item)" class="text-xs text-red-500">
                （{{ stockShortage(item) === 'out' ? '已無庫存' : `庫存不足，僅剩 ${skuStocks[item.skuId]} 件` }}）
              </span>
            </NuxtLink>
            <div class="w-24 text-center text-sm">
              <template v-if="item.seckillPrice != null">
                <div class="text-red-600 font-medium">NT$ {{ item.seckillPrice.toLocaleString() }}</div>
                <div class="text-gray-400 text-xs line-through">NT$ {{ item.price.toLocaleString() }}</div>
              </template>
              <template v-else>
                <span class="text-gray-700">NT$ {{ item.price.toLocaleString() }}</span>
              </template>
            </div>
            <div class="w-28 flex items-center justify-center">
              <div class="flex items-center border border-gray-200 rounded overflow-hidden">
                <button
                  class="w-8 h-8 flex items-center justify-center text-gray-500 hover:bg-gray-100 transition text-sm disabled:text-gray-300 disabled:hover:bg-transparent disabled:cursor-not-allowed"
                  :disabled="stockShortage(item) === 'out'"
                  @click="changeQty(item.itemId, -1)"
                >－</button>
                <span class="w-10 text-center text-sm">{{ item.count }}</span>
                <button
                  class="w-8 h-8 flex items-center justify-center text-gray-500 hover:bg-gray-100 transition text-sm disabled:text-gray-300 disabled:hover:bg-transparent disabled:cursor-not-allowed"
                  :disabled="stockShortage(item) === 'out'"
                  @click="changeQty(item.itemId, 1)"
                >＋</button>
              </div>
            </div>
            <div class="w-24 text-center text-sm font-medium text-red-600">
              NT$ {{ (unitPrice(item) * item.count).toLocaleString() }}
            </div>
            <div class="w-12 text-center">
              <button
                class="text-gray-400 hover:text-red-500 transition"
                @click="removeItem(item.itemId)"
              >
                <UIcon name="i-heroicons-trash" class="w-4 h-4" />
              </button>
            </div>
          </div>

          <!-- 底部操作列 -->
          <div class="bg-white rounded-lg px-6 py-3 flex items-center gap-4">
            <input type="checkbox" :checked="allChecked" :disabled="checkableItems.length === 0" @change="toggleAll" class="w-4 h-4 accent-red-500 disabled:accent-gray-500 disabled:cursor-not-allowed" />
            <span class="text-sm text-gray-500">全選</span>
            <button class="text-sm text-gray-400 hover:text-red-500 transition ml-2" @click="removeChecked">
              刪除選取項目
            </button>
          </div>
        </div>

        <!-- 結算面板 -->
        <div class="w-72 shrink-0 space-y-3 sticky top-24">
          <div class="bg-white rounded-lg p-5 space-y-4">
            <h2 class="text-sm font-semibold text-gray-700">訂單摘要</h2>
            <div class="space-y-2 text-sm text-gray-600">
              <div class="flex justify-between">
                <span>已選 {{ checkedCount }} 件商品</span>
                <span>NT$ {{ checkedSubtotal.toLocaleString() }}</span>
              </div>
              <div class="flex justify-between text-gray-400">
                <span>運費</span>
                <span>{{ checkedSubtotal >= 990 ? '免運' : 'NT$ 80' }}</span>
              </div>
              <div class="border-t border-gray-100 pt-2 flex justify-between font-medium text-base">
                <span>合計</span>
                <span class="text-red-600">NT$ {{ totalAmount.toLocaleString() }}</span>
              </div>
            </div>
            <p v-if="checkedSubtotal > 0 && checkedSubtotal < 990" class="text-xs text-gray-400">
              再購買 NT$ {{ (990 - checkedSubtotal).toLocaleString() }} 可享免運費
            </p>
            <p v-if="hasCheckedStockIssue" class="text-xs text-red-500 text-right">
              勾選商品中有庫存不足的項目，請調整數量或取消勾選後再結算
            </p>
            <button
              class="w-full h-11 bg-red-500 text-white rounded font-medium hover:bg-red-600 transition disabled:opacity-40 disabled:cursor-not-allowed"
              :disabled="checkedCount === 0 || hasCheckedStockIssue"
              @click="goCheckout"
            >
              去結算（{{ checkedCount }}）
            </button>
          </div>
        </div>

      </div>

      <!-- 空購物車 -->
      <div v-else class="bg-white rounded-lg py-24 text-center">
        <UIcon name="i-heroicons-shopping-cart" class="w-16 h-16 text-gray-200 mx-auto mb-4" />
        <p class="text-gray-400 mb-6">購物車是空的</p>
        <NuxtLink to="/">
          <UButton color="red" size="lg">去逛逛</UButton>
        </NuxtLink>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import type { CartItem } from '~/types/cart'

useHead({ title: '購物車' })

// 購物車要同時服務會員/訪客，不強制登入才能看（middleware: 'auth' 移除）
const cartStore = useCartStore()
const { fetchCartItems, removeFromCart, updateQty, toggleChecked, toggleAllChecked, removeCheckedItems } = useCart()
const { fetchSkuStocks } = useInventory()

const items = ref<CartItem[]>(await fetchCartItems())

// 一般倉庫存（秒殺商品的名額語意是 item.seckillRemaining，不查這裡，見 stockShortage）——
// 購物車頁本來完全沒接庫存校驗，「已無庫存的商品」是使用者實測抓到的真實缺口，這裡補上。
const skuStocks = ref<Record<number, number>>({})
async function refreshStocks() {
  const skuIds = items.value.filter(i => i.available && i.seckillPrice == null).map(i => i.skuId)
  skuStocks.value = skuIds.length > 0 ? await fetchSkuStocks(skuIds) : {}
}
await refreshStocks()

/** null=正常、'low'=數量超過庫存、'out'=完全無庫存。已下架項目不在這裡判斷（另有自己的「已下架」標示）。 */
function stockShortage(item: CartItem): 'low' | 'out' | null {
  if (!item.available) return null
  if (item.seckillPrice != null) {
    if (item.seckillRemaining == null) return null
    if (item.seckillRemaining <= 0) return 'out'
    return item.count > item.seckillRemaining ? 'low' : null
  }
  const stock = skuStocks.value[item.skuId]
  if (stock == null) return null
  if (stock <= 0) return 'out'
  return item.count > stock ? 'low' : null
}

/** 完全無庫存的商品不給勾選（只能看，不能買）——'low'（庫存不足但還有貨）維持可勾選，
 * 靠 hasCheckedStockIssue 擋去結算，讓使用者自己調整數量，兩種狀態的處理方式不一樣。 */
const checkableItems = computed(() => items.value.filter(i => stockShortage(i) !== 'out'))

/** 庫存查詢是進頁面後才做的，如果使用者之前就勾選了某項目、庫存卻在這之間變成 0，
 * 這裡要把它強制取消勾選，不然會出現「無庫存但已勾選」這種矛盾狀態。 */
async function enforceStockCheckability(list: CartItem[]): Promise<CartItem[]> {
  const toUncheck = list.filter(i => i.checked && stockShortage(i) === 'out')
  if (toUncheck.length === 0) return list
  await Promise.all(toUncheck.map(i => toggleChecked(i.itemId, false)))
  return fetchCartItems()
}
items.value = await enforceStockCheckability(items.value)

const hasCheckedStockIssue = computed(() => items.value.some(i => i.checked && stockShortage(i) !== null))

/** 有秒殺價就用秒殺價——活動結束後 item.seckillPrice 自然變 undefined，這裡不用另外判斷「是否過期」。 */
function unitPrice(item: CartItem) {
  return item.seckillPrice ?? item.price
}

const allChecked = computed(() => checkableItems.value.length > 0 && checkableItems.value.every(i => i.checked))
const checkedCount = computed(() => items.value.filter(i => i.checked).reduce((sum, i) => sum + i.count, 0))
const checkedSubtotal = computed(() => items.value.filter(i => i.checked).reduce((sum, i) => sum + unitPrice(i) * i.count, 0))
const totalAmount = computed(() => checkedSubtotal.value + (checkedSubtotal.value > 0 && checkedSubtotal.value < 990 ? 80 : 0))

// 全選/全不選只作用在可勾選的項目上——後端 checked-all 端點是全域切換，沒有「排除無庫存」的概念，
// 呼叫完之後要再跑一次 enforceStockCheckability 把被連帶勾選到的無庫存項目強制取消掉。
async function toggleAll() {
  await toggleAllChecked(!allChecked.value)
  items.value = await enforceStockCheckability(await fetchCartItems())
}

async function toggleItem(item: CartItem) {
  if (stockShortage(item) === 'out') return
  await toggleChecked(item.itemId, !item.checked)
  items.value = await fetchCartItems()
}

async function changeQty(id: number, delta: number) {
  const item = items.value.find(i => i.itemId === id)
  if (!item || item.count + delta < 1 || stockShortage(item) === 'out') return
  await updateQty(id, item.count + delta)
  items.value = await fetchCartItems()
}

async function removeItem(id: number) {
  const newCount = await removeFromCart(id)
  items.value = await fetchCartItems()
  cartStore.setCount(newCount)
}

async function removeChecked() {
  const newCount = await removeCheckedItems()
  items.value = await fetchCartItems()
  cartStore.setCount(newCount)
}

function goCheckout() {
  navigateTo('/order/confirm')
}
</script>
