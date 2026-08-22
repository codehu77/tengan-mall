<script setup lang="ts">
const { data: seckillData } = await useSeckill()
const flashSaleSessions = computed(() => seckillData.value?.flashSaleSessions ?? [])
const launches = computed(() => seckillData.value?.launches ?? [])

function discountLabel(sku: { seckillPrice: number; originalPrice: number }) {
  if (sku.originalPrice <= 0) return ''
  const off = Math.round((1 - sku.seckillPrice / sku.originalPrice) * 10)
  return `${off}折`
}

function sessionLabel(session: { sessionName: string | null; startTime: string; status: 'ACTIVE' | 'PUBLISHED' }) {
  const hhmm = new Date(session.startTime).toLocaleTimeString('zh-TW', { hour: '2-digit', minute: '2-digit', hour12: false })
  const name = session.sessionName ? `${session.sessionName}（${hhmm}）` : hhmm
  return session.status === 'ACTIVE' ? `${name} 現正瘋搶` : `${name} 準時開搶`
}
</script>

<template>
  <div class="max-w-7xl mx-auto px-6 py-8">
    <h1 class="text-2xl font-bold text-gray-800 mb-6">限時搶購 / 首發</h1>

    <div v-if="flashSaleSessions.length === 0 && launches.length === 0" class="bg-white rounded-lg py-24 text-center text-gray-400">
      目前沒有進行中的活動
    </div>

    <!-- 限時搶購：依場次分組，PUBLISHED 場次只能預覽（還沒開賣） -->
    <section v-if="flashSaleSessions.length > 0" class="mb-10">
      <h2 class="text-lg font-semibold text-gray-700 mb-4">
        <UBadge color="red" variant="solid">限時搶購</UBadge>
      </h2>

      <div v-for="session in flashSaleSessions" :key="session.activityId" class="mb-8">
        <h3 class="text-sm font-medium text-gray-600 mb-3 flex items-center gap-2">
          <UBadge :color="session.status === 'ACTIVE' ? 'red' : 'gray'" variant="soft">
            {{ sessionLabel(session) }}
          </UBadge>
        </h3>

        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-6">
          <div
            v-for="sku in session.skus"
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
              <span
                class="inline-block text-xs px-2 py-0.5 rounded-full"
                :class="session.status === 'ACTIVE' ? 'bg-red-50 text-red-500' : 'bg-gray-100 text-gray-400'"
              >
                {{ session.status === 'ACTIVE' ? `剩餘 ${sku.remaining} 件` : '尚未開賣' }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 首發：不分場次，一個活動一區塊 -->
    <section v-for="launch in launches" :key="launch.activityId" class="mb-10">
      <h2 class="text-lg font-semibold text-gray-700 mb-4 flex items-center gap-2">
        <UBadge color="red" variant="solid">首發</UBadge>
        <span class="text-sm text-gray-400 font-normal">
          結束時間：{{ new Date(launch.endTime).toLocaleString('zh-TW', { hour12: false }) }}
        </span>
      </h2>

      <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-6">
        <div
          v-for="sku in launch.skus"
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
            <span class="inline-block text-xs px-2 py-0.5 rounded-full bg-red-50 text-red-500">
              剩餘 {{ sku.remaining }} 件
            </span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
