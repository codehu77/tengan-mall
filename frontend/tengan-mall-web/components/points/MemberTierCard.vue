<template>
  <PointsStateView :loading="loading" :error="error" :skeleton-rows="1" @retry="$emit('retry')">
    <template #loading>
      <USkeleton class="h-40 w-full rounded-xl" />
    </template>

    <div v-if="tier" class="bg-white rounded-xl p-6 border border-gray-100 h-full">
      <div class="flex items-center justify-between mb-4">
        <span class="text-base text-gray-500">目前會員資格</span>
        <span
          class="text-sm font-semibold px-2.5 py-1 rounded-full"
          :class="tier.tier === 'PRO_PLUS' ? 'bg-red-100 text-red-600' : tier.tier === 'PRO' ? 'bg-orange-100 text-orange-600' : 'bg-gray-100 text-gray-500'"
        >
          {{ TIER_LABEL[tier.tier] }}
        </span>
      </div>

      <p class="text-5xl font-bold text-gray-800">
        {{ (tier.cashbackRate * 100).toFixed(0) }}<span class="text-2xl">%</span>
      </p>
      <p class="text-base text-gray-400 mt-1">消費點數回饋比例</p>

      <div class="mt-5 pt-4 border-t border-gray-100">
        <div class="flex items-center justify-between text-base text-gray-600 mb-1.5">
          <span>本月已回饋</span>
          <span>
            {{ tier.monthlyEarnedPoints.toLocaleString() }} 點
            <span class="text-gray-400">/ {{ tier.monthlyCap ? `${tier.monthlyCap.toLocaleString()} 點` : '無上限' }}</span>
          </span>
        </div>
        <div v-if="tier.monthlyCap" class="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
          <div
            class="h-full bg-red-500 rounded-full transition-all"
            :style="{ width: `${Math.min(100, (tier.monthlyEarnedPoints / tier.monthlyCap) * 100)}%` }"
          />
        </div>
        <p v-else class="text-sm text-red-500 mt-1">PRO+ 會員單月回饋無上限</p>
      </div>
    </div>
  </PointsStateView>
</template>

<script setup lang="ts">
import { TIER_LABEL } from '~/types/points'
import type { TierInfo } from '~/types/points'

defineProps<{
  tier: TierInfo | null
  loading: boolean
  error?: string | null
}>()

defineEmits<{ retry: [] }>()
</script>
