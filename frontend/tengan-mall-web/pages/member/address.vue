<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-semibold text-gray-800">我的地址</h1>
      <UButton color="red" @click="openCreate">
        <UIcon name="i-heroicons-plus" class="w-4 h-4" />
        新增地址
      </UButton>
    </div>

    <div v-if="store.loading" class="py-14 text-center text-gray-400">載入中...</div>

    <div v-else-if="store.error" class="py-14 text-center space-y-3">
      <p class="text-base text-red-500">{{ store.error }}</p>
      <UButton variant="ghost" color="gray" @click="store.fetchAddresses()">重新載入</UButton>
    </div>

    <div v-else-if="store.addresses.length === 0" class="py-14 text-center">
      <UIcon name="i-heroicons-map-pin" class="w-10 h-10 text-gray-200 mx-auto mb-2" />
      <p class="text-base text-gray-400">尚未新增任何地址</p>
    </div>

    <div v-else class="divide-y divide-gray-100">
      <div
        v-for="item in store.addresses"
        :key="item.id"
        class="py-4 flex items-start justify-between gap-4"
      >
        <div class="space-y-1.5">
          <div class="flex items-center gap-2 text-base">
            <span class="font-semibold text-gray-800">{{ item.receiverName }}</span>
            <span class="text-gray-300">|</span>
            <span class="text-gray-500">{{ item.receiverPhone }}</span>
          </div>
          <p class="text-base text-gray-500">{{ item.street }}</p>
          <p class="text-sm text-gray-400">{{ item.district }}, {{ item.city }}, {{ item.postalCode }}</p>
          <UBadge v-if="item.isDefault" color="red" variant="outline" size="xs">預設</UBadge>
        </div>

        <div class="flex flex-col items-end gap-2 shrink-0">
          <div class="flex gap-2 text-base">
            <button class="text-gray-500 hover:text-red-500 transition" @click="openEdit(item)">編輯</button>
            <span class="text-gray-200">|</span>
            <button class="text-gray-500 hover:text-red-500 transition" @click="handleDelete(item.id)">刪除</button>
          </div>
          <UButton
            v-if="!item.isDefault"
            variant="outline"
            color="gray"
            size="xs"
            @click="store.setDefault(item.id)"
          >
            設定為預設
          </UButton>
        </div>
      </div>
    </div>

    <MemberAddressFormDialog
      :open="dialogOpen"
      :address="editingAddress"
      :saving="saving"
      @close="dialogOpen = false"
      @submit="handleSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import type { AddressFormPayload, MemberAddress } from '~/types/member'

definePageMeta({ middleware: 'auth', layout: 'member' })

const store = useAddressStore()

const dialogOpen = ref(false)
const editingAddress = ref<MemberAddress | null>(null)
const saving = ref(false)

store.fetchAddresses()

function openCreate() {
  editingAddress.value = null
  dialogOpen.value = true
}

function openEdit(item: MemberAddress) {
  editingAddress.value = item
  dialogOpen.value = true
}

async function handleSubmit(payload: AddressFormPayload) {
  saving.value = true
  try {
    if (editingAddress.value) {
      await store.updateAddress(editingAddress.value.id, payload)
    } else {
      await store.createAddress(payload)
    }
    dialogOpen.value = false
  } finally {
    saving.value = false
  }
}

async function handleDelete(id: number) {
  if (confirm('確定要刪除這筆地址嗎？')) {
    await store.deleteAddress(id)
  }
}
</script>
