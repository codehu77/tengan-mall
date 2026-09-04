<template>
  <div class="w-full max-w-md text-center">
    <p class="text-gray-500">正在完成 LINE 登入...</p>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'auth',
})

useHead({ title: 'LINE 登入' })

const route = useRoute()
const toast = useToast()
const { loginWithLine, linkLine } = useAuth()

/**
 * useAuth() 的 error/loading 是每次呼叫各自獨立的 ref（不是跨頁共享狀態），這裡是整頁導轉回跳的
 * 獨立頁面，跟 login.vue/security.vue 是不同的 useAuth() 呼叫實例，所以錯誤訊息改用全域的
 * useToast()（across 頁面導轉仍看得到），不能沿用 security.vue/login.vue 那種綁 error ref 到模板的模式。
 */
onMounted(async () => {
  const code = route.query.code as string | undefined
  const state = route.query.state as string | undefined
  const oauthError = route.query.error as string | undefined
  const mode = sessionStorage.getItem('line_oauth_mode') === 'link' ? 'link' : 'login'
  sessionStorage.removeItem('line_oauth_mode')

  if (oauthError || !code || !state) {
    // access_denied（使用者取消）或其他 LINE 端錯誤，導回原本的頁面即可，不用額外跳錯誤訊息
    await navigateTo(mode === 'link' ? '/member/security' : '/login')
    return
  }

  if (mode === 'link') {
    const ok = await linkLine(code, state)
    toast.add({
      title: ok ? 'LINE 帳號綁定成功' : '綁定 LINE 帳號失敗',
      description: ok ? undefined : '請稍後再試',
      color: ok ? 'green' : 'red',
      timeout: 3000,
    })
    await navigateTo('/member/security')
    return
  }

  const ok = await loginWithLine(code, state)
  if (!ok) {
    toast.add({
      title: 'LINE 登入失敗',
      description: '請稍後再試，或改用帳號密碼登入',
      color: 'red',
      timeout: 3000,
    })
    await navigateTo('/login')
  }
})
</script>
