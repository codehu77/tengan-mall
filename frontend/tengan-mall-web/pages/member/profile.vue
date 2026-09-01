<template>
  <div>
    <div class="mb-6">
      <h1 class="text-[28px] font-bold text-gray-800">個人檔案</h1>
      <p class="text-sm text-gray-400 mt-1">管理你的個人資料與聯絡方式</p>
    </div>

    <div v-if="loading" class="py-10 text-center text-gray-400">載入中...</div>

    <div v-else class="space-y-6">
      <!-- 個人頭像 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-8">
        <h2 class="text-lg font-semibold text-gray-800 mb-6">個人頭像</h2>

        <div class="flex items-center gap-6">
          <div class="relative shrink-0">
            <img
              v-if="form.avatarUrl"
              :src="form.avatarUrl"
              alt="頭像"
              class="w-20 h-20 rounded-full object-cover border border-gray-100"
              @error="form.avatarUrl = ''"
            />
            <div v-else class="w-20 h-20 rounded-full bg-gray-50 border border-gray-100 flex items-center justify-center">
              <UIcon name="i-heroicons-user" class="w-9 h-9 text-gray-300" />
            </div>
            <button
              type="button"
              class="absolute bottom-0 right-0 w-7 h-7 rounded-full bg-white border border-gray-100 shadow-sm flex items-center justify-center hover:bg-gray-50 transition"
              aria-label="上傳照片"
              @click="triggerFileSelect"
            >
              <UIcon name="i-heroicons-camera" class="w-3.5 h-3.5 text-gray-500" />
            </button>
          </div>

          <div class="min-w-0 flex-1">
            <p class="text-base font-semibold text-gray-800 truncate">{{ profile?.nickname || '會員' }}</p>
            <p class="text-sm text-gray-400 mt-0.5">這是您在網站上顯示的名稱與頭像</p>

            <div class="mt-4">
              <UButton color="red" :loading="uploading" @click="triggerFileSelect">
                <UIcon name="i-heroicons-arrow-up-tray" class="w-4 h-4" />
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
            <p class="text-xs text-gray-400 mt-2">建議使用正方形圖片，檔案大小不超過 2MB</p>
            <p v-if="uploadError" class="text-sm text-red-500 mt-2">{{ uploadError }}</p>
          </div>
        </div>
      </div>

      <!-- 基本資料 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-8">
        <h2 class="text-lg font-semibold text-gray-800 mb-2">基本資料</h2>

        <div class="divide-y divide-gray-100">
          <div class="flex items-center gap-4 py-5">
            <div class="w-44 shrink-0 flex items-center gap-2.5">
              <UIcon name="i-heroicons-user" class="w-5 h-5 text-gray-400 shrink-0" />
              <div class="min-w-0">
                <p class="text-sm font-semibold text-gray-800">暱稱</p>
                <p class="text-xs text-gray-400 truncate">您的公開顯示名稱</p>
              </div>
            </div>
            <UInput v-model="form.nickname" placeholder="請輸入暱稱" size="lg" class="flex-1 min-w-0" />
            <UButton
              color="red"
              :variant="isDirty ? 'solid' : 'soft'"
              size="sm"
              class="shrink-0"
              :disabled="!isDirty"
              :loading="saving"
              @click="handleSave"
            >
              儲存
            </UButton>
          </div>

          <div class="py-5">
            <MemberContactEditRow
              label="手機號碼"
              field="phone"
              icon="i-heroicons-phone"
              hint="用於登入與安全驗證"
              :current-value="authStore.user?.phone ?? null"
              placeholder="請輸入新的手機號碼"
            />
          </div>

          <div class="py-5">
            <MemberContactEditRow
              label="Email"
              field="email"
              icon="i-heroicons-envelope"
              hint="用於接收通知與驗證信件"
              :current-value="authStore.user?.email ?? null"
              placeholder="請輸入新的 Email"
            />
          </div>
        </div>

        <div class="mt-2 pt-6 border-t border-gray-100 flex items-center justify-between gap-3">
          <p class="text-xs text-gray-400 flex items-center gap-1.5">
            <UIcon name="i-heroicons-lock-closed" class="w-3.5 h-3.5 shrink-0" />
            為了您的帳號安全，部分資訊修改後需要重新驗證。
          </p>
          <div class="flex items-center gap-3 shrink-0">
            <p v-if="error" class="text-sm text-red-500">{{ error }}</p>
            <p v-if="saved" class="text-sm text-green-600">已儲存</p>
            <UButton
              color="red"
              size="lg"
              :disabled="!isDirty"
              :loading="saving"
              @click="handleSave"
            >
              儲存變更
            </UButton>
          </div>
        </div>
      </div>

      <!-- 帳號安全小提醒 -->
      <div class="bg-white rounded-2xl border border-gray-100 p-8">
        <h2 class="text-lg font-semibold text-gray-800 flex items-center gap-2 mb-6">
          <UIcon name="i-heroicons-shield-check" class="w-5 h-5 text-blue-500" />
          帳號安全小提醒
        </h2>

        <div class="grid grid-cols-1 sm:grid-cols-3 gap-6">
          <div class="flex items-start gap-3">
            <span class="w-10 h-10 rounded-full bg-indigo-50 flex items-center justify-center shrink-0">
              <UIcon name="i-heroicons-lock-closed" class="w-5 h-5 text-indigo-500" />
            </span>
            <div class="min-w-0">
              <p class="text-sm font-semibold text-gray-800">定期更新密碼</p>
              <p class="text-xs text-gray-400 mt-0.5">建議每 3 個月更新一次密碼</p>
            </div>
          </div>

          <div class="flex items-start gap-3">
            <span class="w-10 h-10 rounded-full bg-green-50 flex items-center justify-center shrink-0">
              <UIcon name="i-heroicons-shield-check" class="w-5 h-5 text-green-500" />
            </span>
            <div class="min-w-0">
              <p class="text-sm font-semibold text-gray-800">啟用雙重驗證</p>
              <p class="text-xs text-gray-400 mt-0.5">提升帳號安全性，防止被盜用</p>
            </div>
          </div>

          <div class="flex items-start gap-3">
            <span class="w-10 h-10 rounded-full bg-amber-50 flex items-center justify-center shrink-0">
              <UIcon name="i-heroicons-user" class="w-5 h-5 text-amber-500" />
            </span>
            <div class="min-w-0">
              <p class="text-sm font-semibold text-gray-800">不與他人共用帳號</p>
              <p class="text-xs text-gray-400 mt-0.5">保護您的個人資訊與交易安全</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth', layout: 'member' })

useHead({ title: '會員資料' })

const memberStore = useMemberStore()
const authStore = useAuthStore()
const profile = computed(() => memberStore.profile)

const loading = ref(true)
const saving = ref(false)
const error = ref('')
const saved = ref(false)

const uploading = ref(false)
const uploadError = ref('')
const fileInputRef = ref<HTMLInputElement | null>(null)

const form = reactive({ nickname: '', avatarUrl: '' })
const original = reactive({ nickname: '', avatarUrl: '' })

const isDirty = computed(() => form.nickname !== original.nickname || form.avatarUrl !== original.avatarUrl)

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
    original.nickname = form.nickname
    original.avatarUrl = form.avatarUrl
  }
  loading.value = false
})

async function handleSave() {
  error.value = ''
  saved.value = false
  saving.value = true
  try {
    await memberStore.updateProfile(form.nickname, form.avatarUrl)
    original.nickname = form.nickname
    original.avatarUrl = form.avatarUrl
    saved.value = true
  } catch (e: any) {
    error.value = e.data?.message || e.statusMessage || '儲存失敗，請稍後再試'
  } finally {
    saving.value = false
  }
}
</script>
