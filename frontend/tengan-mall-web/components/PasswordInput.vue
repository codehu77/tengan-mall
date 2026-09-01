<template>
  <div class="relative">
    <UInput
      :model-value="modelValue"
      :type="visible ? 'text' : 'password'"
      :placeholder="placeholder"
      icon="i-heroicons-lock-closed"
      size="lg"
      :disabled="disabled"
      class="w-full"
      @update:model-value="$emit('update:modelValue', $event)"
    />
    <button
      type="button"
      class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
      tabindex="-1"
      @click="visible = !visible"
    >
      <UIcon :name="visible ? 'i-heroicons-eye-slash' : 'i-heroicons-eye'" class="w-5 h-5" />
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: string
  placeholder?: string
  disabled?: boolean
}>()

defineEmits<{ (e: 'update:modelValue', value: string): void }>()

const visible = ref(false)
</script>
