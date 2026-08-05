<template>
  <div class="bg-gray-100 min-h-screen">

    <!-- 麵包屑 -->
    <div class="max-w-7xl mx-auto px-6 py-3">
      <nav class="text-xs text-gray-400 flex items-center gap-1">
        <NuxtLink to="/" class="hover:text-red-500">首頁</NuxtLink>
        <span>/</span>
        <span class="text-gray-600 truncate max-w-xs">{{ spu?.name }}</span>
      </nav>
    </div>

    <div v-if="spu && currentSku" class="max-w-7xl mx-auto px-6 space-y-4 pb-12">

      <!-- 主區塊：圖片 + 資訊並排 -->
      <div class="bg-white rounded-lg p-8">
        <div class="grid gap-10" style="grid-template-columns: 2fr 3fr">

          <!-- 圖片區 -->
          <div class="space-y-3">
            <div class="w-full aspect-square rounded-lg overflow-hidden bg-gray-50 border border-gray-100">
              <img
                :src="images[activeImg]"
                :alt="currentSku.name"
                class="w-full h-full object-cover"
              />
            </div>
            <div class="flex gap-2">
              <button
                v-for="(img, i) in images"
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

            <!-- 名稱：SPU 標題固定，不隨規格切換而變 -->
            <h1 class="text-2xl font-medium text-gray-800 leading-snug">
              {{ spu.name }}
            </h1>

            <!-- 銷量 -->
            <div class="flex items-center gap-6 text-base text-gray-400 pb-4 border-b border-gray-100">
              <span>已售出 <b class="text-gray-600">{{ currentSku.saleCount.toLocaleString() }}</b> 件</span>
            </div>

            <!-- 價格（淡橘底） -->
            <div class="bg-orange-50 rounded-lg px-5 py-4 flex items-baseline gap-2">
              <span class="text-sm text-gray-400">優惠價</span>
              <span class="text-3xl font-bold text-red-600">
                NT$ {{ currentSku.price.toLocaleString() }}
              </span>
            </div>

            <!-- 規格選擇：純前端狀態切換，不會導覽到新網址 -->
            <div
              v-for="attr in attrOptions"
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
                  @click="selectAttr(attr.attrName, opt)"
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
                  @click="qty = qty + 1"
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

        <div v-if="specs.length > 0">
          <h2 class="text-base font-semibold text-gray-800 mb-5 pb-3 border-b border-gray-100">商品規格</h2>
          <div class="grid grid-cols-2 gap-x-16 gap-y-3">
            <div v-for="spec in specs" :key="spec.label" class="flex gap-3 text-base">
              <span class="text-gray-400 w-16 shrink-0">{{ spec.label }}</span>
              <span class="text-gray-700">{{ spec.value }}</span>
            </div>
          </div>
        </div>

        <div>
          <h2 class="text-base font-semibold text-gray-800 mb-4 pb-3 border-b border-gray-100">商品介紹</h2>
          <div class="text-base text-gray-600 leading-8" v-html="sanitizedDescription" />
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
import DOMPurify from 'isomorphic-dompurify'
import { useProductDetail } from '~/composables/useProductDetail'

const route = useRoute()
const toast = useToast()
const cartStore = useCartStore()
const { addToCart } = useCart()

const spuId = Number(route.params.spuId)
const { data: spu } = await useProductDetail(spuId)

const activeImg = ref(0)
const qty = ref(1)

const skus = computed(() => spu.value?.skus ?? [])

// 預設變體：sort 值最小的那顆（後台精靈本來就有的排序欄位，跟管理端的預設呈現順序一致）
const defaultSkuId = skus.value.length > 0
  ? skus.value.reduce((min, s) => (s.sort < min.sort ? s : min)).id
  : null
const selectedSkuId = ref(defaultSkuId)

const currentSku = computed(() => skus.value.find(s => s.id === selectedSkuId.value) ?? skus.value[0])

// spu 共通圖 + 目前這顆 sku 的專屬圖，切換 sku 時 activeImg 歸零，等同大圖/縮圖跳到對應 sku 的圖
const images = computed(() => {
  const sku = currentSku.value
  if (!sku) return []
  const urls = [sku.mainImage, ...sku.images.map(i => i.imageUrl), ...(spu.value?.images.map(i => i.imageUrl) ?? [])]
  return Array.from(new Set(urls)).filter(Boolean)
})

// 銷售屬性（顏色/容量...）的可選值，彙整同一顆 spu 底下所有 sku 出現過的組合
const attrOptions = computed(() => {
  const map = new Map<string, string[]>()
  for (const sku of skus.value) {
    for (const av of sku.saleAttrValues) {
      const options = map.get(av.attrName) ?? []
      if (!options.includes(av.attrValue)) options.push(av.attrValue)
      map.set(av.attrName, options)
    }
  }
  return Array.from(map.entries()).map(([attrName, options]) => ({ attrName, options }))
})

const selectedAttrs = computed<Record<string, string>>(() => {
  const result: Record<string, string> = {}
  for (const av of currentSku.value?.saleAttrValues ?? []) result[av.attrName] = av.attrValue
  return result
})

// 純前端狀態切換，不 router.replace——MOMO 那種「選規格不換網址」的體驗
function selectAttr(attrName: string, value: string) {
  const target = { ...selectedAttrs.value, [attrName]: value }
  const match = skus.value.find(sku =>
    sku.saleAttrValues.length === Object.keys(target).length
    && sku.saleAttrValues.every(av => target[av.attrName] === av.attrValue)
  )
  if (!match) return
  selectedSkuId.value = match.id
  activeImg.value = 0
}

const specs = computed(() => (spu.value?.attrValues ?? []).map(v => ({ label: v.attrName, value: v.attrValue })))
const sanitizedDescription = computed(() => DOMPurify.sanitize(spu.value?.description ?? ''))

async function handleAddToCart() {
  const sku = currentSku.value
  if (!sku) return
  const newCount = await addToCart(
    { skuId: sku.id, skuName: sku.name, price: sku.price, image: images.value[0] ?? sku.mainImage },
    qty.value
  )
  cartStore.setCount(newCount)
  toast.add({
    title: '已加入購物車',
    description: sku.name,
    color: 'green',
    timeout: 2000,
  })
}

async function handleBuyNow() {
  const sku = currentSku.value
  if (!sku) return
  const newCount = await addToCart(
    { skuId: sku.id, skuName: sku.name, price: sku.price, image: images.value[0] ?? sku.mainImage },
    qty.value
  )
  cartStore.setCount(newCount)
  navigateTo('/order/confirm')
}
</script>
