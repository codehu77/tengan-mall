<template>
  <div>
    <div v-if="mode === 'view'" class="flex items-center gap-4">
      <div class="w-44 shrink-0 flex items-center gap-2.5">
        <UIcon :name="icon" class="w-5 h-5 text-gray-400 shrink-0" />
        <div class="min-w-0">
          <p class="text-sm font-semibold text-gray-800">{{ label }}</p>
          <p v-if="hint" class="text-xs text-gray-400 truncate">{{ hint }}</p>
        </div>
      </div>
      <p class="flex-1 min-w-0 truncate text-[15px] text-gray-800">{{ currentValue || '未設定' }}</p>
      <UButton color="gray" variant="outline" size="sm" class="shrink-0" @click="startEdit">修改</UButton>
    </div>

    <div v-else-if="mode === 'edit'" class="bg-gray-50 rounded-xl border border-gray-100 p-4 space-y-3">
      <p class="text-sm font-medium text-gray-600">修改{{ label }}</p>
      <p class="text-sm text-gray-400">目前：{{ currentValue || '未設定' }}</p>
      <UInput v-model="newValue" :placeholder="placeholder" :icon="icon" size="lg" :disabled="loading" />
      <PasswordInput v-model="currentPassword" placeholder="請輸入目前密碼（第三方登入帳號可留空）" :disabled="loading" />
      <p v-if="error" class="text-sm text-red-500">{{ error }}</p>
      <div class="flex gap-2 pt-1">
        <UButton color="red" :loading="loading" @click="handleStart">發送驗證碼</UButton>
        <UButton color="gray" variant="ghost" :disabled="loading" @click="cancel">取消</UButton>
      </div>
    </div>

    <div v-else class="bg-gray-50 rounded-xl border border-gray-100 p-4 space-y-3">
      <p class="text-sm font-medium text-gray-600">驗證碼已發送至 {{ newValue }}</p>
      <UInput v-model="code" placeholder="請輸入驗證碼" icon="i-heroicons-shield-check" size="lg" :disabled="loading" />
      <p v-if="otpCode" class="text-xs text-blue-500">[展示模式] 驗證碼：{{ otpCode }}</p>
      <p v-if="error" class="text-sm text-red-500">{{ error }}</p>
      <div class="flex gap-2 pt-1">
        <UButton color="red" :loading="loading" @click="handleVerify">確認</UButton>
        <UButton color="gray" variant="ghost" :disabled="loading" @click="cancel">取消</UButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 電話/Email 共用同一顆元件（UI 邏輯一樣），但刻意不共用狀態——每個實例各自呼叫 useAuth()，
 * loading/error/otpCode 完全獨立，避免修改 Email 出錯時，電話那格也跟著顯示同一個錯誤訊息
 * （之前父層只呼叫一次 useAuth() 共用給兩個實例，就是這個坑的根源）。
 */
const props = defineProps<{
  label: string
  currentValue: string | null
  placeholder: string
  icon: string
  field: 'phone' | 'email'
  hint?: string
}>()

const {
  startChangePhone,
  verifyChangePhone,
  startChangeEmail,
  verifyChangeEmail,
  loading,
  error,
  otpCode,
} = useAuth()

const mode = ref<'view' | 'edit' | 'verify'>('view')
const newValue = ref('')
const currentPassword = ref('')
const code = ref('')

function startEdit() {
  mode.value = 'edit'
  newValue.value = ''
  currentPassword.value = ''
  code.value = ''
}

function cancel() {
  mode.value = 'view'
}

async function handleStart() {
  const ok = props.field === 'phone'
    ? await startChangePhone(newValue.value, currentPassword.value)
    : await startChangeEmail(newValue.value, currentPassword.value)
  if (ok) mode.value = 'verify'
}

async function handleVerify() {
  const ok = props.field === 'phone'
    ? await verifyChangePhone(newValue.value, code.value)
    : await verifyChangeEmail(newValue.value, code.value)
  if (ok) {
    mode.value = 'view'
    useToast().add({ title: '修改成功', description: `${props.label}已更新為 ${newValue.value}`, color: 'green', timeout: 3000 })
  }
}
</script>
