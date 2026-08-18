<template>
  <PointsStateView :loading="loading" :error="error" :skeleton-rows="1" @retry="$emit('retry')">
    <template #loading>
      <USkeleton class="h-40 w-full rounded-xl" />
    </template>

    <div class="bg-white rounded-xl p-6 border border-gray-100 h-full">
      <p class="text-base text-gray-500 mb-4">會員回饋比較</p>
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-3 items-stretch">
        <div
          v-for="benefit in benefits"
          :key="benefit.tier"
          class="rounded-lg border p-4 relative h-full"
          :class="
            benefit.tier === 'PRO'
              ? 'border-red-300 bg-red-50/50'
              : benefit.isCurrent
                ? 'border-gray-800'
                : 'border-gray-100'
          "
        >
          <div class="absolute -top-2.5 left-4 flex items-center gap-1.5">
            <span v-if="benefit.isCurrent" class="text-sm bg-gray-800 text-white px-2 py-0.5 rounded-full">
              目前方案
            </span>
            <span v-if="benefit.tier === 'PRO'" class="text-sm bg-red-500 text-white px-2 py-0.5 rounded-full">
              推薦方案
            </span>
          </div>
          <p class="font-semibold text-gray-800 mt-1 text-lg">{{ benefit.label }}</p>
          <p class="text-3xl font-bold mt-1" :class="benefit.tier === 'PRO' ? 'text-red-500' : 'text-gray-700'">
            {{ benefit.cashbackRateLabel }}
          </p>
          <p class="text-sm text-gray-400 mb-3">{{ benefit.monthlyCapLabel }}</p>
          <ul class="space-y-1.5">
            <li v-for="perk in benefit.perks" :key="perk" class="flex items-start gap-1.5 text-sm text-gray-500">
              <UIcon name="i-heroicons-check" class="w-3.5 h-3.5 text-gray-400 mt-0.5 shrink-0" />
              <span>{{ perk }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </PointsStateView>
</template>

<script setup lang="ts">
import type { TierBenefit } from '~/types/points'

defineProps<{
  benefits: TierBenefit[]
  loading: boolean
  error?: string | null
}>()

defineEmits<{ retry: [] }>()
</script>
