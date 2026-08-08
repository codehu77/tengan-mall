<template>
  <UModal :model-value="open" @update:model-value="$emit('close')">
    <UCard>
      <template #header>
        <p class="text-lg font-semibold text-gray-700">{{ editing ? '編輯地址' : '新增地址' }}</p>
      </template>

      <div class="space-y-4 py-1">
        <div>
          <label class="block text-sm text-gray-600 mb-1">收件人姓名</label>
          <input v-model="form.receiverName" type="text" placeholder="請輸入收件人姓名"
            class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">收件人電話</label>
          <input v-model="form.receiverPhone" type="tel" placeholder="請輸入收件人電話"
            class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">地址</label>
          <div class="grid grid-cols-2 gap-4 mb-3">
            <div class="relative">
              <select v-model="form.city"
                class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700 bg-white appearance-none pr-9">
                <option v-for="city in taiwanCities" :key="city" :value="city">{{ city }}</option>
              </select>
              <UIcon name="i-heroicons-chevron-down" class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
            </div>
            <div class="relative">
              <select v-model="form.district"
                class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700 bg-white appearance-none pr-9">
                <option v-for="d in districts" :key="d" :value="d">{{ d }}</option>
              </select>
              <UIcon name="i-heroicons-chevron-down" class="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
            </div>
          </div>
          <input v-model="form.street" type="text" placeholder="請輸入詳細地址"
            class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">郵遞區號</label>
          <input v-model="form.postalCode" type="text" placeholder="請輸入郵遞區號"
            class="w-full text-base border border-gray-200 rounded-lg px-3 py-2.5 outline-none focus:border-red-300 text-gray-700" />
        </div>
        <label v-if="!editing" class="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
          <input type="checkbox" v-model="form.isDefault" class="accent-red-500 w-4 h-4" />
          設為預設地址
        </label>
        <p v-if="error" class="text-sm text-red-500">{{ error }}</p>
      </div>

      <template #footer>
        <div class="flex justify-end gap-2">
          <button
            class="px-5 py-2 text-base text-gray-500 border border-gray-200 rounded-lg hover:bg-gray-50 transition"
            @click="$emit('close')"
          >
            取消
          </button>
          <UButton color="red" :loading="saving" @click="handleSubmit">儲存</UButton>
        </div>
      </template>
    </UCard>
  </UModal>
</template>

<script setup lang="ts">
import type { AddressFormPayload, MemberAddress } from '~/types/member'

const props = defineProps<{
  open: boolean
  address: MemberAddress | null
  saving: boolean
}>()

const emit = defineEmits<{
  close: []
  submit: [payload: AddressFormPayload]
}>()

const editing = computed(() => props.address !== null)
const error = ref('')

const form = reactive({
  receiverName: '',
  receiverPhone: '',
  city: taiwanCities[0] as string,
  district: '',
  postalCode: '',
  street: '',
  isDefault: false,
})

const districts = computed(() => districtMap[form.city] ?? [])

watch(() => form.city, () => {
  if (!districts.value.includes(form.district)) {
    form.district = districts.value[0] ?? ''
  }
})

watch(() => props.open, (isOpen) => {
  if (isOpen) {
    error.value = ''
    form.receiverName = props.address?.receiverName ?? ''
    form.receiverPhone = props.address?.receiverPhone ?? ''
    form.city = props.address?.city ?? taiwanCities[0]
    form.district = props.address?.district ?? (districtMap[form.city]?.[0] ?? '')
    form.postalCode = props.address?.postalCode ?? ''
    form.street = props.address?.street ?? ''
    form.isDefault = props.address?.isDefault ?? false
  }
})

function handleSubmit() {
  if (!form.receiverName.trim() || !form.receiverPhone.trim() || !form.district
    || !form.postalCode.trim() || !form.street.trim()) {
    error.value = '請完整填寫收件人姓名、電話與地址'
    return
  }
  error.value = ''
  emit('submit', { ...form })
}
</script>
