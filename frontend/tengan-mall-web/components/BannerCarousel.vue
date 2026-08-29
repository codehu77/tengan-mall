<template>
  <div
    v-if="banners.length > 0"
    class="relative rounded-2xl overflow-hidden h-[470px] border border-border shadow-card bg-background"
  >
    <template v-for="(banner, index) in banners" :key="banner.id">
      <component
        :is="banner.linkUrl ? 'a' : 'div'"
        :href="banner.linkUrl ?? undefined"
        class="absolute inset-0 transition-opacity duration-500"
        :class="index === activeIndex ? 'opacity-100' : 'opacity-0 pointer-events-none'"
      >
        <img :src="banner.imageUrl" :alt="banner.title ?? ''" class="w-full h-full object-cover" />
      </component>
    </template>

    <template v-if="banners.length > 1">
      <button
        type="button"
        class="absolute left-3 top-1/2 -translate-y-1/2 bg-white/70 hover:bg-white rounded-full w-8 h-8 flex items-center justify-center"
        @click="prev"
      >
        ‹
      </button>
      <button
        type="button"
        class="absolute right-3 top-1/2 -translate-y-1/2 bg-white/70 hover:bg-white rounded-full w-8 h-8 flex items-center justify-center"
        @click="next"
      >
        ›
      </button>
      <div class="absolute bottom-3 left-1/2 -translate-x-1/2 flex gap-2">
        <button
          v-for="(banner, index) in banners"
          :key="banner.id"
          type="button"
          class="h-2 rounded-full transition-all"
          :class="index === activeIndex ? 'w-5 bg-white' : 'w-2 bg-white/50'"
          @click="activeIndex = index"
        />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { Banner } from '~/composables/useBanners'

const props = defineProps<{ banners: Banner[] }>()

const activeIndex = ref(0)
let timer: ReturnType<typeof setInterval> | undefined

function next() {
  if (props.banners.length === 0) return
  activeIndex.value = (activeIndex.value + 1) % props.banners.length
}

function prev() {
  if (props.banners.length === 0) return
  activeIndex.value = (activeIndex.value - 1 + props.banners.length) % props.banners.length
}

onMounted(() => {
  if (props.banners.length > 1) {
    timer = setInterval(next, 4000)
  }
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>
