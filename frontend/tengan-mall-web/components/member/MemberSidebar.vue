<template>
  <aside class="w-56 shrink-0 space-y-6">
    <!-- 頭像與暱稱 -->
    <div class="flex items-center gap-3">
      <img
        v-if="memberStore.profile?.avatarUrl"
        :src="memberStore.profile.avatarUrl"
        alt="頭像"
        class="w-14 h-14 rounded-full object-cover shrink-0"
      />
      <UIcon v-else name="i-heroicons-user-circle" class="w-14 h-14 text-gray-300 shrink-0" />
      <div class="min-w-0">
        <p class="text-lg font-bold text-gray-800 truncate">{{ displayName }}</p>
        <NuxtLink to="/member/profile" class="text-sm text-gray-400 hover:text-red-500 flex items-center gap-1">
          <UIcon name="i-heroicons-pencil-square" class="w-3.5 h-3.5" />
          編輯個人簡介
        </NuxtLink>
      </div>
    </div>

    <!-- 導覽 -->
    <nav class="space-y-4">
      <div>
        <p class="flex items-center gap-2 text-base text-gray-500 mb-1.5">
          <UIcon name="i-heroicons-user" class="w-5 h-5" />
          我的帳戶
        </p>
        <div class="pl-7 space-y-1">
          <NuxtLink
            to="/member/profile"
            class="block py-1 text-base transition"
            :class="isActive('/member/profile') ? 'text-red-500 font-medium' : 'text-gray-500 hover:text-red-500'"
          >
            個人檔案
          </NuxtLink>
          <NuxtLink
            to="/member/address"
            class="block py-1 text-base transition"
            :class="isActive('/member/address') ? 'text-red-500 font-medium' : 'text-gray-500 hover:text-red-500'"
          >
            地址簿
          </NuxtLink>
        </div>
      </div>

      <NuxtLink
        to="/order/list"
        class="flex items-center gap-2 py-1 text-base transition"
        :class="isActive('/order') ? 'text-red-500 font-medium' : 'text-gray-500 hover:text-red-500'"
      >
        <UIcon name="i-heroicons-clipboard-document-list" class="w-5 h-5" />
        我的訂單
      </NuxtLink>

      <NuxtLink
        to="/member/points"
        class="flex items-center gap-2 py-1 text-base transition"
        :class="isActive('/member/points') ? 'text-red-500 font-medium' : 'text-gray-500 hover:text-red-500'"
      >
        <UIcon name="i-heroicons-wallet" class="w-5 h-5" />
        我的點數
      </NuxtLink>
    </nav>
  </aside>
</template>

<script setup lang="ts">
const route = useRoute()
const memberStore = useMemberStore()
const authStore = useAuthStore()

const displayName = computed(() => memberStore.profile?.nickname || authStore.username)

function isActive(prefix: string) {
  return route.path.startsWith(prefix)
}
</script>
