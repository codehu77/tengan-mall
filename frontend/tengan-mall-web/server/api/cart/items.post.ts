/**
 * 只需要 skuId+count——name/price/image 這些由後端向 tengan-product 即時查詢，不是前端該提供的資料
 * （呼叫端目前仍傳整個 product 物件是為了跟舊 mock 介面相容，這裡只取用得到的欄位）。
 */
export default defineEventHandler(async (event) => {
  const body = await readBody<{ skuId: number; count: number; specText?: string }>(event)
  await callCartBackend(event, '/api/customer/cart/items', {
    method: 'POST',
    body: { skuId: body.skuId, count: body.count, specText: body.specText ?? null },
  })
  const { count } = await callCartBackend<{ count: number }>(event, '/api/customer/cart/count')
  return count
})
