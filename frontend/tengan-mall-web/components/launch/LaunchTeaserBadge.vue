<script setup lang="ts">
const props = defineProps<{
  saleStartTime: string
}>()

const target = computed(() => props.saleStartTime)
const { hh, mm, ss, isPast } = useCountdown(target)
</script>

<template>
  <span
    v-if="!isPast"
    class="inline-flex items-center gap-1 text-xs px-2 py-0.5 rounded-full bg-gray-100 text-gray-500"
  >
    即將開賣・{{ hh }}:{{ mm }}:{{ ss }}
  </span>
  <span v-else class="inline-block text-xs px-2 py-0.5 rounded-full bg-red-50 text-red-500">
    熱門搶購中
  </span>
</template>
