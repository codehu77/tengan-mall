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

            <!-- 價格：即將開賣 > 有活躍秒殺(搶購價+倒數) > 一般優惠價，三者互斥 -->
            <div v-if="isNotYetOnSale" class="bg-gray-50 rounded-lg px-5 py-4 space-y-2">
              <div class="flex items-center gap-2">
                <UBadge color="gray" variant="solid">即將開賣</UBadge>
                <span class="text-xs text-gray-500 font-mono">{{ saleStartText }}（倒數 {{ saleHh }}:{{ saleMm }}:{{ saleSs }}）</span>
              </div>
              <div class="flex items-baseline gap-2">
                <span class="text-3xl font-bold text-gray-500">
                  NT$ {{ currentSku.price.toLocaleString() }}
                </span>
              </div>
              <p v-if="purchaseLimitPerUser" class="text-xs text-gray-500">每人限購 {{ purchaseLimitPerUser }} 件</p>
            </div>
            <div v-else-if="activeSeckillSku" class="bg-red-50 rounded-lg px-5 py-4 space-y-2">
              <div class="flex items-center gap-2">
                <UBadge color="red" variant="solid">限時搶購</UBadge>
                <span class="text-xs text-gray-500 font-mono">距結束 {{ hh }}:{{ mm }}:{{ ss }}</span>
              </div>
              <div class="flex items-baseline gap-2">
                <span class="text-3xl font-bold text-red-600">
                  NT$ {{ activeSeckillSku.seckillPrice.toLocaleString() }}
                </span>
                <span class="text-sm text-gray-400 line-through">
                  NT$ {{ currentSku.price.toLocaleString() }}
                </span>
              </div>
              <p class="text-xs text-gray-500">
                剩餘 {{ activeSeckillSku.remaining }} 件・每人限購 {{ activeSeckillSku.limitPerUser }} 件
              </p>
            </div>
            <div v-else class="bg-orange-50 rounded-lg px-5 py-4 space-y-1">
              <div class="flex items-baseline gap-2">
                <span class="text-sm text-gray-400">優惠價</span>
                <span class="text-3xl font-bold text-red-600">
                  NT$ {{ currentSku.price.toLocaleString() }}
                </span>
              </div>
              <p v-if="purchaseLimitPerUser" class="text-xs text-gray-500">每人限購 {{ purchaseLimitPerUser }} 件</p>
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
                  :class="isOptionSoldOut(attr.attrName, opt)
                    ? 'border-gray-100 bg-gray-50 text-gray-300 cursor-not-allowed'
                    : selectedAttrs[attr.attrName] === opt
                      ? 'border-red-500 bg-red-50 text-red-600'
                      : isOptionOutOfStock(attr.attrName, opt)
                        ? 'border-gray-200 text-gray-400 hover:border-red-300'
                        : 'border-gray-200 text-gray-700 hover:border-red-300'"
                  :disabled="isOptionSoldOut(attr.attrName, opt)"
                  @click="selectAttr(attr.attrName, opt)"
                >
                  {{ opt }}
                  <span v-if="isOptionSoldOut(attr.attrName, opt)" class="text-xs">（已售完）</span>
                  <span v-else-if="isOptionOutOfStock(attr.attrName, opt)" class="text-xs">（缺貨）</span>
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
                  :disabled="!activeSeckillSku && qtyMax !== null && qty >= qtyMax"
                  :class="{ 'opacity-30 cursor-not-allowed': !activeSeckillSku && qtyMax !== null && qty >= qtyMax }"
                  @click="qty = qty + 1"
                >＋</button>
              </div>
              <!-- 秒殺 SKU 的庫存語意是搶購名額（remaining），不是這裡的一般倉庫存，兩者互斥顯示 -->
              <span v-if="!activeSeckillSku && !isNotYetOnSale && availableStock !== null && availableStock <= 0" class="text-sm text-gray-400">
                庫存不足
              </span>
              <span v-else-if="!activeSeckillSku && !isNotYetOnSale && availableStock !== null" class="text-sm text-gray-400">
                庫存 {{ availableStock }} 件
              </span>
            </div>

            <!-- 按鈕 -->
            <div class="flex gap-3 mt-auto pt-2">
              <button
                class="flex-1 h-14 rounded border-2 border-red-500 text-red-500 font-medium text-base hover:bg-red-50 transition flex items-center justify-center gap-2 disabled:opacity-40 disabled:cursor-not-allowed disabled:hover:bg-transparent"
                :disabled="isPurchaseDisabled"
                @click="handleAddToCart"
              >
                <UIcon name="i-heroicons-shopping-cart" class="w-5 h-5" />
                {{ isNotYetOnSale ? '即將開賣' : isOutOfStock ? '庫存不足' : '加入購物車' }}
              </button>
              <button
                class="flex-1 h-14 rounded bg-red-500 text-white font-medium text-base hover:bg-red-600 transition disabled:opacity-40 disabled:cursor-not-allowed disabled:hover:bg-red-500"
                :disabled="isPurchaseDisabled"
                @click="handleBuyNow"
              >
                {{ isNotYetOnSale ? '即將開賣' : isOutOfStock ? '庫存不足' : '立即購買' }}
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
import type { SkuStockInfo } from '~/composables/useInventory'

const route = useRoute()
const toast = useToast()
const cartStore = useCartStore()
const { addToCart } = useCart()
const { fetchSkuStocks } = useInventory()

const spuId = Number(route.params.spuId)
const { data: spu } = await useProductDetail(spuId)
const { data: seckillData } = await useSeckill()

useHead({ title: computed(() => spu.value?.name || '商品詳情') })

const activeImg = ref(0)
const qty = ref(1)

const skus = computed(() => spu.value?.skus ?? [])

// 一次查回這個 SPU 底下所有 sku 的一般倉庫存（連同開賣時間/限購資訊），讓規格選項按鈕能像秒殺售完一樣
// 即時反灰（而不是等使用者選到那顆、按下加入購物車才發現不能買）。秒殺 sku 的庫存語意是搶購名額，
// 不查這裡，見下面 activeSeckillSkuIds 的排除邏輯。
const skuStocks = ref<Record<number, SkuStockInfo>>({})
if (skus.value.length > 0) {
  skuStocks.value = await fetchSkuStocks(skus.value.map(s => s.id))
}

// 預設變體：sort 值最小的那顆（後台精靈本來就有的排序欄位，跟管理端的預設呈現順序一致）
const defaultSkuId = skus.value.length > 0
  ? skus.value.reduce((min, s) => (s.sort < min.sort ? s : min)).id
  : null
const selectedSkuId = ref(defaultSkuId)

const currentSku = computed(() => skus.value.find(s => s.id === selectedSkuId.value) ?? skus.value[0])

// 目前這顆 sku 是不是活躍秒殺——只認 ACTIVE 場次，PUBLISHED（還沒開賣）的場次不算，
// remaining=0（賣完/被設 0）也不算，活動結束後 useSeckill() 的資料自然不會再包含這個 skuId，
// 這裡不用寫「是否過期」的額外判斷。
const activeSeckillSku = computed(() => {
  const skuId = currentSku.value?.id
  if (!skuId) return null
  for (const session of seckillData.value?.flashSaleSessions ?? []) {
    if (session.status !== 'ACTIVE') continue
    const product = session.products.find(p => p.spuId === spuId)
    const sku = product?.skus.find(s => s.skuId === skuId && s.remaining > 0)
    if (sku) return { ...sku, endTime: session.endTime }
  }
  return null
})

// 這個 SPU 在目前活躍的秒殺活動裡，哪些規格已經沒名額（remaining=0，不管是後台故意設 0 還是被搶完）——
// 這些規格的按鈕整場活動期間都要反灰、禁止選擇，連原價都不能買（跟使用者確認過的行為，見「秒殺改成綁 SPU」規劃文件）。
const soldOutSeckillSkuIds = computed(() => {
  const ids = new Set<number>()
  for (const session of seckillData.value?.flashSaleSessions ?? []) {
    if (session.status !== 'ACTIVE') continue
    const product = session.products.find(p => p.spuId === spuId)
    for (const sku of product?.skus ?? []) {
      if (sku.remaining <= 0) ids.add(sku.skuId)
    }
  }
  return ids
})

// 目前活躍秒殺涉及到的所有 sku（不管是否賣完）——這些 sku 的庫存語意是搶購名額，一般倉庫存
// 判斷要排除它們，不然會跟 soldOutSeckillSkuIds 的邏輯互相矛盾（例如秒殺庫存充足但一般倉是 0）。
const activeSeckillSkuIds = computed(() => {
  const ids = new Set<number>()
  for (const session of seckillData.value?.flashSaleSessions ?? []) {
    if (session.status !== 'ACTIVE') continue
    const product = session.products.find(p => p.spuId === spuId)
    for (const sku of product?.skus ?? []) ids.add(sku.skuId)
  }
  return ids
})

// 一般倉庫存售完的規格——排除掉正在走秒殺名額語意的 sku，見上面 activeSeckillSkuIds 的說明。
const soldOutStockSkuIds = computed(() => {
  const ids = new Set<number>()
  for (const sku of skus.value) {
    if (activeSeckillSkuIds.value.has(sku.id)) continue
    const stock = skuStocks.value[sku.id]?.availableStock
    if (stock != null && stock <= 0) ids.add(sku.id)
  }
  return ids
})

const seckillEndTime = computed(() => activeSeckillSku.value?.endTime ?? null)
const { hh, mm, ss } = useCountdown(seckillEndTime)

// 「猜你喜歡」的瀏覽興趣訊號——只有登入會員才記錄，訪客不追蹤。非關鍵路徑，失敗不影響頁面
// 本身，recordInterest 內部已經 .catch(() => {}) 吞掉錯誤。
const authStore = useAuthStore()
onMounted(() => {
  if (authStore.isLoggedIn && spu.value?.catalog1Id) {
    const { recordInterest } = useCategoryInterest()
    recordInterest(spu.value.catalog1Id, 'VIEW')
  }
})

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

// 選了某個屬性值（連同目前其餘已選屬性）會對應到哪顆 sku——選規格按鈕本身跟「是否售完反灰」共用同一套匹配邏輯
function resolveSkuForAttrChange(attrName: string, value: string) {
  const target = { ...selectedAttrs.value, [attrName]: value }
  return skus.value.find(sku =>
    sku.saleAttrValues.length === Object.keys(target).length
    && sku.saleAttrValues.every(av => target[av.attrName] === av.attrValue)
  )
}

/** 秒殺名額用完的規格按鈕直接禁止點擊，連原價都不能買（見 soldOutSeckillSkuIds 的說明）。 */
function isOptionSoldOut(attrName: string, value: string) {
  const match = resolveSkuForAttrChange(attrName, value)
  return !!match && soldOutSeckillSkuIds.value.has(match.id)
}

/** 一般倉庫存為 0——只是視覺提示（灰字+「缺貨」標籤），仍然可以點選切換過去看該規格的圖片，
 * 只有真的要下單時（加入購物車/立即購買）才擋，跟秒殺售完「連選都不能選」是不同的使用者體驗。 */
function isOptionOutOfStock(attrName: string, value: string) {
  const match = resolveSkuForAttrChange(attrName, value)
  return !!match && soldOutStockSkuIds.value.has(match.id)
}

// 純前端狀態切換，不 router.replace——MOMO 那種「選規格不換網址」的體驗
function selectAttr(attrName: string, value: string) {
  const match = resolveSkuForAttrChange(attrName, value)
  if (!match || isOptionSoldOut(attrName, value)) return
  selectedSkuId.value = match.id
  activeImg.value = 0
}

const specs = computed(() => (spu.value?.attrValues ?? []).map(v => ({ label: v.attrName, value: v.attrValue })))
const sanitizedDescription = computed(() => DOMPurify.sanitize(spu.value?.description ?? ''))

// 目前選中 sku 的一般倉庫存（來自上面一次查好的 skuStocks）——秒殺 SKU 的庫存語意是搶購名額
// （activeSeckillSku.remaining），兩者互斥，秒殺進行中不看一般倉庫存（比照下單時 tengan-order
// 只鎖非秒殺項目的邏輯，見 CreateOrderService「一般商品只鎖『不是秒殺』的那些」）。
const availableStock = computed(() => {
  const sku = currentSku.value
  if (!sku || activeSeckillSku.value) return null
  return skuStocks.value[sku.id]?.availableStock ?? null
})

// 目前選中 sku 的開賣時間/限購資訊——秒殺進行中互斥（不會同時是「即將開賣」），見 activeSeckillSku。
const currentSkuStockInfo = computed(() => {
  const sku = currentSku.value
  return sku ? skuStocks.value[sku.id] ?? null : null
})
const isNotYetOnSale = computed(() =>
  !activeSeckillSku.value && currentSkuStockInfo.value?.purchasable === false
)
const saleStartTime = computed(() => currentSkuStockInfo.value?.saleStartTime ?? null)
const purchaseLimitPerUser = computed(() => currentSkuStockInfo.value?.purchaseLimitPerUser ?? null)

const { hh: saleHh, mm: saleMm, ss: saleSs } = useCountdown(saleStartTime)
const saleStartText = computed(() => {
  const iso = saleStartTime.value
  if (!iso) return ''
  const d = new Date(iso)
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const hhStr = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${mm}/${dd} ${hhStr}:${min} 開賣`
})

/** 加購數量上限：一般倉庫存跟每人限購取較小值；秒殺 sku 有自己一套名額邏輯，不套用這裡。 */
const qtyMax = computed(() => {
  if (activeSeckillSku.value) return null
  const candidates = [availableStock.value, purchaseLimitPerUser.value].filter((v): v is number => v != null)
  return candidates.length > 0 ? Math.min(...candidates) : null
})

watch(() => currentSku.value?.id, () => {
  qty.value = 1
})
watch([qty, qtyMax], () => {
  if (qtyMax.value !== null && qty.value > qtyMax.value) {
    qty.value = Math.max(1, qtyMax.value)
  }
})

const isOutOfStock = computed(() =>
  !activeSeckillSku.value && availableStock.value !== null && availableStock.value <= 0
)
const isPurchaseDisabled = computed(() => isOutOfStock.value || isNotYetOnSale.value)

async function handleAddToCart() {
  const sku = currentSku.value
  if (!sku || isPurchaseDisabled.value) return
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
  if (!sku || isPurchaseDisabled.value) return
  const newCount = await addToCart(
    { skuId: sku.id, skuName: sku.name, price: sku.price, image: images.value[0] ?? sku.mainImage },
    qty.value
  )
  cartStore.setCount(newCount)
  navigateTo('/order/confirm')
}
</script>
