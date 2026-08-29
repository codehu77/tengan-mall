<script setup lang="ts">
/**
 * 首頁 Banner 下方的活動廣告卡（優惠券／PRO 升級），純視覺推廣區塊，不是商品卡，
 * 高度刻意跟 ProductCard 不同。PRO 卡連到真實存在的 /member/subscription 頁面；
 * 優惠券目前沒有對應的「領取」頁面/API，點擊只顯示提示，不假裝已經有這個功能。
 */
withDefaults(
  defineProps<{
    variant: 'coupon' | 'pro'
    title: string
    subtitle: string
    ctaLabel: string
    to?: string
  }>(),
  {},
)

const emit = defineEmits<{ cta: [] }>()

const theme: Record<string, { bg: string; icon: string; iconBg: string; iconColor: string; btn: string }> = {
  coupon: {
    bg: 'bg-gradient-to-br from-[#FFF1F0] to-[#FFF7F0]',
    icon: 'i-heroicons-ticket',
    iconBg: 'bg-danger-soft',
    iconColor: 'text-danger',
    btn: 'bg-danger hover:opacity-90',
  },
  pro: {
    bg: 'bg-gradient-to-br from-brand-lighter to-brand-light',
    icon: 'i-heroicons-sparkles',
    iconBg: 'bg-brand-light',
    iconColor: 'text-brand',
    btn: 'bg-brand hover:bg-brand-hover',
  },
}
</script>

<template>
  <div
    class="rounded-2xl border border-border shadow-card p-6 flex items-center justify-between gap-4"
    :class="theme[variant].bg"
  >
    <div class="min-w-0">
      <p class="text-base font-bold text-heading mb-1">{{ title }}</p>
      <p class="text-sm text-subtle truncate">{{ subtitle }}</p>
    </div>

    <div class="flex items-center gap-3 shrink-0">
      <span class="hidden sm:flex w-11 h-11 rounded-full items-center justify-center" :class="theme[variant].iconBg">
        <UIcon :name="theme[variant].icon" class="w-6 h-6" :class="theme[variant].iconColor" />
      </span>

      <NuxtLink
        v-if="to"
        :to="to"
        class="inline-flex items-center gap-1 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors whitespace-nowrap"
        :class="theme[variant].btn"
      >
        {{ ctaLabel }}
        <UIcon name="i-heroicons-arrow-right" class="w-3.5 h-3.5" />
      </NuxtLink>
      <button
        v-else
        type="button"
        class="inline-flex items-center gap-1 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors whitespace-nowrap"
        :class="theme[variant].btn"
        @click="emit('cta')"
      >
        {{ ctaLabel }}
        <UIcon name="i-heroicons-arrow-right" class="w-3.5 h-3.5" />
      </button>
    </div>
  </div>
</template>
