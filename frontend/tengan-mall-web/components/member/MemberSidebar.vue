<template>
  <aside class="w-56 shrink-0">
    <div class="bg-white rounded-xl border border-gray-100 shadow-sm p-5 space-y-6">
      <!-- 頭像與暱稱 -->
      <div class="flex items-center gap-3">
        <img
          v-if="memberStore.profile?.avatarUrl"
          :src="memberStore.profile.avatarUrl"
          alt="頭像"
          class="w-12 h-12 rounded-full object-cover shrink-0"
        />
        <UIcon v-else name="i-heroicons-user-circle" class="w-12 h-12 text-gray-300 shrink-0" />
        <div class="min-w-0">
          <p class="text-base font-semibold text-gray-900 truncate">{{ displayName }}</p>
          <NuxtLink to="/member/profile" class="text-xs text-gray-400 hover:text-red-500 flex items-center gap-1 mt-0.5">
            <UIcon name="i-heroicons-pencil-square" class="w-3 h-3" />
            編輯個人簡介
          </NuxtLink>
        </div>
      </div>

      <!-- 導覽 -->
      <nav class="space-y-5">
        <div>
          <p class="text-xs font-medium text-gray-400 tracking-wide px-2.5 mb-1.5">
            我的帳戶
          </p>
          <div class="space-y-0.5">
            <NuxtLink
              to="/member/profile"
              class="flex items-center gap-2.5 px-2.5 py-2 rounded-lg text-sm transition"
              :class="isActive('/member/profile') ? 'bg-red-50 text-red-500 font-medium' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'"
            >
              <UIcon name="i-heroicons-user" class="w-[18px] h-[18px] shrink-0" />
              個人檔案
            </NuxtLink>
            <NuxtLink
              to="/member/address"
              class="flex items-center gap-2.5 px-2.5 py-2 rounded-lg text-sm transition"
              :class="isActive('/member/address') ? 'bg-red-50 text-red-500 font-medium' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'"
            >
              <UIcon name="i-heroicons-map-pin" class="w-[18px] h-[18px] shrink-0" />
              地址簿
            </NuxtLink>
            <NuxtLink
              to="/member/security"
              class="flex items-center gap-2.5 px-2.5 py-2 rounded-lg text-sm transition"
              :class="isActive('/member/security') ? 'bg-red-50 text-red-500 font-medium' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'"
            >
              <UIcon name="i-heroicons-shield-check" class="w-[18px] h-[18px] shrink-0" />
              帳號安全
            </NuxtLink>
          </div>
        </div>

        <div class="space-y-0.5">
          <NuxtLink
            to="/order/list"
            class="flex items-center gap-2.5 px-2.5 py-2 rounded-lg text-sm transition"
            :class="isActive('/order') ? 'bg-red-50 text-red-500 font-medium' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'"
          >
            <UIcon name="i-heroicons-clipboard-document-list" class="w-[18px] h-[18px] shrink-0" />
            我的訂單
          </NuxtLink>

          <NuxtLink
            to="/member/points"
            class="flex items-center gap-2.5 px-2.5 py-2 rounded-lg text-sm transition"
            :class="isActive('/member/points') ? 'bg-red-50 text-red-500 font-medium' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'"
          >
            <UIcon name="i-heroicons-wallet" class="w-[18px] h-[18px] shrink-0" />
            我的點數
          </NuxtLink>

          <NuxtLink
            to="/member/subscription"
            class="flex items-center gap-2.5 px-2.5 py-2 rounded-lg text-sm transition"
            :class="isActive('/member/subscription') ? 'bg-red-50 text-red-500 font-medium' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'"
          >
            <UIcon name="i-heroicons-star" class="w-[18px] h-[18px] shrink-0" />
            訂閱會員
          </NuxtLink>
        </div>
      </nav>
    </div>
  </aside>
</template>

<script setup lang="ts">
const route = useRoute()
const memberStore = useMemberStore()
const authStore = useAuthStore()

const displayName = computed(() => memberStore.profile?.nickname || authStore.identifier)

function isActive(prefix: string) {
  return route.path.startsWith(prefix)
}
</script>
