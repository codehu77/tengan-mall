<template>
  <div class="bg-white rounded-xl border border-gray-100 overflow-hidden">
    <!-- 消費者視角的分類 Tabs：每個分類背後對應固定的 type+status 組合，不再讓使用者面對兩個正交的
         篩選維度（只有「獲得」真的有待入帳/已生效兩階段，其餘類型都是一次性事件）。 -->
    <div class="flex border-b border-gray-100 overflow-x-auto">
      <button
        v-for="tab in categoryTabs"
        :key="tab.label"
        class="px-4 py-3 text-base font-medium transition border-b-2 -mb-px whitespace-nowrap flex items-center gap-1.5"
        :class="isActive(tab)
          ? 'border-red-500 text-red-600'
          : 'border-transparent text-gray-500 hover:text-gray-700'"
        @click="$emit('update:category', { type: tab.type, status: tab.status })"
      >
        {{ tab.label }}
        <span
          v-if="countLabel(tab)"
          class="text-xs font-semibold rounded-full px-1.5 py-0.5 min-w-[1.25rem] text-center"
          :class="isActive(tab) ? 'bg-red-100 text-red-600' : 'bg-gray-100 text-gray-500'"
        >
          {{ countLabel(tab) }}
        </span>
      </button>
    </div>

    <!-- 篩選列 -->
    <div class="flex flex-wrap items-center gap-3 px-4 py-3">
      <div class="relative">
        <select
          :value="modelValue.dateRange"
          class="text-base border border-gray-200 rounded-lg pl-3 pr-8 py-2 outline-none focus:border-red-300 text-gray-700 bg-white appearance-none"
          @change="$emit('update:dateRange', ($event.target as HTMLSelectElement).value as any)"
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
import type {
  PointDateRangeFilter,
  PointTransactionTypeFilter,
  PointTransactionStatusFilter,
  TransactionQuery,
  TransactionCountItem,
} from '~/types/points'

const props = defineProps<{
  modelValue: TransactionQuery
  counts?: TransactionCountItem[]
}>()

const emit = defineEmits<{
  'update:category': [{ type: PointTransactionTypeFilter; status: PointTransactionStatusFilter }]
  'update:dateRange': [PointDateRangeFilter]
  'update:keyword': [string]
}>()

interface CategoryTab {
  label: string
  type: PointTransactionTypeFilter
  status: PointTransactionStatusFilter
}

const categoryTabs: CategoryTab[] = [
  { label: '全部', type: 'ALL', status: 'ALL' },
  { label: '待入帳', type: 'EARN', status: 'PENDING' },
  { label: '已生效', type: 'EARN', status: 'CONFIRMED' },
  { label: '已使用', type: 'REDEEM', status: 'CONFIRMED' },
  { label: '已過期', type: 'EXPIRE', status: 'ALL' },
  { label: '人工調整', type: 'ADJUST', status: 'ALL' },
  { label: '已撤銷', type: 'REDEEM', status: 'REVERSED' },
]

function isActive(tab: CategoryTab) {
  return props.modelValue.type === tab.type && props.modelValue.status === tab.status
}

// 分類 tabs 顯示筆數：後端只回傳原始 (type, status) 分組數字，分類定義（例如「已過期」對應
// type=EXPIRE 不分狀態）只活在這個檔案，跟真正拿去篩選列表用的 type/status 是同一份定義。
function countFor(tab: CategoryTab): number {
  if (!props.counts) return 0
  if (tab.type === 'ALL') {
    return props.counts.reduce((sum, c) => sum + c.count, 0)
  }
  return props.counts
    .filter(c => c.type === tab.type && (tab.status === 'ALL' || c.status === tab.status))
    .reduce((sum, c) => sum + c.count, 0)
}

function countLabel(tab: CategoryTab): string | null {
  const n = countFor(tab)
  if (n <= 0) return null
  return n > 99 ? '99+' : String(n)
}

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
