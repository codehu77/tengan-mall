<template>
  <div class="h-full">
    <div v-if="loading">
      <slot name="loading">
        <div class="space-y-3">
          <USkeleton v-for="n in skeletonRows" :key="n" class="h-16 w-full rounded-lg" />
        </div>
      </slot>
    </div>

    <div v-else-if="error" class="bg-white rounded-lg py-14 text-center border border-gray-100">
      <UIcon name="i-heroicons-exclamation-triangle" class="w-10 h-10 text-red-300 mx-auto mb-3" />
      <p class="text-gray-600 text-lg mb-1">{{ error }}</p>
      <p class="text-gray-400 text-base mb-4">請稍後再試，或重新整理頁面</p>
      <UButton color="red" variant="outline" size="sm" @click="$emit('retry')">重新載入</UButton>
    </div>

    <div v-else-if="isEmpty">
      <slot name="empty">
        <div class="bg-white rounded-lg py-14 text-center border border-gray-100">
          <UIcon :name="emptyIcon" class="w-10 h-10 text-gray-200 mx-auto mb-3" />
          <p class="text-gray-400 text-lg">{{ emptyTitle }}</p>
        </div>
      </slot>
    </div>

    <div v-else class="h-full">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  loading: boolean
  error?: string | null
  isEmpty?: boolean
  skeletonRows?: number
  emptyIcon?: string
  emptyTitle?: string
}>(), {
  error: null,
  isEmpty: false,
  skeletonRows: 3,
  emptyIcon: 'i-heroicons-inbox',
  emptyTitle: '暫無資料',
})

defineEmits<{ retry: [] }>()
</script>
