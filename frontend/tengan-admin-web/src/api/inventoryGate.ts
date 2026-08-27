import { http } from "@/utils/http";

/** 對齊 tengan-admin InventoryGateController 的回應形狀（BFF 轉發 tengan-inventory）。 */
export type GateStatusItem = {
  skuId: number;
  spuId?: number;
  skuName?: string;
  mainImage?: string;
  saleStartTime?: string;
  gateCloseTime?: string;
  purchaseLimitPerUser?: number;
  gateProtectedStock?: number;
  gateWarmedAt?: string;
  gateSettledAt?: string;
  currentAvailablePermits?: number;
  buyersCount?: number;
};

export type GateStatusListResult = {
  items: Array<GateStatusItem>;
};

export const getGateStatusList = () => {
  return http.request<GateStatusListResult>("get", "/api/admin/inventory/gates");
};

/** 不用乾等 GateWarmUpScheduler 固定的每日四個時間點，立即跑一次預熱。 */
export const triggerGateWarmUpNow = () => {
  return http.request<{ count: number }>("post", "/api/admin/inventory/gates/warmup-now");
};
