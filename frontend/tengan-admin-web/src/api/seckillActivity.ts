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

export const updateActivitySkus = (id: number, items: Array<SkuItemInput>) => {
  return http.request<void>(
    "put",
    `/api/admin/seckill/activities/${id}/skus`,
    { data: { items } }
  );
};

/** 不用等 WarmUpScheduler 固定的每日四個時間點，demo/測試新建的場次可以立刻從 PUBLISHED 轉 ACTIVE。 */
export const triggerWarmUpNow = () => {
  return http.request<{ count: number }>(
    "post",
    "/api/admin/seckill/warmup-now"
  );
};
