import { http } from "@/utils/http";

/** 對齊 tengan-admin InventoryWarehouseController 的回應形狀（BFF 轉發 tengan-inventory）。 */
export type WarehouseItem = {
  id: number;
  name: string;
  address: string;
};

export type WarehouseListResult = {
  items: Array<WarehouseItem>;
};

export const getWarehouseList = () => {
  return http.request<WarehouseListResult>(
    "get",
    "/api/admin/inventory/warehouses"
  );
};

export type CreateWarehouseData = {
  name: string;
  address: string;
};

export const createWarehouse = (data: CreateWarehouseData) => {
  return http.request<{ id: number }>(
    "post",
    "/api/admin/inventory/warehouses",
    { data }
  );
};
