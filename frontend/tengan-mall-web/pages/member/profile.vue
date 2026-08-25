<template>
  <div>
    <h1 class="text-2xl font-semibold text-gray-800 mb-6">個人檔案</h1>

    <div v-if="loading" class="py-10 text-center text-gray-400">載入中...</div>

    <div v-else class="max-w-xl space-y-6">
      <!-- 頭像 -->
      <div class="flex items-center gap-4">
        <img :src="form.avatarUrl || FALLBACK_AVATAR" alt="頭像" class="w-20 h-20 rounded-full object-cover border border-gray-100" />
        <div class="flex-1 space-y-2">
          <div class="flex gap-2">
            <UInput v-model="form.avatarUrl" placeholder="貼上頭像圖片網址" class="flex-1" />
            <UButton color="gray" variant="outline" :loading="uploading" @click="triggerFileSelect">
              上傳照片
            </UButton>
            <input
              ref="fileInputRef"
              type="file"
              accept="image/jpeg,image/png,image/webp"
              class="hidden"
              @change="handleFileSelected"
            />
          </div>
          <p v-if="uploadError" class="text-sm text-red-500">{{ uploadError }}</p>
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

useHead({ title: '會員資料' })

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

const uploading = ref(false)
const uploadError = ref('')
const fileInputRef = ref<HTMLInputElement | null>(null)

const form = reactive({ nickname: '', avatarUrl: '' })

function triggerFileSelect() {
  fileInputRef.value?.click()
}

async function handleFileSelected(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  uploadError.value = ''
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const { url } = await $fetch<{ url: string }>('/api/member/avatar', {
      method: 'POST',
      body: formData,
    })
    form.avatarUrl = url
  } catch (e: any) {
    uploadError.value = e.data?.message || e.statusMessage || '上傳失敗，請稍後再試'
  } finally {
    uploading.value = false
    input.value = ''
  }
}

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
