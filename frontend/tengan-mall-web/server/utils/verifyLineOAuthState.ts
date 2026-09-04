/**
 * `line.post.ts`/`line-link.post.ts` 共用的 state/nonce 驗證——真正的 CSRF 關卡（見
 * `line-start.get.ts` 的說明）。`line_oauth_state` cookie 存的是 `line-start.get.ts` 種下的
 * `"${mode}:${state}:${nonce}"`，這裡拆開比對 `mode`（呼叫的是登入還是連結端點）跟 `state`（前端
 * 從 LINE 回跳帶回來的值）是否跟 cookie 裡的相符，不符就直接 400 擋下，不會呼叫到 tengan-auth。
 * 用完（不論成功失敗）都清掉 cookie，避免同一組 state/nonce 被重放。回傳 `nonce` 讓呼叫端轉送給
 * 後端做 id_token 的 nonce claim 驗證。
 */
export function verifyLineOAuthState(
  event: Parameters<typeof getCookie>[0],
  expectedMode: 'login' | 'link',
  state: string,
): string {
  const cookieValue = getCookie(event, 'line_oauth_state')
  deleteCookie(event, 'line_oauth_state', { path: '/' })

  const [mode, cookieState, nonce] = cookieValue?.split(':') ?? []
  if (mode !== expectedMode || !cookieState || cookieState !== state || !nonce) {
    throw createError({ statusCode: 400, statusMessage: 'LINE 登入驗證失敗，請重新登入' })
  }
  return nonce
}
