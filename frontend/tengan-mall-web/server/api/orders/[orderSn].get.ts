import type { OrderDetail, OrderProcessing } from '~/types/order'

/**
 * 含秒殺項目的訂單可能還在非同步落地中，後端回 202——ofetch 對 2xx 一律視為成功不會拋例外，
 * 這裡改用 $fetch.raw() 才能讀到實際狀態碼，202 時自己也回 202 + { processing: true }，
 * 讓前端能區分「處理中」跟真正的錯誤（見 tengan-order 規劃第 5 節）。
 */
export default defineEventHandler(async (event): Promise<OrderDetail | OrderProcessing> => {
  const accessToken = requireAccessToken(event)
  const orderSn = getRouterParam(event, 'orderSn') as string
  const config = useRuntimeConfig()

  try {
    const response = await $fetch.raw(`${config.public.apiBase}/api/customer/orders/${orderSn}`, {
      headers: { Authorization: `Bearer ${accessToken}` },
    })
    if (response.status === 202) {
      setResponseStatus(event, 202)
      return { processing: true, orderSn }
    }
    return response._data as OrderDetail
  } catch (e: any) {
    throw createError({
      statusCode: e?.response?.status ?? 500,
      statusMessage: e?.response?._data?.message ?? '伺服器錯誤，請稍後再試',
      data: e?.response?._data,
    })
  }
})
