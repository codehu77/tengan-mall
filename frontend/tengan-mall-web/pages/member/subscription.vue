<template>
  <div class="space-y-4">
    <div>
      <h1 class="text-2xl font-semibold text-gray-800">訂閱會員</h1>
      <p class="text-base text-gray-400 mt-1">訂閱 PRO / PRO+，每月自動扣款享有更高點數回饋比例</p>
    </div>

    <div v-if="loading" class="bg-white rounded-xl p-16 text-center text-gray-400 border border-gray-100">
      載入中...
    </div>

    <template v-else>
      <!-- PENDING（ECPay 首刷授權結果還沒回來，例如剛剛按上一頁離開付款頁）直接落回下面的方案選擇
           畫面，不特別攔住——使用者可能本來就想改選別的方案，重新點下面任一顆按鈕都會先讓後端同步
           查一次 ECPay 真實狀態：查到舊嘗試其實沒成功就作廢重來，查到其實成功了就收斂成 ACTIVE
           （見 handleSubscribe 的 catch 分支）。 -->
      <template v-if="subscription?.subscribed && subscription.status !== 'PENDING'">
        <!-- 已取消但權益尚未到期：明確標示不會立刻失去 PRO -->
        <div
          v-if="!subscription.autoRenew"
          class="bg-orange-50 border border-orange-200 rounded-lg px-5 py-3 flex items-center gap-3"
        >
          <UIcon name="i-heroicons-information-circle" class="w-5 h-5 text-orange-400 shrink-0" />
          <p class="text-sm text-orange-600">
            已取消續訂，{{ SUBSCRIPTION_TIER_LABEL[subscription.targetTier!] }} 權益維持到
            <span class="font-semibold">{{ formatDate(subscription.paidUntil!) }}</span>，之後不會再自動扣款
          </p>
        </div>

        <!-- 訂閱中 -->
        <div class="bg-white rounded-xl p-6 border border-gray-100">
          <div class="flex items-center justify-between mb-4">
            <span class="text-base text-gray-500">目前訂閱狀態</span>
            <span
              class="text-sm font-semibold px-2.5 py-1 rounded-full"
              :class="subscription.targetTier === 'PRO_PLUS' ? 'bg-red-100 text-red-600' : 'bg-orange-100 text-orange-600'"
            >
              {{ SUBSCRIPTION_TIER_LABEL[subscription.targetTier!] }}
            </span>
          </div>

          <p class="text-base text-gray-600">
            {{ subscription.autoRenew ? '下次扣款日' : '權益到期日' }}
            <span class="font-semibold text-gray-800">{{ formatDate(subscription.paidUntil!) }}</span>
          </p>

          <!-- 你的方案權益：訂閱成功後不能只剩帳單資訊，這筆錢換到什麼要留在畫面上 -->
          <div v-if="currentBenefit" class="mt-5 pt-4 border-t border-gray-100">
            <p class="text-base text-gray-500 mb-3">你的方案權益</p>
            <div class="flex items-center gap-6 mb-3">
              <div>
                <p class="text-2xl font-bold text-gray-800">{{ currentBenefit.cashbackRateLabel }}</p>
                <p class="text-sm text-gray-400">消費點數回饋比例</p>
              </div>
              <div>
                <p class="text-2xl font-bold text-gray-800">{{ currentBenefit.monthlyCapLabel }}</p>
                <p class="text-sm text-gray-400">每月回饋上限</p>
              </div>
            </div>
            <ul class="space-y-1.5 mb-3">
              <li v-for="perk in currentBenefit.perks" :key="perk" class="flex items-start gap-1.5 text-sm text-gray-600">
                <UIcon name="i-heroicons-check" class="w-3.5 h-3.5 text-red-500 shrink-0 mt-0.5" />
                {{ perk }}
              </li>
            </ul>
            <NuxtLink to="/member/points" class="text-sm text-red-500 hover:text-red-600 font-medium">
              查看本月已回饋點數 →
            </NuxtLink>
          </div>

          <button
            v-if="subscription.autoRenew"
            class="mt-5 w-full h-12 border border-gray-200 text-gray-600 rounded-lg font-medium text-base hover:bg-gray-50 transition disabled:opacity-50"
            :disabled="cancelling"
            @click="handleCancel"
          >
            {{ cancelling ? '處理中...' : '取消訂閱' }}
          </button>
        </div>
      </template>

      <!-- 未訂閱：顯示方案選擇 -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <div v-for="plan in PLANS" :key="plan.tier" class="bg-white rounded-xl p-6 border border-gray-100 flex flex-col">
          <span
            class="self-start text-sm font-semibold px-2.5 py-1 rounded-full mb-3"
            :class="plan.tier === 'PRO_PLUS' ? 'bg-red-100 text-red-600' : 'bg-orange-100 text-orange-600'"
          >
            {{ plan.label }}
          </span>
          <p class="text-4xl font-bold text-gray-800">
            NT$ {{ plan.price }}<span class="text-base font-normal text-gray-400"> / 月</span>
          </p>
          <ul class="mt-4 space-y-1.5 text-sm text-gray-600 flex-1">
            <li v-for="perk in plan.perks" :key="perk" class="flex items-start gap-1.5">
              <UIcon name="i-heroicons-check" class="w-4 h-4 text-red-500 shrink-0 mt-0.5" />
              {{ perk }}
            </li>
          </ul>
          <button
            class="mt-5 w-full h-12 bg-red-500 text-white rounded-lg font-semibold text-base hover:bg-red-600 transition disabled:opacity-50"
            :disabled="subscribing"
            @click="handleSubscribe(plan.tier)"
          >
            {{ subscribing ? '處理中...' : `訂閱 ${plan.label}` }}
          </button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { SUBSCRIPTION_TIER_LABEL } from '~/types/subscription'
import type { EcpayFormData } from '~/types/payment'
import type { MySubscriptionResult, SubscriptionTargetTier } from '~/types/subscription'
import { pointsService } from '~/services/pointsService'
import type { TierBenefit } from '~/types/points'

definePageMeta({ middleware: 'auth', layout: 'member' })

const PLANS: { tier: SubscriptionTargetTier; label: string; price: number; perks: string[] }[] = [
  { tier: 'PRO', label: 'PRO', price: 99, perks: ['消費點數回饋比例提升', '每月回饋上限提高'] },
  { tier: 'PRO_PLUS', label: 'PRO+', price: 199, perks: ['消費點數回饋比例最高', '每月回饋無上限'] },
]

const toast = useToast()
const { subscribe, fetchMySubscription, cancelSubscription } = useSubscription()

const loading = ref(true)
const subscribing = ref(false)
const cancelling = ref(false)
const subscription = ref<MySubscriptionResult | null>(null)
const tierBenefits = ref<TierBenefit[]>([])

// 訂閱成功後的「你的方案權益」卡片直接沿用「我的點數」比較表同一份 perks 文案，
// 避免這裡另外寫死一份，之後兩邊文案兜不起來。
const currentBenefit = computed(() =>
  tierBenefits.value.find(b => b.tier === subscription.value?.targetTier) ?? null,
)

onMounted(async () => {
  try {
    const [subscriptionResult] = await Promise.all([
      fetchMySubscription(),
      pointsService.fetchTierInfo().then(({ benefits }) => {
        tierBenefits.value = benefits
      }).catch(() => {
        // 權益小卡是加值資訊，載入失敗就悄悄不顯示，不影響訂閱狀態本身能不能看
      }),
      // 從 ECPay 付款頁整頁導回來時通常會自動重抓（見 plugins/auth.ts），但如果是用瀏覽器上一頁
      // 或站內連結（非整頁刷新）回到這頁，Header 的等級徽章不會知道升級/取消已生效，這裡順手補一次。
      usePointsStore().loadCurrentTier(),
    ])
    subscription.value = subscriptionResult
  } finally {
    loading.value = false
  }
})

async function handleSubscribe(tier: SubscriptionTargetTier) {
  subscribing.value = true
  try {
    const result = await subscribe(tier)
    submitEcpayForm(result.ecpayForm)
  } catch (e: any) {
    // 從 PENDING 按「重新查詢付款結果」時，後端會先同步查 ECPay 真實狀態——如果查到其實已經授權
    // 成功，會直接收斂成 ACTIVE 再丟「已經有一份進行中的訂閱」擋重複，這裡重抓一次狀態，讓畫面從
    // 「付款確認中」正確跳到「訂閱中」，不要停在過期的 PENDING 畫面又疊一個看起來像失敗的紅色提示。
    subscription.value = await fetchMySubscription()
    if (subscription.value.status === 'ACTIVE') {
      toast.add({ title: '訂閱成功', description: '剛剛的付款其實已經授權成功', color: 'green', timeout: 3000 })
    } else {
      toast.add({
        title: '訂閱失敗',
        description: e.data?.data?.message ?? e.message,
        color: 'red',
        timeout: 4000,
      })
    }
    subscribing.value = false
  }
}

/** ECPay 的付款頁是瀏覽器整頁導向，動態組一個隱藏表單立即 submit，比照 order/pay.vue 既有模式。 */
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

async function handleCancel() {
  cancelling.value = true
  try {
    await cancelSubscription()
    subscription.value = await fetchMySubscription()
    toast.add({ title: '已取消續訂', color: 'gray', timeout: 3000 })
  } catch (e: any) {
    toast.add({ title: '取消失敗', description: e.data?.data?.message ?? e.message, color: 'red', timeout: 3000 })
  } finally {
    cancelling.value = false
  }
}
</script>
