<template>
  <div class="bg-card border-b border-border relative z-40" @mouseleave="activeId = null">
    <div class="max-w-7xl mx-auto px-6">

      <!-- 一級分類 Tab 列 -->
      <ul class="flex items-center">
        <li
          v-for="cat in categories"
          :key="cat.catId"
          @mouseenter="activeId = cat.catId"
        >
          <button
            class="px-4 py-3 text-sm font-medium whitespace-nowrap transition-colors border-b-2"
            :class="activeId === cat.catId
              ? 'text-brand border-brand'
              : 'text-body border-transparent hover:text-brand'"
          >
            {{ cat.name }}
          </button>
        </li>
      </ul>
    </div>

    <!-- 二三級 Mega Dropdown -->
    <Transition name="slide-down">
      <div
        v-if="activeCategory?.children?.length"
        class="absolute top-full left-0 w-full bg-card border-t border-border shadow-card-hover py-6"
      >
        <div class="max-w-7xl mx-auto px-6 flex flex-wrap gap-10">
          <div
            v-for="sub in activeCategory.children"
            :key="sub.catId"
            class="min-w-[120px]"
          >
            <p class="text-sm font-semibold text-heading mb-2 flex items-center gap-1.5">
              <span class="w-1 h-3.5 bg-brand rounded-full inline-block shrink-0"></span>
              {{ sub.name }}
            </p>
            <div class="flex flex-col gap-1.5 pl-3">
              <NuxtLink
                v-for="item in sub.children"
                :key="item.catId"
                :to="`/search?catId=${item.catId}&catName=${encodeURIComponent(item.name)}`"
                class="text-sm text-subtle hover:text-brand transition-colors"
                @click="activeId = null"
              >
                {{ item.name }}
              </NuxtLink>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import type { CategoryTree } from '~/mocks/products'

const props = defineProps<{
  categories: CategoryTree[]
}>()

const activeId = ref<number | null>(null)

const activeCategory = computed(() =>
  props.categories.find(c => c.catId === activeId.value) ?? null
)
</script>

<style scoped>
.slide-down-enter-active {
  transition: all 0.15s ease-out;
}
.slide-down-leave-active {
  transition: all 0.1s ease-in;
}
.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-6px);
}
.slide-down-leave-to {
  opacity: 0;
}
</style>
