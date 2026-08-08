/** 登入成功後呼叫一次，把訪客購物車併入會員購物車——後端沒登入會回 401，交給呼叫端（useAuth.login）處理。 */
export default defineEventHandler(async (event) => {
  await callCartBackend(event, '/api/customer/cart/merge', { method: 'POST' })
  const { count } = await callCartBackend<{ count: number }>(event, '/api/customer/cart/count')
  return count
})
