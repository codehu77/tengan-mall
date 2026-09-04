/**
 * 產生 LINE 授權 URL 並把 state/nonce 種進 HttpOnly cookie——這支是唯一真正驗證 CSRF 的關卡
 * （見 oauth_login_design 的 LINE 補完設計）：`line.post.ts`/`line-link.post.ts` 收到前端傳回的
 * code+state 之後，會拿這顆 cookie 的值來比對，比對通過才會呼叫後端換 token。state/nonce 只活在
 * cookie 裡，前端頁面 JS 從沒拿到、也無法偽造。
 */
export default defineEventHandler((event) => {
  const query = getQuery(event)
  const mode = query.mode === 'link' ? 'link' : 'login'

  const state = crypto.randomUUID()
  const nonce = crypto.randomUUID()

  setCookie(event, 'line_oauth_state', `${mode}:${state}:${nonce}`, {
    httpOnly: true,
    sameSite: 'lax',
    path: '/',
    maxAge: 600,
  })

  const config = useRuntimeConfig()
  const url = new URL('https://access.line.me/oauth2/v2.1/authorize')
  url.searchParams.set('response_type', 'code')
  url.searchParams.set('client_id', config.lineChannelId)
  url.searchParams.set('redirect_uri', config.lineRedirectUri)
  url.searchParams.set('state', state)
  url.searchParams.set('nonce', nonce)
  url.searchParams.set('scope', 'openid profile email')

  return { url: url.toString() }
})
