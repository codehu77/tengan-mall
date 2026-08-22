import { http } from "@/utils/http";

/** 對齊 tengan-admin SeckillActivityController 的回應形狀（BFF 轉發 tengan-seckill）。 */
export type ActivityItem = {
  id: number;
  activityType: "FLASH_SALE" | "LAUNCH";
  startTime: string;
  endTime: string;
  sessionId: number | null;
  activityDate: string | null;
  sessionName: string | null;
  status: "DRAFT" | "PUBLISHED" | "ACTIVE" | "SETTLED";
};

export type ActivityListResult = {
  items: Array<ActivityItem>;
  total: number;
};

export const getActivityList = () => {
  return http.request<ActivityListResult>(
    "get",
    "/api/admin/seckill/activities"
  );
};

export type SkuItem = {
  id: number;
  skuId: number;
  seckillPrice: number;
  seckillCount: number;
  limitPerUser: number;
  soldCount: number;
  settledAt: string | null;
};

export type ActivityDetail = ActivityItem & {
  skus: Array<SkuItem>;
};

export const getActivity = (id: number) => {
  return http.request<ActivityDetail>(
    "get",
    `/api/admin/seckill/activities/${id}`
  );
};

/** FLASH_SALE 填 sessionId+activityDate；LAUNCH 填 startTime+endTime（另一組留空）。 */
export type CreateActivityData = {
  activityType: "FLASH_SALE" | "LAUNCH";
  sessionId?: number | null;
  activityDate?: string | null;
  startTime?: string | null;
  endTime?: string | null;
};

export const createActivity = (data: CreateActivityData) => {
  return http.request<{ id: number }>(
    "post",
    "/api/admin/seckill/activities",
    { data }
  );
};

export const deleteActivity = (id: number) => {
  return http.request<void>("delete", `/api/admin/seckill/activities/${id}`);
};

export type SkuItemInput = {
  skuId: number;
  seckillPrice: number;
  seckillCount: number;
  limitPerUser: number;
};

/** 不用等 WarmUpScheduler 固定的每日四個時間點，demo/測試新建的場次可以立刻從 PUBLISHED 轉 ACTIVE。 */
export const triggerWarmUpNow = () => {
  return http.request<{ count: number }>(
    "post",
    "/api/admin/seckill/warmup-now"
  );
};

/** 選 SPU 建活動時，依真實庫存比例算各規格的建議配額——純建議值，畫面上還是可以自由覆蓋每一列。 */
export type SpuSkuSuggestion = {
  skuId: number;
  variantLabel: string;
  mainImage?: string;
  realStock: number;
  suggestedQuota: number;
};

export const getSpuSkuSuggestions = (spuId: number, totalQuota: number) => {
  return http.request<{ items: Array<SpuSkuSuggestion> }>(
    "get",
    "/api/admin/seckill/spu-skus",
    { params: { spuId, totalQuota } }
  );
};

/** 重新編輯既有活動的商品時回查目前的設定——一場活動可以綁多個商品（SPU），空陣列代表這場活動還沒設定過商品。 */
export type ActivitySpuSkus = {
  spuId: number;
  spuName: string;
  spuMainImage?: string;
  seckillPrice: number;
  limitPerUser: number;
  items: Array<SpuSkuSuggestion>;
};

export const getActivitySpuSkus = (activityId: number) => {
  return http.request<{ items: Array<ActivitySpuSkus> }>(
    "get",
    `/api/admin/seckill/activities/${activityId}/spu-skus`
  );
};

/** 設定活動商品列表頁的新增/編輯/刪除單一商品用——只覆蓋這個商品範圍，其餘商品不受影響；
 * skuIds 是這個商品目前全部的規格（界定覆蓋範圍），items 可以是空陣列代表整個商品從活動移除。 */
export const replaceProductSkus = (
  activityId: number,
  spuId: number,
  skuIds: Array<number>,
  items: Array<SkuItemInput>
) => {
  return http.request<void>(
    "put",
    `/api/admin/seckill/activities/${activityId}/products/${spuId}/skus`,
    { data: { skuIds, items } }
  );
};
