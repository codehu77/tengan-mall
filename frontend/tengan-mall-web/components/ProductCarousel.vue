<script setup lang="ts">
/**
 * 活動型商品區塊（限時搶購／即將開賣）用的水平捲動容器：實際商品數可能是 5 個也可能 20 個以上，
 * 不假設固定筆數，超過可視寬度就用左右箭頭捲動，不做分頁或篩選。
 * 純版面元件，商品卡片內容由外層傳入的 slot 決定。
 */
const scrollerRef = ref<HTMLElement | null>(null)

function scrollByAmount(dir: 1 | -1) {
  const el = scrollerRef.value
  if (!el) return
  el.scrollBy({ left: dir * el.clientWidth * 0.9, behavior: 'smooth' })
}
</script>

<template>
  <div class="relative">
    <div ref="scrollerRef" class="flex gap-3 overflow-x-auto scroll-smooth snap-x snap-mandatory no-scrollbar pb-1">
      <slot />
    </div>

    <button
      type="button"
      aria-label="上一批"
      class="hidden sm:flex absolute -left-3.5 top-[76px] -translate-y-1/2 w-9 h-9 rounded-full bg-card border border-border shadow-card items-center justify-center text-body hover:text-brand hover:border-brand transition"
      @click="scrollByAmount(-1)"
    >
      <UIcon name="i-heroicons-chevron-left" class="w-4 h-4" />
    </button>
    <button
      type="button"
      aria-label="下一批"
      class="hidden sm:flex absolute -right-3.5 top-[76px] -translate-y-1/2 w-9 h-9 rounded-full bg-card border border-border shadow-card items-center justify-center text-body hover:text-brand hover:border-brand transition"
      @click="scrollByAmount(1)"
    >
      <UIcon name="i-heroicons-chevron-right" class="w-4 h-4" />
    </button>
  </div>
</template>
