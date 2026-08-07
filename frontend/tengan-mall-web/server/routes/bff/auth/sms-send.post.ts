/**
 * 沒有接真實簡訊廠商（見 sms_scope_decision）——後端 /sms/send 直接把驗證碼回在回應裡，
 * 這裡原樣轉發給前端展示，不是洩漏機密（demo 情境下這就是設計行為）。
 */
export default defineEventHandler(async (event) => {
  const body = await readBody<{ phone: string }>(event)
  return await callBackend<{ code: string }>('/api/public/auth/sms/send', {
    method: 'POST',
    body: { phone: body.phone, purpose: 'REGISTER' },
  })
})
