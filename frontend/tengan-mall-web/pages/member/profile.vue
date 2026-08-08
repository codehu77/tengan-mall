<template>
  <div>
    <h1 class="text-2xl font-semibold text-gray-800 mb-6">個人檔案</h1>

    <div v-if="loading" class="py-10 text-center text-gray-400">載入中...</div>

    <div v-else class="max-w-xl space-y-6">
      <!-- 頭像 -->
      <div class="flex items-center gap-4">
        <img :src="form.avatarUrl || FALLBACK_AVATAR" alt="頭像" class="w-20 h-20 rounded-full object-cover border border-gray-100" />
        <div class="flex-1 space-y-2">
          <UInput v-model="form.avatarUrl" placeholder="貼上頭像圖片網址" />
          <div class="flex flex-wrap gap-2">
            <button
              v-for="preset in PRESET_AVATARS"
              :key="preset"
              type="button"
              class="w-10 h-10 rounded-full overflow-hidden border-2 transition"
              :class="form.avatarUrl === preset ? 'border-red-500' : 'border-transparent hover:border-gray-200'"
              @click="form.avatarUrl = preset"
            >
              <img :src="preset" alt="預設頭像" class="w-full h-full object-cover" />
            </button>
          </div>
        </div>
      </div>

      <!-- 基本資料 -->
      <div class="space-y-4">
        <div>
          <label class="block text-base text-gray-500 mb-1">暱稱</label>
          <UInput v-model="form.nickname" placeholder="請輸入暱稱" />
        </div>
        <div>
          <label class="block text-base text-gray-500 mb-1">帳號</label>
          <UInput :model-value="profile?.username" disabled />
        </div>
        <div>
          <label class="block text-base text-gray-500 mb-1">手機號碼</label>
          <UInput :model-value="profile?.phone || '未設定'" disabled />
        </div>
      </div>

      <p v-if="error" class="text-base text-red-500">{{ error }}</p>
      <p v-if="saved" class="text-base text-green-600">已儲存</p>

      <div class="flex justify-end">
        <UButton color="red" size="lg" :loading="saving" @click="handleSave">儲存變更</UButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth', layout: 'member' })

const FALLBACK_AVATAR = 'https://api.dicebear.com/7.x/identicon/svg?seed=tengan-mall'
const PRESET_AVATARS = [
  'https://api.dicebear.com/7.x/identicon/svg?seed=tengan-1',
  'https://api.dicebear.com/7.x/identicon/svg?seed=tengan-2',
  'https://api.dicebear.com/7.x/identicon/svg?seed=tengan-3',
  'https://api.dicebear.com/7.x/identicon/svg?seed=tengan-4',
  'https://api.dicebear.com/7.x/identicon/svg?seed=tengan-5',
  'https://api.dicebear.com/7.x/identicon/svg?seed=tengan-6',
]

const memberStore = useMemberStore()
const profile = computed(() => memberStore.profile)

const loading = ref(true)
const saving = ref(false)
const error = ref('')
const saved = ref(false)

const form = reactive({ nickname: '', avatarUrl: '' })

onMounted(async () => {
  if (!memberStore.profile) {
    await memberStore.fetchProfile()
  }
  if (memberStore.profile) {
    form.nickname = memberStore.profile.nickname
    form.avatarUrl = memberStore.profile.avatarUrl || ''
  }
  loading.value = false
})

async function handleSave() {
  error.value = ''
  saved.value = false
  saving.value = true
  try {
    await memberStore.updateProfile(form.nickname, form.avatarUrl)
    saved.value = true
  } catch (e: any) {
    error.value = e.data?.message || e.statusMessage || '儲存失敗，請稍後再試'
  } finally {
    saving.value = false
  }
}
</script>
