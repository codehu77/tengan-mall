<template>
  <div class="min-h-screen flex flex-col bg-gray-50">
    <LayoutAppHeader />
    <CategoryTabBar
      v-if="showTabBar"
      :categories="categories"
    />
    <main class="flex-1">
      <slot />
    </main>
    <LayoutAppFooter />
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const { categories } = useCategories()
const cartStore = useCartStore()
const { fetchCartCount } = useCart()

const showTabBar = computed(() =>
  route.path === '/' || route.path.startsWith('/search') || route.path.startsWith('/item/')
)

const { data: initialCartCount } = await useAsyncData('cart-count', fetchCartCount)
cartStore.setCount(initialCartCount.value ?? 0)
</script>
