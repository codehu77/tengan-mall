import type { CategoryAffinityItem } from '~/composables/useCategoryInterest'

export default defineEventHandler(async (event) => {
  const accessToken = requireAccessToken(event)

  return await callBackend<CategoryAffinityItem[]>('/api/customer/member/category-affinity', {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
})
