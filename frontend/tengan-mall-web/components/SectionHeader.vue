<script setup lang="ts">
/**
 * 全站共用的 Section 標題列：[Icon] 標題 ... 中間可插自訂內容(倒數/頁籤) ... 查看更多。
 * 首頁的限時搶購／即將開賣／熱門商品／猜你喜歡都共用這個版面，只換 icon/accent/連結。
 */
withDefaults(
  defineProps<{
    icon: string
    title: string
    accent?: 'primary' | 'danger' | 'success'
    to?: string
    toLabel?: string
  }>(),
  { accent: 'primary', toLabel: '查看更多' },
)

const accentBg: Record<string, string> = {
  primary: 'bg-brand',
  danger: 'bg-danger',
  success: 'bg-success',
}
</script>

<template>
  <div class="flex items-center gap-4 mb-4">
    <div class="flex items-center gap-2 shrink-0">
      <span class="flex items-center justify-center w-7 h-7 rounded-lg text-white" :class="accentBg[accent]">
        <UIcon :name="icon" class="w-4 h-4" />
      </span>
      <h2 class="text-lg font-bold text-heading">{{ title }}</h2>
    </div>

    <div class="flex-1 flex items-center gap-3 min-w-0 overflow-x-auto no-scrollbar">
      <slot />
    </div>

    <NuxtLink
      v-if="to"
      :to="to"
      class="text-sm text-subtle hover:text-brand flex items-center gap-1 transition-colors shrink-0"
    >
      {{ toLabel }}
      <UIcon name="i-heroicons-chevron-right" class="w-4 h-4" />
    </NuxtLink>
  </div>
</template>
