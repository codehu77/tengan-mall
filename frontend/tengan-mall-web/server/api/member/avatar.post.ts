/**
 * 轉發會員頭像上傳到 tengan-media（透過 Gateway `/api/customer/media/images`）——
 * 瀏覽器傳來的 multipart 先用 readMultipartFormData 解析，再重新包成 FormData 轉發，
 * 全站第一次做「BFF 轉發 multipart」（比照 tengan-admin BFF 轉發後台素材上傳的手法）。
 */
export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  const parts = await readMultipartFormData(event)
  const filePart = parts?.find((part) => part.name === 'file')
  if (!filePart || !filePart.data.length) {
    throw createError({ statusCode: 400, statusMessage: '缺少上傳檔案' })
  }

  const formData = new FormData()
  formData.append(
    'file',
    new Blob([filePart.data], { type: filePart.type }),
    filePart.filename ?? 'avatar',
  )

  return await callBackend<{ url: string }>('/api/customer/media/images', {
    method: 'POST',
    body: formData,
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
