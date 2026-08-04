<template>
  <div class="flex items-start gap-4 px-5 py-3 border-b border-gray-100">
    <span class="text-sm text-gray-500 w-16 shrink-0 pt-0.5">{{ label }}</span>

    <div class="flex-1 flex flex-wrap gap-x-8 gap-y-2">
      <label
        v-for="opt in visibleOptions"
        :key="opt"
        class="flex items-center gap-1.5 cursor-pointer select-none group"
        @click.prevent="emit('toggle', opt)"
      >
        <span
          class="w-4 h-4 rounded border flex items-center justify-center shrink-0 transition-colors"
          :class="selected.includes(opt)
            ? 'bg-blue-500 border-blue-500'
            : 'border-gray-300 group-hover:border-blue-400'"
        >
          <UIcon v-if="selected.includes(opt)" name="i-heroicons-check" class="w-2.5 h-2.5 text-white" />
        </span>
        <span class="text-sm" :class="selected.includes(opt) ? 'text-blue-600 font-medium' : 'text-gray-700'">
          {{ opt }}
        </span>
      </label>
    </div>

    <button
      class="flex items-center gap-0.5 text-sm text-blue-500 shrink-0 whitespace-nowrap hover:text-blue-600 transition-colors"
      @click="emit('expand')"
    >
      {{ expanded ? '收合' : '選更多' }}
      <UIcon :name="expanded ? 'i-heroicons-chevron-up' : 'i-heroicons-chevron-down'" class="w-3.5 h-3.5" />
    </button>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  label: string
  options: string[]
  selected: string[]
  expanded: boolean
}>()

const emit = defineEmits<{
  toggle: [string]
  expand: []
}>()

const VISIBLE_COUNT = 4

const visibleOptions = computed(() =>
  props.expanded ? props.options : props.options.slice(0, VISIBLE_COUNT)
)
</script>
