<template>
  <div class="bg-white rounded-xl p-6 border border-gray-100">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-xl font-bold text-gray-800">即將到期點數</h2>
      <span class="text-sm text-gray-400">依到期日由近到遠排序</span>
    </div>

    <PointsStateView
      :loading="loading"
      :error="error"
      :is-empty="!loading && !error && batches.length === 0"
      empty-icon="i-heroicons-check-circle"
      empty-title="目前沒有即將到期的點數"
      @retry="$emit('retry')"
    >
      <div class="divide-y divide-gray-100">
        <div
          v-for="batch in sortedBatches"
          :key="batch.batchId"
          class="flex items-center justify-between py-3 first:pt-0 last:pb-0"
        >
          <div>
            <p class="text-lg text-gray-700 font-medium">{{ batch.points.toLocaleString() }} 點</p>
            <p class="text-sm text-gray-400 mt-0.5">
              {{ formatDate(batch.earnedAt) }} 取得
              <template v-if="batch.sourceOrderSn">・訂單 {{ batch.sourceOrderSn }}</template>
            </p>
          </div>
          <div class="text-right">
            <p class="text-base text-gray-600">{{ formatDate(batch.expiresAt) }} 到期</p>
            <p class="text-sm mt-0.5" :class="daysRemaining(batch.expiresAt) <= 7 ? 'text-red-500' : 'text-orange-400'">
              剩 {{ Math.max(0, daysRemaining(batch.expiresAt)) }} 天
            </p>
          </div>
        </div>
      </div>
    </PointsStateView>
  </div>
</template>

<script setup lang="ts">
import type { PointBatch } from '~/types/points'

const props = defineProps<{
  batches: PointBatch[]
  loading: boolean
  error?: string | null
}>()

defineEmits<{ retry: [] }>()

const sortedBatches = computed(() =>
  [...props.batches].sort((a, b) => new Date(a.expiresAt).getTime() - new Date(b.expiresAt).getTime())
)
</script>
