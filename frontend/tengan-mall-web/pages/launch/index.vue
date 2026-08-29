<script setup lang="ts">
useHead({ title: '即將開賣' })

const page = ref(1)
const { data } = await useLaunchTeaserPage(page)
const items = computed(() => data.value?.items ?? [])
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-8">
    <h1 class="text-2xl font-bold text-gray-800 mb-6">即將開賣</h1>

    <div v-if="items.length === 0" class="bg-white rounded-lg py-24 text-center text-gray-400">
      目前沒有即將開賣的商品
    </div>

    <div v-else class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-6">
      <div
        v-for="product in items"
        :key="product.spuId"
        class="bg-white rounded-xl shadow-sm hover:shadow-md transition cursor-pointer overflow-hidden"
        @click="navigateTo(`/item/${product.spuId}`)"
      >
        <div class="relative aspect-square overflow-hidden bg-gray-50">
          <img :src="product.mainImage" :alt="product.name" class="w-full h-full object-cover" />
        </div>
        <div class="p-3">
          <p class="text-sm text-gray-700 line-clamp-2 mb-2 min-h-[2.5rem]">{{ product.name }}</p>
          <p class="text-gray-700 font-bold text-lg leading-none mb-2">NT$ {{ product.price.toLocaleString() }}</p>
          <LaunchTeaserBadge :sale-start-time="product.saleStartTime" />
        </div>
      </div>
    </div>

    <div v-if="items.length > 0" class="flex justify-center gap-3 mt-8">
      <button
        class="px-4 py-2 rounded border border-gray-200 text-sm text-gray-600 disabled:opacity-40 disabled:cursor-not-allowed"
        :disabled="page <= 1"
        @click="page = Math.max(1, page - 1)"
      >
        上一頁
      </button>
      <button
        class="px-4 py-2 rounded border border-gray-200 text-sm text-gray-600 disabled:opacity-40 disabled:cursor-not-allowed"
        :disabled="items.length < 20"
        @click="page = page + 1"
      >
        下一頁
      </button>
    </div>
  </div>
</template>
