/**
 * 統一呼叫 tengan-auth（透過 Gateway）的小工具，把 base URL 前綴跟錯誤轉發集中在一處——
 * server/api/auth/** 底下每支路由都要打後端、都要把後端的 4xx/5xx 原樣轉發給前端，
 * 不集中處理的話每支都要重複同一段 try/catch。
 */
export async function callBackend<T>(path: string, options: Record<string, any> = {}): Promise<T> {
  const config = useRuntimeConfig()
  try {
    return (await $fetch(`${config.public.apiBase}${path}`, options)) as T
  } catch (e: any) {
    throw createError({
      statusCode: e?.response?.status ?? 500,
      statusMessage: e?.response?._data?.message ?? '伺服器錯誤，請稍後再試',
      data: e?.response?._data,
    })
  }
}
