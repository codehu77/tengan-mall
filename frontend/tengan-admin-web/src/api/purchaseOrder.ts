import { http } from "@/utils/http";

/** 對齊 tengan-admin PurchaseOrderController 的回應形狀（BFF 轉發 tengan-inventory）。 */
export type PurchaseOrderSummary = {
  id: number;
  poNumber: string;
  wareId: number;
  supplierName?: string;
  status: number;
  createdAt: string;
  receivedAt?: string;
};

export type PurchaseOrderListResult = {
  items: Array<PurchaseOrderSummary>;
  total: number;
};

export type PurchaseOrderQuery = {
  status?: number;
  wareId?: number;
  page: number;
  pageSize: number;
};

export const getPurchaseOrderList = (params: PurchaseOrderQuery) => {
  return http.request<PurchaseOrderListResult>(
    "get",
    "/api/admin/inventory/purchase-orders",
    { params }
  );
};

export type PurchaseOrderItem = {
  id: number;
  skuId: number;
  orderedQty: number;
  receivedQty?: number;
};

export type PurchaseOrderDetail = {
  id: number;
  poNumber: string;
  wareId: number;
  supplierName?: string;
  status: number;
  createdBy: string;
  createdAt: string;
  receivedAt?: string;
  items: Array<PurchaseOrderItem>;
};

export const getPurchaseOrderDetail = (id: number) => {
  return http.request<PurchaseOrderDetail>(
    "get",
    `/api/admin/inventory/purchase-orders/${id}`
  );
};

export type CreatePurchaseOrderItemData = {
  skuId: number;
  orderedQty: number;
};

export type CreatePurchaseOrderData = {
  wareId: number;
  supplierName?: string;
  items: Array<CreatePurchaseOrderItemData>;
};

export const createPurchaseOrder = (data: CreatePurchaseOrderData) => {
  return http.request<{ id: number }>(
    "post",
    "/api/admin/inventory/purchase-orders",
    { data }
  );
};

export type ReceivePurchaseOrderItemData = {
  itemId: number;
  receivedQty: number;
};

export const receivePurchaseOrder = (
  id: number,
  data: { items: Array<ReceivePurchaseOrderItemData> }
) => {
  return http.request<void>(
    "post",
    `/api/admin/inventory/purchase-orders/${id}/receive`,
    { data }
  );
};
