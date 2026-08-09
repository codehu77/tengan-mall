import { http } from "@/utils/http";

/** 對齊 tengan-admin InventoryTaskController 的回應形狀（BFF 轉發 tengan-inventory），供客服排查卡單問題。 */
export type TaskItem = {
  id: number;
  orderSn: string;
  status: number;
  createdAt: string;
};

export type TaskListResult = {
  items: Array<TaskItem>;
  total: number;
};

export type TaskQuery = {
  status?: number;
  from?: string;
  to?: string;
  page: number;
  pageSize: number;
};

export const getTaskList = (params: TaskQuery) => {
  return http.request<TaskListResult>("get", "/api/admin/inventory/tasks", {
    params
  });
};
