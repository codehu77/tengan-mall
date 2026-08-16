<template>
  <UModal :model-value="open" @update:model-value="$emit('close')">
    <UCard>
      <template #header>
        <p class="text-lg font-semibold text-gray-700">交易詳情</p>
      </template>

      <PointsStateView :loading="loading" :error="error" :skeleton-rows="1" @retry="$emit('retry')">
        <template #loading>
          <div class="space-y-3 py-2">
            <USkeleton class="h-5 w-2/3" />
            <USkeleton class="h-4 w-1/2" />
            <USkeleton class="h-24 w-full" />
          </div>
        </template>

        <div v-if="transaction" class="space-y-4 py-1">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <UBadge :color="(TRANSACTION_TYPE_META[transaction.type].color as any)" variant="subtle">
                {{ TRANSACTION_TYPE_META[transaction.type].label }}
              </UBadge>
              <span class="text-base text-gray-400">{{ TRANSACTION_STATUS_META[transaction.status].label }}</span>
            </div>
            <p class="text-3xl font-bold" :class="transaction.points >= 0 ? 'text-green-600' : 'text-gray-700'">
              {{ transaction.points >= 0 ? '+' : '' }}{{ transaction.points.toLocaleString() }} 點
            </p>
          </div>

          <div>
            <p class="text-lg text-gray-800 font-medium">{{ transaction.title }}</p>
            <p class="text-base text-gray-500 mt-1 leading-relaxed">{{ transaction.description }}</p>
          </div>

          <dl class="bg-gray-50 rounded-lg p-4 space-y-2.5 text-base">
            <div class="flex justify-between">
              <dt class="text-gray-400">交易時間</dt>
              <dd class="text-gray-700">{{ formatDate(transaction.createdAt, true) }}</dd>
            </div>
            <div v-if="transaction.orderSn" class="flex justify-between">
              <dt class="text-gray-400">關聯訂單</dt>
              <dd class="text-gray-700 font-mono">{{ transaction.orderSn }}</dd>
            </div>
            <div v-if="transaction.channel" class="flex justify-between">
              <dt class="text-gray-400">交易來源</dt>
              <dd class="text-gray-700">{{ transaction.channel }}</dd>
            </div>
            <div v-if="transaction.operator" class="flex justify-between">
              <dt class="text-gray-400">處理人員</dt>
              <dd class="text-gray-700">{{ transaction.operator }}</dd>
            </div>
            <div v-if="transaction.expiresAt" class="flex justify-between">
              <dt class="text-gray-400">此筆點數效期</dt>
              <dd class="text-gray-700">{{ formatDate(transaction.expiresAt) }}</dd>
            </div>
            <div v-if="transaction.balanceAfter !== null" class="flex justify-between">
              <dt class="text-gray-400">交易後餘額</dt>
              <dd class="text-gray-700">{{ transaction.balanceAfter.toLocaleString() }} 點</dd>
            </div>
          </dl>
        </div>
      </PointsStateView>

      <template #footer>
        <div class="flex justify-end">
          <button
            class="px-5 py-2 text-base text-gray-500 border border-gray-200 rounded-lg hover:bg-gray-50 transition"
            @click="$emit('close')"
          >
            關閉
          </button>
        </div>
      </template>
    </UCard>
  </UModal>
</template>

<script setup lang="ts">
import { TRANSACTION_TYPE_META, TRANSACTION_STATUS_META } from '~/types/points'
import type { PointTransaction } from '~/types/points'

defineProps<{
  open: boolean
  transaction: PointTransaction | null
  loading: boolean
  error?: string | null
}>()

defineEmits<{ close: []; retry: [] }>()
</script>
