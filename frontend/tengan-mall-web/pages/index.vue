<template>
  <div class="max-w-7xl mx-auto px-6 py-8">

    <!-- 首頁輪播 Banner -->
    <BannerCarousel v-if="banners.length > 0" :banners="banners" />
    <div
      v-else
      class="bg-card border border-border rounded-2xl h-[470px] flex items-center justify-center"
    >
      <p class="text-muted text-xl">尚未設定輪播圖</p>
    </div>

    <!-- 活動廣告卡：純推廣入口，優惠券目前沒有領取頁面/API，先做視覺+提示；PRO 卡連到真實存在的訂閱頁 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mt-6 mb-10">
      <PromoCard
        variant="coupon"
        title="領取優惠券"
        subtitle="多種優惠券任你選擇"
        cta-label="立即領取"
        @cta="showCouponComingSoon"
      />
      <PromoCard
        variant="pro"
        title="升級天願 PRO 會員"
        subtitle="立即獲得消費回饋，購物更划算"
        cta-label="立即升級"
        to="/member/subscription"
      />
    </div>

    <!-- 限時搶購 -->
    <SeckillSection v-if="flashSaleSessions.length > 0" :flash-sale-sessions="flashSaleSessions" />

    <!-- 即將開賣 -->
    <LaunchTeaserSection v-if="teaserProducts.length > 0" :products="teaserProducts" />

    <!-- 熱門商品 -->
    <section v-if="hotProducts.length > 0" class="mb-10">
      <SectionHeader icon="i-heroicons-fire" title="熱門商品" accent="danger" to="/search" toLabel="查看更多" />
      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
        <ProductCard
          v-for="product in hotProducts"
          :key="product.skuId"
          :product="product"
        />
      </div>
    </section>

    <!-- 猜你喜歡：訪客也看得到（退化成洗牌過的全站熱銷），會員才有個人化排序 -->
    <section v-if="guessItems.length > 0">
      <SectionHeader icon="i-heroicons-heart" title="猜你喜歡" accent="pink" />
      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
        <ProductCard
          v-for="product in guessItems"
          :key="product.skuId"
          :product="product"
        />
      </div>
      <div ref="guessSentinel" class="h-4" />
      <p v-if="guessExhausted" class="text-center text-muted text-sm py-4">已經沒有更多推薦了</p>
    </section>

  </div>
</template>

<script setup lang="ts">
import type { Product } from '~/mocks/products'
import type { ProductSearchQuery, SearchItem } from '~/composables/useProductSearch'

useHead({ title: '首頁' })

const toast = useToast()
function showCouponComingSoon() {
  toast.add({ title: '優惠券活動即將推出', color: 'blue', timeout: 3000 })
}

// 直接把泛用搜尋端點當「銷量前 N 名」用（sort=sale，不帶 keyword/catId），不用新增後端端點。
// 首頁熱門商品區塊不需要 search.vue 那支 toProduct 的秒殺價覆蓋邏輯，用一個簡化版本就好。
const hotQuery = ref<ProductSearchQuery>({ sort: 'sale', order: 'desc', pageSize: 10 })
const { data: hotData } = await useProductSearch(hotQuery)

function toHotProduct(item: SearchItem): Product {
  return {
    skuId: item.spuId,
    spuId: item.spuId,
    skuName: item.spuName,
    price: item.minPrice,
    maxPrice: item.maxPrice,
    skuDefaultImg: item.mainImage,
    saleCount: item.saleCount,
    categoryId: 0,
  }
}
const hotProducts = computed(() => (hotData.value?.items ?? []).map(toHotProduct))

const { data: seckillData } = await useSeckill()
const flashSaleSessions = computed(() => seckillData.value?.flashSaleSessions ?? [])

const { data: bannerData } = await useBanners()
const banners = computed(() => bannerData.value?.banners ?? [])

const { data: launchTeaserData } = await useLaunchTeaser()
const teaserProducts = computed(() => launchTeaserData.value?.items ?? [])

// 猜你喜歡：無限捲動，走訪順序/游標狀態全部交給 useGuessYouLike 這個 composable 管理。
// 第一批資料比照熱門商品的模式，在 SSR 階段就 await 抓好（不是進頁面後才由 onMounted 發
// 請求），使用者一進來就看得到，不用等一段時間才浮現。onMounted 只負責裝 IntersectionObserver
// （這個一定要 DOM 存在才能做，沒辦法搬到 SSR 階段）。全站第一個無限捲動，只在首頁用，
// 不刻意抽成通用元件。
const { items: guessItems, exhausted: guessExhausted, init: initGuessYouLike, loadMore: loadMoreGuessYouLike } = useGuessYouLike()
await initGuessYouLike()

const guessSentinel = ref<HTMLElement | null>(null)
let guessObserver: IntersectionObserver | null = null

onMounted(() => {
  if (!guessSentinel.value) return
  guessObserver = new IntersectionObserver((entries) => {
    if (entries[0]?.isIntersecting) loadMoreGuessYouLike()
  })
  guessObserver.observe(guessSentinel.value)
})
onUnmounted(() => {
  guessObserver?.disconnect()
})
</script>
