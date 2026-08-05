import { http } from "@/utils/http";

/** 對齊 tengan-admin SearchController 的回應形狀（BFF 轉發 tengan-search）。 */
export type ReindexResult = {
  indexedCount: number;
};

export const reindexSearch = () => {
  return http.request<ReindexResult>("post", "/api/admin/search/reindex");
};
