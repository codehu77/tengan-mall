<template>
  <div class="w-full max-w-md">

    <!-- Logo -->
    <div class="text-center mb-8">
      <NuxtLink to="/" class="text-3xl font-bold text-red-600">天願商城</NuxtLink>
      <p class="text-gray-500 mt-2 text-sm">重設您的登入密碼</p>
    </div>

    <UCard class="shadow-lg">
      <template #header>
        <h1 class="text-xl font-bold text-gray-800 text-center">忘記密碼</h1>
      </template>

      <!-- Step 1：輸入手機/Email -->
      <form v-if="step === 1" class="space-y-5" @submit.prevent="handleForgot">
        <UFormGroup label="手機或 Email" required>
          <UInput
            v-model="identifier"
            placeholder="請輸入註冊時使用的手機號碼或 Email"
            icon="i-heroicons-user"
            size="lg"
            :disabled="loading"
          />
        </UFormGroup>

        <UAlert
          v-if="error"
          color="red"
          variant="soft"
          icon="i-heroicons-exclamation-circle"
          :description="error"
        />

        <UButton type="submit" color="red" size="lg" block :loading="loading">
          發送驗證碼
        </UButton>
      </form>

      <!-- Step 2：輸入驗證碼 -->
      <form v-else-if="step === 2" class="space-y-5" @submit.prevent="handleVerify">
        <p class="text-sm text-gray-500">若此帳號存在，驗證碼已寄出</p>

        <UFormGroup label="驗證碼" required>
          <UInput
            v-model="code"
            placeholder="請輸入驗證碼"
            icon="i-heroicons-shield-check"
            size="lg"
            :disabled="loading"
          />
        </UFormGroup>

        <UAlert
          v-if="error"
          color="red"
          variant="soft"
          icon="i-heroicons-exclamation-circle"
          :description="error"
        />

        <UButton type="submit" color="red" size="lg" block :loading="loading">
          下一步
        </UButton>
        <UButton color="gray" variant="ghost" size="lg" block :disabled="loading" @click="step = 1">
          返回上一步
        </UButton>
      </form>

      <!-- Step 3：設定新密碼 -->
      <form v-else class="space-y-5" @submit.prevent="handleReset">
        <UFormGroup label="新密碼" required>
          <PasswordInput v-model="newPassword" placeholder="請設定新密碼（至少 6 碼）" :disabled="loading" />
        </UFormGroup>

        <UAlert
          v-if="error"
          color="red"
          variant="soft"
          icon="i-heroicons-exclamation-circle"
          :description="error"
        />

        <UButton type="submit" color="red" size="lg" block :loading="loading">
          完成重設
        </UButton>
      </form>

      <template #footer>
        <div class="text-center text-sm text-gray-500">
          想起密碼了？
          <NuxtLink to="/login" class="text-red-600 font-medium hover:underline">
            返回登入
          </NuxtLink>
        </div>
      </template>
    </UCard>

  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

useHead({ title: '忘記密碼' })

const step = ref(1)
const identifier = ref('')
const code = ref('')
const newPassword = ref('')
const resetToken = ref('')
const { forgotPassword, verifyForgotPassword, resetPassword, loading, error } = useAuth()

async function handleForgot() {
  const ok = await forgotPassword(identifier.value)
  if (ok) step.value = 2
}

async function handleVerify() {
  const token = await verifyForgotPassword(identifier.value, code.value)
  if (token) {
    resetToken.value = token
    step.value = 3
  }
}

async function handleReset() {
  await resetPassword(resetToken.value, newPassword.value)
}
</script>
