<template>
  <header class="bg-white shadow-sm sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between gap-4">

      <!-- Logo -->
      <NuxtLink to="/" class="text-2xl font-bold text-red-600 shrink-0">
        天願商城
      </NuxtLink>

      <!-- 搜尋框 -->
      <div class="flex-1 max-w-xl">
        <UInput
          v-model="keyword"
          placeholder="搜尋商品..."
          icon="i-heroicons-magnifying-glass"
          size="lg"
          @keyup.enter="handleSearch"
        />
      </div>

      <!-- 右側操作區 -->
      <div class="flex items-center gap-4 shrink-0">
        <!-- 購物車 -->
        <div class="relative" @mouseenter="openMiniCart" @mouseleave="closeMiniCart">
          <NuxtLink to="/cart" class="relative text-gray-600 hover:text-red-600 block py-2">
            <UIcon name="i-heroicons-shopping-cart" class="w-6 h-6" />
            <span
              v-if="cartCount > 0"
              class="absolute -top-0.5 -right-1.5 bg-red-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center"
            >
              {{ cartCount }}
            </span>
          </NuxtLink>

          <!-- Hover 下拉預覽 -->
          <Transition
            enter-active-class="transition ease-out duration-100"
            enter-from-class="opacity-0 translate-y-1"
            enter-to-class="opacity-100 translate-y-0"
            leave-active-class="transition ease-in duration-75"
            leave-from-class="opacity-100"
            leave-to-class="opacity-0"
          >
            <div
              v-if="miniCartOpen"
              class="absolute right-0 top-full mt-2 w-[440px] bg-white rounded-lg shadow-xl border border-gray-100 z-50"
            >
              <!-- 箭頭 -->
              <div class="absolute -top-1.5 right-4 w-3 h-3 bg-white border-t border-l border-gray-100 rotate-45" />

              <template v-if="miniCartLoading">
                <div class="py-14 text-center text-sm text-gray-400">載入中...</div>
              </template>

              <template v-else-if="miniCartItems.length > 0">
                <p class="px-5 pt-4 pb-2 text-sm text-gray-400">最近加入的商品</p>
                <div class="max-h-96 overflow-y-auto">
                  <NuxtLink
                    v-for="item in miniCartItems"
                    :key="item.itemId"
                    :to="`/item/${item.skuId}`"
                    class="flex items-center gap-4 px-5 py-3 hover:bg-gray-50 transition"
                  >
                    <img :src="item.image" :alt="item.skuName" class="w-14 h-14 rounded border border-gray-100 object-cover shrink-0" />
                    <span class="flex-1 text-base text-gray-700 truncate">{{ item.skuName }}</span>
                    <span class="text-base text-red-500 font-medium shrink-0">${{ item.price.toLocaleString() }}</span>
                  </NuxtLink>
                </div>
                <div class="flex items-center justify-between px-5 py-3.5 border-t border-gray-100 bg-gray-50 rounded-b-lg">
                  <span class="text-xs text-gray-400">
                    {{ remainingCount > 0 ? `${remainingCount} 件商品尚未展示` : '' }}
                  </span>
                  <UButton to="/cart" color="red" size="md">查看我的購物車</UButton>
                </div>
              </template>

              <template v-else>
                <div class="py-14 text-center">
                  <UIcon name="i-heroicons-shopping-cart" class="w-10 h-10 text-gray-200 mx-auto mb-2" />
                  <p class="text-base text-gray-400">購物車是空的</p>
                </div>
              </template>
            </div>
          </Transition>
        </div>

        <!-- 登入狀態 -->
        <template v-if="authStore.isLoggedIn">
          <UDropdown :items="userMenuItems">
            <UButton variant="ghost" color="gray">
              {{ authStore.username }}
              <UIcon name="i-heroicons-chevron-down" class="w-4 h-4" />
            </UButton>
          </UDropdown>
        </template>
        <template v-else>
          <NuxtLink to="/login">
            <UButton variant="ghost" color="gray" size="md">登入</UButton>
          </NuxtLink>
          <NuxtLink to="/register">
            <UButton color="red" size="md">註冊</UButton>
          </NuxtLink>
        </template>
      </div>

    </div>
  </header>
</template>

<script setup lang="ts">
import type { CartItem } from '~/mocks/cart'

const keyword = ref('')
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const { fetchMiniCart } = useCart()

const cartCount = computed(() => cartStore.count)

const miniCartOpen = ref(false)
const miniCartLoading = ref(false)
const miniCartItems = ref<CartItem[]>([])
const miniCartTotal = ref(0)
const remainingCount = computed(() => Math.max(0, miniCartTotal.value - miniCartItems.value.length))

let closeTimer: ReturnType<typeof setTimeout> | undefined

async function openMiniCart() {
  clearTimeout(closeTimer)
  miniCartOpen.value = true
  miniCartLoading.value = true
  const { items, total } = await fetchMiniCart(5)
  miniCartItems.value = items
  miniCartTotal.value = total
  miniCartLoading.value = false
}

function closeMiniCart() {
  closeTimer = setTimeout(() => {
    miniCartOpen.value = false
  }, 150)
}

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: keyword.value } })
  }
}

const userMenuItems = [
  [
    { label: '我的訂單', icon: 'i-heroicons-clipboard-document-list', to: '/order/list' },
    { label: '我的點數', icon: 'i-heroicons-wallet', to: '/member/points' },
  ],
  [
    { label: '登出', icon: 'i-heroicons-arrow-right-on-rectangle', click: () => authStore.logout() },
  ],
]
</script>
