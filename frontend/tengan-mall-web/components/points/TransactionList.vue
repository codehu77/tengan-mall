<template>
  <div class="bg-white rounded-xl border border-gray-100 overflow-hidden">
    <PointsStateView
      :loading="loading"
      :error="error"
      :is-empty="!loading && !error && transactions.length === 0"
      :skeleton-rows="5"
      empty-title="沒有符合條件的交易紀錄"
      @retry="$emit('retry')"
    >
      <template #loading>
        <div class="divide-y divide-gray-100 px-5">
          <div v-for="n in 5" :key="n" class="py-4 flex items-center gap-4">
            <USkeleton class="h-9 w-9 rounded-full shrink-0" />
            <div class="flex-1 space-y-2">
              <USkeleton class="h-3.5 w-1/3" />
              <USkeleton class="h-3 w-1/2" />
            </div>
            <USkeleton class="h-4 w-14" />
          </div>
        </div>
      </template>

      <div class="divide-y divide-gray-100 px-5">
        <button
          v-for="tx in transactions"
          :key="tx.id"
          type="button"
          class="w-full py-4 flex items-center gap-4 text-left hover:bg-gray-50/80 transition -mx-5 px-5"
          @click="$emit('select', tx.id)"
        >
          <span
            class="w-9 h-9 rounded-full flex items-center justify-center shrink-0"
            :class="iconBgClass(tx.type)"
          >
            <UIcon :name="TRANSACTION_TYPE_META[tx.type].icon" class="w-4.5 h-4.5" :class="iconColorClass(tx.type)" />
          </span>

          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <p class="text-base text-gray-700 font-medium truncate">{{ tx.title }}</p>
              <UBadge :color="(TRANSACTION_TYPE_META[tx.type].color as any)" variant="subtle" size="sm">
                {{ TRANSACTION_TYPE_META[tx.type].label }}
              </UBadge>
              <UBadge v-if="tx.status !== 'CONFIRMED'" :color="(TRANSACTION_STATUS_META[tx.status].color as any)" variant="soft" size="sm">
                {{ TRANSACTION_STATUS_META[tx.status].label }}
              </UBadge>
            </div>
            <p class="text-sm text-gray-400 mt-1 truncate">{{ formatDate(tx.createdAt, true) }}</p>
          </div>

          <div class="text-right shrink-0">
            <p class="text-lg font-semibold" :class="tx.points >= 0 ? 'text-green-600' : 'text-gray-700'">
              {{ tx.points >= 0 ? '+' : '' }}{{ tx.points.toLocaleString() }}
            </p>
            <p v-if="tx.status === 'CONFIRMED' && tx.balanceAfter !== null" class="text-sm text-gray-400 mt-0.5">
              餘額 {{ tx.balanceAfter.toLocaleString() }}
            </p>
          </div>

          <UIcon name="i-heroicons-chevron-right" class="w-4 h-4 text-gray-300 shrink-0" />
        </button>
      </div>

      <div v-if="totalPages > 1" class="flex justify-center py-4 border-t border-gray-100">
        <UPagination
          :model-value="page"
          :page-count="pageSize"
          :total="total"
          size="sm"
          @update:model-value="$emit('page-change', $event)"
        />
      </div>
    </PointsStateView>
  </div>
</template>

<script setup lang="ts">
import { TRANSACTION_TYPE_META, TRANSACTION_STATUS_META } from '~/types/points'
import type { PointTransaction } from '~/types/points'

defineProps<{
  transactions: PointTransaction[]
  loading: boolean
  error?: string | null
  page: number
  pageSize: number
  total: number
  totalPages: number
}>()

defineEmits<{
  retry: []
  select: [string]
  'page-change': [number]
}>()

function iconBgClass(type: PointTransaction['type']) {
  const map: Record<string, string> = {
    EARN: 'bg-green-50',
    REDEEM: 'bg-red-50',
    EXPIRE: 'bg-gray-100',
    ADJUST: 'bg-orange-50',
  }
  return map[type]
}

function iconColorClass(type: PointTransaction['type']) {
  const map: Record<string, string> = {
    EARN: 'text-green-500',
    REDEEM: 'text-red-500',
    EXPIRE: 'text-gray-400',
    ADJUST: 'text-orange-500',
  }
  return map[type]
}
</script>
