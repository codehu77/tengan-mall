import { http } from "@/utils/http";

/** 對齊 tengan-admin AdminOperLogController 的回應形狀。 */
export type OperLogItem = {
  id: number;
  adminUserId: number;
  username: string;
  module: string;
  action: string;
  targetDesc?: string;
  resultStatus: number; // 1=成功 0=拒絕/失敗
  createdAt: string;
};

export type OperLogSearchResult = {
  items: Array<OperLogItem>;
  total: number;
};

export type OperLogSearchParams = {
  adminId?: number;
  module?: string;
  from?: string; // ISO-8601，例如 2026-07-31T00:00:00
  to?: string;
  pageNum: number;
  pageSize: number;
};

export const searchOperLogs = (params: OperLogSearchParams) => {
  return http.request<OperLogSearchResult>(
    "get",
    "/api/admin/system/logs",
    { params }
  );
};
