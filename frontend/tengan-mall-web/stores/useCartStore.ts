import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', () => {
  const count = ref(0)

  function setCount(n: number) {
    count.value = n
  }

  return { count, setCount }
})
