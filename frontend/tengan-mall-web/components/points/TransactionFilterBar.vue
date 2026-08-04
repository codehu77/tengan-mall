<template>
  <div class="bg-white rounded-xl border border-gray-100 overflow-hidden">
    <!-- 類型 Tabs -->
    <div class="flex border-b border-gray-100 overflow-x-auto">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="px-4 py-3 text-base font-medium transition border-b-2 -mb-px whitespace-nowrap"
        :class="modelValue.type === tab.value
          ? 'border-red-500 text-red-600'
          : 'border-transparent text-gray-500 hover:text-gray-700'"
        @click="$emit('update:type', tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 篩選列 -->
    <div class="flex flex-wrap items-center gap-3 px-4 py-3">
      <div class="relative">
        <select
          :value="modelValue.dateRange"
          class="text-base border border-gray-200 rounded-lg pl-3 pr-8 py-2 outline-none focus:border-red-300 text-gray-700 bg-white appearance-none"
          @change="$emit('update:dateRange', ($event.target as HTMLSelectElement).value)"
        >
          <option v-for="r in dateRangeOptions" :key="r.value" :value="r.value">{{ r.label }}</option>
        </select>
        <UIcon name="i-heroicons-chevron-down" class="absolute right-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400 pointer-events-none" />
      </div>

      <div class="relative flex-1 min-w-[200px] max-w-xs">
        <UIcon name="i-heroicons-magnifying-glass" class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-300" />
        <input
          :value="modelValue.keyword"
          type="text"
          placeholder="搜尋訂單編號或交易說明"
          class="w-full text-base border border-gray-200 rounded-lg pl-9 pr-3 py-2 outline-none focus:border-red-300 text-gray-700 placeholder-gray-300"
          @input="onKeywordInput"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PointDateRangeFilter, PointTransactionFilterType, TransactionQuery } from '~/types/points'

defineProps<{
  modelValue: TransactionQuery
}>()

const emit = defineEmits<{
  'update:type': [PointTransactionFilterType]
  'update:dateRange': [PointDateRangeFilter]
  'update:keyword': [string]
}>()

const tabs: { label: string; value: PointTransactionFilterType }[] = [
  { label: '全部', value: 'ALL' },
  { label: '獲得', value: 'EARN' },
  { label: '使用', value: 'REDEEM' },
  { label: '過期', value: 'EXPIRE' },
  { label: '人工調整', value: 'ADJUST' },
  { label: '待入帳', value: 'PENDING' },
]

const dateRangeOptions: { label: string; value: PointDateRangeFilter }[] = [
  { label: '全部時間', value: 'ALL' },
  { label: '近 30 天', value: '30D' },
  { label: '近 90 天', value: '90D' },
  { label: '近 1 年', value: '1Y' },
]

let debounceTimer: ReturnType<typeof setTimeout> | undefined

function onKeywordInput(e: Event) {
  const value = (e.target as HTMLInputElement).value
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => emit('update:keyword', value), 300)
}
</script>
