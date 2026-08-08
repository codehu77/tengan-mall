/**
 * tengan-cart 是唯一同時服務會員/訪客的後端——會員帶 Authorization，訪客靠瀏覽器的
 * tengan_guest_key cookie（後端 CartIdentityFilter 直接發的，不是 Nuxt 自己管理的 cookie）。
 * Nuxt server 是中介，要雙向轉發這顆 cookie：讀瀏覽器帶來的值當 Cookie header 轉發給後端，
 * 再把後端回應的 Set-Cookie 原樣轉發回瀏覽器——不這樣做的話後端發的訪客 cookie 永遠到不了瀏覽器，
 * 訪客購物車每次都會被當成全新訪客。
 */
export async function callCartBackend<T>(event: Parameters<typeof getCookie>[0], path: string,
    options: Record<string, any> = {}): Promise<T> {
  const config = useRuntimeConfig()
  const accessToken = optionalAccessToken(event)
  const guestKey = getCookie(event, 'tengan_guest_key')

  const headers: Record<string, string> = { ...(options.headers || {}) }
  if (accessToken) headers.Authorization = `Bearer ${accessToken}`
  if (guestKey) headers.Cookie = `tengan_guest_key=${guestKey}`

  try {
    const response = await $fetch.raw(`${config.public.apiBase}${path}`, { ...options, headers })
    const setCookie = response.headers.get('set-cookie')
    if (setCookie) {
      appendResponseHeader(event, 'set-cookie', setCookie)
    }
    return response._data as T
  } catch (e: any) {
    throw createError({
      statusCode: e?.response?.status ?? 500,
      statusMessage: e?.response?._data?.message ?? '伺服器錯誤，請稍後再試',
      data: e?.response?._data,
    })
  }
}
