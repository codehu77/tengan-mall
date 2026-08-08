import { defineStore } from 'pinia'
import type { AddressFormPayload, MemberAddress } from '~/types/member'

export const useAddressStore = defineStore('address', () => {
  const addresses = ref<MemberAddress[]>([])
  const loading = ref(false)
  const error = ref('')

  async function fetchAddresses() {
    loading.value = true
    error.value = ''
    try {
      addresses.value = await $fetch<MemberAddress[]>('/api/member/addresses')
    } catch (e: any) {
      error.value = e.data?.message || e.statusMessage || '地址載入失敗'
    } finally {
      loading.value = false
    }
  }

  async function createAddress(payload: AddressFormPayload) {
    await $fetch('/api/member/addresses', { method: 'POST', body: payload })
    await fetchAddresses()
  }

  async function updateAddress(id: number, payload: Omit<AddressFormPayload, 'isDefault'>) {
    await $fetch(`/api/member/addresses/${id}`, { method: 'PUT', body: payload })
    await fetchAddresses()
  }

  async function deleteAddress(id: number) {
    await $fetch(`/api/member/addresses/${id}`, { method: 'DELETE' })
    await fetchAddresses()
  }

  async function setDefault(id: number) {
    await $fetch(`/api/member/addresses/${id}/default`, { method: 'PUT' })
    await fetchAddresses()
  }

  return { addresses, loading, error, fetchAddresses, createAddress, updateAddress, deleteAddress, setDefault }
})
