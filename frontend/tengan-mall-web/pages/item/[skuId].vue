<template>
  <div class="bg-gray-100 min-h-screen">

    <!-- 麵包屑 -->
    <div class="max-w-7xl mx-auto px-6 py-3">
      <nav class="text-xs text-gray-400 flex items-center gap-1">
        <NuxtLink to="/" class="hover:text-red-500">首頁</NuxtLink>
        <span>/</span>
        <span class="text-gray-600 truncate max-w-xs">{{ product?.skuName }}</span>
      </nav>
    </div>

    <div v-if="product" class="max-w-7xl mx-auto px-6 space-y-4 pb-12">

      <!-- 主區塊：圖片 + 資訊並排 -->
      <div class="bg-white rounded-lg p-8">
        <div class="grid gap-10" style="grid-template-columns: 2fr 3fr">

          <!-- 圖片區 -->
          <div class="space-y-3">
            <div class="w-full aspect-square rounded-lg overflow-hidden bg-gray-50 border border-gray-100">
              <img
                :src="product.images[activeImg]"
                :alt="product.skuName"
                class="w-full h-full object-cover"
              />
            </div>
            <div class="flex gap-2">
              <button
                v-for="(img, i) in product.images"
                :key="i"
                class="w-20 h-20 rounded border-2 overflow-hidden transition shrink-0"
                :class="activeImg === i ? 'border-red-500' : 'border-gray-200 hover:border-gray-400'"
                @click="activeImg = i"
              >
                <img :src="img" class="w-full h-full object-cover" />
              </button>
            </div>
          </div>

          <!-- 資訊區 -->
          <div class="flex flex-col gap-5">

            <!-- 名稱 -->
            <h1 class="text-2xl font-medium text-gray-800 leading-snug">
              {{ product.skuName }}
            </h1>

            <!-- 銷量 -->
            <div class="flex items-center gap-6 text-base text-gray-400 pb-4 border-b border-gray-100">
              <span>已售出 <b class="text-gray-600">{{ product.saleCount.toLocaleString() }}</b> 件</span>
              <span>庫存 <b class="text-gray-600">{{ product.stock }}</b> 件</span>
            </div>

            <!-- 價格（淡橘底） -->
            <div class="bg-orange-50 rounded-lg px-5 py-4 flex items-baseline gap-2">
              <span class="text-sm text-gray-400">優惠價</span>
              <span class="text-3xl font-bold text-red-600">
                NT$ {{ product.price.toLocaleString() }}
              </span>
            </div>

            <!-- 規格選擇 -->
            <div
              v-for="attr in product.attrs"
              :key="attr.attrName"
              class="flex gap-4"
            >
              <span class="text-base text-gray-500 w-12 pt-1.5 shrink-0">{{ attr.attrName }}</span>
              <div class="flex flex-wrap gap-2">
                <button
                  v-for="opt in attr.options"
                  :key="opt"
                  class="px-4 py-2 rounded border text-base transition"
                  :class="selectedAttrs[attr.attrName] === opt
                    ? 'border-red-500 bg-red-50 text-red-600'
                    : 'border-gray-200 text-gray-700 hover:border-red-300'"
                  @click="selectedAttrs[attr.attrName] = opt"
                >
                  {{ opt }}
                </button>
              </div>
            </div>

            <!-- 數量 -->
            <div class="flex items-center gap-4">
              <span class="text-base text-gray-500 w-12 shrink-0">數量</span>
              <div class="flex items-center border border-gray-200 rounded overflow-hidden">
                <button
                  class="w-9 h-9 flex items-center justify-center text-gray-500 hover:bg-gray-100 transition"
                  @click="qty = Math.max(1, qty - 1)"
                >－</button>
                <span class="w-12 text-center text-sm">{{ qty }}</span>
                <button
                  class="w-9 h-9 flex items-center justify-center text-gray-500 hover:bg-gray-100 transition"
                  @click="qty = Math.min(product.stock, qty + 1)"
                >＋</button>
              </div>
            </div>

            <!-- 按鈕 -->
            <div class="flex gap-3 mt-auto pt-2">
              <button
                class="flex-1 h-14 rounded border-2 border-red-500 text-red-500 font-medium text-base hover:bg-red-50 transition flex items-center justify-center gap-2"
                @click="handleAddToCart"
              >
                <UIcon name="i-heroicons-shopping-cart" class="w-5 h-5" />
                加入購物車
              </button>
              <button
                class="flex-1 h-14 rounded bg-red-500 text-white font-medium text-base hover:bg-red-600 transition"
                @click="handleBuyNow"
              >
                立即購買
              </button>
            </div>

          </div>
        </div>
      </div>

      <!-- 規格與描述 -->
      <div class="bg-white rounded-lg p-8 space-y-8">

        <div>
          <h2 class="text-base font-semibold text-gray-800 mb-5 pb-3 border-b border-gray-100">商品規格</h2>
          <div class="grid grid-cols-2 gap-x-16 gap-y-3">
            <div v-for="spec in product.specs" :key="spec.label" class="flex gap-3 text-base">
              <span class="text-gray-400 w-16 shrink-0">{{ spec.label }}</span>
              <span class="text-gray-700">{{ spec.value }}</span>
            </div>
          </div>
        </div>

        <div>
          <h2 class="text-base font-semibold text-gray-800 mb-4 pb-3 border-b border-gray-100">商品介紹</h2>
          <p class="text-base text-gray-600 leading-8">{{ product.description }}</p>
        </div>

      </div>
    </div>

    <div v-else class="text-center py-24 text-gray-400">
      <p>找不到此商品</p>
      <NuxtLink to="/" class="text-red-500 text-sm mt-2 inline-block">返回首頁</NuxtLink>
    </div>

  </div>
</template>

<script setup lang="ts">
import { getMockProductDetail } from '~/mocks/products'

const route = useRoute()
const toast = useToast()
const cartStore = useCartStore()
const { addToCart } = useCart()

const skuId = Number(route.params.skuId)
const product = getMockProductDetail(skuId)

const activeImg = ref(0)
const qty = ref(1)
const selectedAttrs = ref<Record<string, string>>({})

async function handleAddToCart() {
  if (!product) return
  const newCount = await addToCart(
    { skuId: product.skuId, skuName: product.skuName, price: product.price, image: product.images[0] },
    qty.value
  )
  cartStore.setCount(newCount)
  toast.add({
    title: '已加入購物車',
    description: product.skuName,
    color: 'green',
    timeout: 2000,
  })
}

async function handleBuyNow() {
  if (!product) return
  const newCount = await addToCart(
    { skuId: product.skuId, skuName: product.skuName, price: product.price, image: product.images[0] },
    qty.value
  )
  cartStore.setCount(newCount)
  navigateTo('/order/confirm')
}
</script>
