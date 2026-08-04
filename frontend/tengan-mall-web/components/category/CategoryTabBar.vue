<template>
  <div class="bg-white border-b border-gray-100 shadow-sm relative z-40" @mouseleave="activeId = null">
    <div class="max-w-7xl mx-auto px-6">

      <!-- 一級分類 Tab 列 -->
      <ul class="flex items-center">
        <li
          v-for="cat in categories"
          :key="cat.catId"
          @mouseenter="activeId = cat.catId"
        >
          <button
            class="px-4 py-3 text-base font-medium whitespace-nowrap transition-colors border-b-2"
            :class="activeId === cat.catId
              ? 'text-red-600 border-red-500'
              : 'text-gray-600 border-transparent hover:text-red-500'"
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
        class="absolute top-full left-0 w-full bg-white border-t border-gray-100 shadow-xl py-6"
      >
        <div class="max-w-7xl mx-auto px-6 flex flex-wrap gap-10">
          <div
            v-for="sub in activeCategory.children"
            :key="sub.catId"
            class="min-w-[120px]"
          >
            <p class="text-base font-semibold text-gray-800 mb-2 flex items-center gap-1.5">
              <span class="w-1 h-3.5 bg-red-500 rounded-full inline-block shrink-0"></span>
              {{ sub.name }}
            </p>
            <div class="flex flex-col gap-1.5 pl-3">
              <NuxtLink
                v-for="item in sub.children"
                :key="item.catId"
                :to="`/search?catId=${item.catId}&catName=${encodeURIComponent(item.name)}`"
                class="text-base text-gray-500 hover:text-red-500 transition-colors"
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
