<script setup lang="ts">
const { data: seckillData } = await useSeckill()
const activities = computed(() => seckillData.value?.activities ?? [])

const activityLabel: Record<'FLASH_SALE' | 'LAUNCH', string> = {
  FLASH_SALE: '限時搶購',
  LAUNCH: '首發',
}

function discountLabel(sku: { seckillPrice: number; originalPrice: number }) {
  if (sku.originalPrice <= 0) return ''
  const off = Math.round((1 - sku.seckillPrice / sku.originalPrice) * 10)
  return `${off}折`
}
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-8">
    <h1 class="text-2xl font-bold text-gray-800 mb-6">限時搶購 / 首發</h1>

    <div v-if="activities.length === 0" class="bg-white rounded-lg py-24 text-center text-gray-400">
      目前沒有進行中的活動
    </div>

    <section v-for="activity in activities" :key="activity.id" class="mb-10">
      <h2 class="text-lg font-semibold text-gray-700 mb-4 flex items-center gap-2">
        <UBadge color="red" variant="solid">{{ activityLabel[activity.activityType] }}</UBadge>
        <span class="text-sm text-gray-400 font-normal">
          結束時間：{{ new Date(activity.endTime).toLocaleString('zh-TW', { hour12: false }) }}
        </span>
      </h2>

      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-6">
        <div
          v-for="sku in activity.skus"
          :key="sku.skuId"
          class="bg-white rounded-xl shadow-sm hover:shadow-md transition cursor-pointer overflow-hidden"
          @click="navigateTo(`/item/${sku.spuId}`)"
        >
          <div class="relative aspect-square overflow-hidden bg-gray-50">
            <img :src="sku.mainImage" :alt="sku.name" class="w-full h-full object-cover" />
            <span class="absolute top-2 left-2 bg-red-500 text-white text-xs font-bold px-1.5 py-0.5 rounded">
              {{ discountLabel(sku) }}
            </span>
          </div>
          <div class="p-3">
            <p class="text-sm text-gray-700 line-clamp-2 mb-2 min-h-[2.5rem]">{{ sku.name }}</p>
            <p class="text-red-500 font-bold text-lg leading-none mb-1">NT$ {{ sku.seckillPrice.toLocaleString() }}</p>
            <p class="text-gray-400 text-xs line-through mb-2">NT$ {{ sku.originalPrice.toLocaleString() }}</p>
            <p class="text-xs text-gray-500">剩餘 <span class="font-semibold text-gray-700">{{ sku.remaining }}</span> 件</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
