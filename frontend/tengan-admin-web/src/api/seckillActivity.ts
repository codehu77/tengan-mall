import { http } from "@/utils/http";

/** 對齊 tengan-admin SeckillActivityController 的回應形狀（BFF 轉發 tengan-seckill）。 */
export type ActivityItem = {
  id: number;
  activityType: "FLASH_SALE" | "LAUNCH";
  startTime: string;
  endTime: string;
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

export type CreateActivityData = {
  activityType: "FLASH_SALE" | "LAUNCH";
  startTime: string;
  endTime: string;
};

export const createActivity = (data: CreateActivityData) => {
  return http.request<{ id: number }>(
    "post",
    "/api/admin/seckill/activities",
    { data }
  );
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
