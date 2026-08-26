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

/** synced=false 代表這顆 skuId 還沒同步過商品的開賣時間設定，不是後端出錯，前端顯示提示文字即可。 */
export type GateConfigItem = {
  skuId: number;
  synced: boolean;
  trafficGateEnabled: boolean;
  saleStartTime?: string;
  gateCloseTime?: string;
  gateWarmedAt?: string;
  gateSettledAt?: string;
};

/** 對話框開啟時回填目前設定用。 */
export const getGate = (skuId: number) => {
  return http.request<GateConfigItem>("get", `/api/admin/inventory/gates/${skuId}`);
};

export type ConfigureGateData = {
  trafficGateEnabled: boolean;
  gateCloseTime?: string;
};

/** 管理員在庫存頁面直接設定庫存流量閘門。 */
export const configureGate = (skuId: number, data: ConfigureGateData) => {
  return http.request<void>("put", `/api/admin/inventory/gates/${skuId}`, { data });
};
