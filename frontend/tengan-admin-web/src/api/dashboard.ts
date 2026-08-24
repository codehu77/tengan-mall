import { http } from "@/utils/http";

/** 對齊 tengan-admin DashboardController 的回應形狀。 */
export type DailyRevenuePoint = {
  date: string;
  revenue: number;
};

export type DashboardSummary = {
  todayNewOrderCount: number;
  todayRevenue: number;
  monthRevenue: number;
  yearRevenue: number;
  pendingShipmentCount: number;
  lowStockSkuCount: number;
  revenueTrend: Array<DailyRevenuePoint>;
};

export const getDashboardSummary = () => {
  return http.request<DashboardSummary>("get", "/api/admin/dashboard/summary");
};
