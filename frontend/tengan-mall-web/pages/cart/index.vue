<template>
  <div class="bg-gray-100 min-h-screen py-6">
    <div class="max-w-7xl mx-auto px-6">

      <h1 class="text-2xl font-semibold text-gray-800 mb-5">購物車</h1>

      <div v-if="items.length > 0" class="flex gap-6 items-start">

        <!-- 商品列表 -->
        <div class="flex-1 space-y-3">

          <!-- 標題列 -->
          <div class="bg-white rounded-lg px-6 py-3 flex items-center gap-4 text-sm text-gray-400">
            <input type="checkbox" :checked="allChecked" @change="toggleAll" class="w-4 h-4 accent-red-500" />
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
              @change="toggleItem(item)"
              class="w-4 h-4 accent-red-500 shrink-0"
            />
            <NuxtLink :to="item.spuId ? `/item/${item.spuId}` : '#'" class="w-20 h-20 rounded border border-gray-100 overflow-hidden shrink-0">
              <img :src="item.image" :alt="item.skuName" class="w-full h-full object-cover" />
            </NuxtLink>
            <NuxtLink :to="item.spuId ? `/item/${item.spuId}` : '#'" class="flex-1 text-sm text-gray-700 hover:text-red-500 transition line-clamp-2">
              {{ item.skuName }}
              <span v-if="!item.available" class="text-xs text-gray-400">（已下架）</span>
            </NuxtLink>
            <div class="w-24 text-center text-sm text-gray-700">
              NT$ {{ item.price.toLocaleString() }}
            </div>
            <div class="w-28 flex items-center justify-center">
              <div class="flex items-center border border-gray-200 rounded overflow-hidden">
                <button
                  class="w-8 h-8 flex items-center justify-center text-gray-500 hover:bg-gray-100 transition text-sm"
                  @click="changeQty(item.itemId, -1)"
                >－</button>
                <span class="w-10 text-center text-sm">{{ item.count }}</span>
                <button
                  class="w-8 h-8 flex items-center justify-center text-gray-500 hover:bg-gray-100 transition text-sm"
                  @click="changeQty(item.itemId, 1)"
                >＋</button>
              </div>
            </div>
            <div class="w-24 text-center text-sm font-medium text-red-600">
              NT$ {{ (item.price * item.count).toLocaleString() }}
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
            <input type="checkbox" :checked="allChecked" @change="toggleAll" class="w-4 h-4 accent-red-500" />
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
            <button
              class="w-full h-11 bg-red-500 text-white rounded font-medium hover:bg-red-600 transition disabled:opacity-40 disabled:cursor-not-allowed"
              :disabled="checkedCount === 0"
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

// 購物車要同時服務會員/訪客，不強制登入才能看（middleware: 'auth' 移除）
const cartStore = useCartStore()
const { fetchCartItems, removeFromCart, updateQty, toggleChecked, toggleAllChecked, removeCheckedItems } = useCart()

const items = ref<CartItem[]>(await fetchCartItems())

const allChecked = computed(() => items.value.length > 0 && items.value.every(i => i.checked))
const checkedCount = computed(() => items.value.filter(i => i.checked).reduce((sum, i) => sum + i.count, 0))
const checkedSubtotal = computed(() => items.value.filter(i => i.checked).reduce((sum, i) => sum + i.price * i.count, 0))
const totalAmount = computed(() => checkedSubtotal.value + (checkedSubtotal.value > 0 && checkedSubtotal.value < 990 ? 80 : 0))

async function toggleAll() {
  await toggleAllChecked(!allChecked.value)
  items.value = await fetchCartItems()
}

async function toggleItem(item: CartItem) {
  await toggleChecked(item.itemId, !item.checked)
  items.value = await fetchCartItems()
}

async function changeQty(id: number, delta: number) {
  const item = items.value.find(i => i.itemId === id)
  if (!item || item.count + delta < 1) return
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
