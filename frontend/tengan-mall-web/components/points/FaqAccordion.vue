<template>
  <div class="bg-white rounded-xl p-6 border border-gray-100">
    <h2 class="text-xl font-bold text-gray-800 mb-4">點數使用規則 FAQ</h2>

    <PointsStateView :loading="loading" :error="error" :skeleton-rows="3" @retry="$emit('retry')">
      <UAccordion :items="accordionItems" color="gray" variant="soft">
        <template #default="{ item, open }">
          <button
            class="flex items-center justify-between w-full py-3 text-left"
            :class="open ? 'text-red-600' : 'text-gray-700'"
          >
            <span class="text-base font-medium">{{ item.label }}</span>
            <UIcon
              name="i-heroicons-chevron-down"
              class="w-4 h-4 transition-transform"
              :class="[open && 'rotate-180']"
            />
          </button>
        </template>
        <template #item="{ item }">
          <p class="text-base text-gray-500 leading-relaxed pb-4">{{ item.answer }}</p>
        </template>
      </UAccordion>
    </PointsStateView>
  </div>
</template>

<script setup lang="ts">
import type { PointFaqItem } from '~/types/points'

const props = defineProps<{
  faqList: PointFaqItem[]
  loading: boolean
  error?: string | null
}>()

defineEmits<{ retry: [] }>()

const accordionItems = computed(() =>
  props.faqList.map(f => ({ label: f.question, answer: f.answer }))
)
</script>
