<template>
  <PointsStateView
    :loading="loading"
    :error="error"
    :skeleton-rows="1"
    @retry="$emit('retry')"
  >
    <template #loading>
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <USkeleton v-for="n in 3" :key="n" class="h-28 w-full rounded-xl" />
      </div>
    </template>

    <div v-if="summary" class="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <div class="bg-white rounded-xl p-5 border border-gray-100">
        <div class="flex items-center gap-2 text-gray-500 text-base mb-2">
          <UIcon name="i-heroicons-wallet" class="w-4 h-4" />
          <span>可用點數</span>
        </div>
        <p class="text-4xl font-bold text-red-600">{{ summary.availablePoints.toLocaleString() }}</p>
        <p class="text-sm text-gray-400 mt-1">約可折抵 NT$ {{ (summary.availablePoints * summary.pointValueRatio).toLocaleString() }}</p>
      </div>

      <div class="bg-white rounded-xl p-5 border border-gray-100">
        <div class="flex items-center gap-2 text-gray-500 text-base mb-2">
          <UIcon name="i-heroicons-arrow-path" class="w-4 h-4" />
          <span>待入帳點數</span>
        </div>
        <p class="text-4xl font-bold text-gray-800">{{ summary.pendingPoints.toLocaleString() }}</p>
        <p class="text-sm text-gray-400 mt-1">訂單鑑賞期滿後自動入帳</p>
      </div>

      <div class="bg-white rounded-xl p-5 border border-orange-100 bg-orange-50/40">
        <div class="flex items-center gap-2 text-orange-600 text-base mb-2">
          <UIcon name="i-heroicons-exclamation-circle" class="w-4 h-4" />
          <span>即將到期點數</span>
        </div>
        <p class="text-4xl font-bold text-orange-500">{{ summary.expiringPoints.toLocaleString() }}</p>
        <p class="text-sm text-orange-400 mt-1">{{ summary.expiringWithinDays }} 天內到期，請盡快使用</p>
      </div>
    </div>
  </PointsStateView>
</template>

<script setup lang="ts">
import type { PointAccountSummary } from '~/types/points'

defineProps<{
  summary: PointAccountSummary | null
  loading: boolean
  error?: string | null
}>()

defineEmits<{ retry: [] }>()
</script>
