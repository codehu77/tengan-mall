import { http } from "@/utils/http";

/** 對齊 tengan-admin OrderController 的回應形狀（BFF 轉發 tengan-order）。 */
export type OrderSummary = {
  id: number;
  orderSn: string;
  memberId: number;
  status: number;
  payAmount: number;
  paymentMethod: string;
  createdAt: string;
};

export type OrderListResult = {
  items: Array<OrderSummary>;
  total: number;
};

export type OrderQuery = {
  status?: number;
  page: number;
  pageSize: number;
};

export const getOrderList = (params: OrderQuery) => {
  return http.request<OrderListResult>("get", "/api/admin/orders", { params });
};

export type OrderItem = {
  skuId: number;
  spuId: number;
  skuName: string;
  skuImage?: string;
  price: number;
  count: number;
  subtotal: number;
};

export type OrderDetail = {
  id: number;
  orderSn: string;
  memberId: number;
  status: number;
  cancelReason?: string;
  totalAmount: number;
  discountAmount: number;
  payAmount: number;
  paymentMethod: string;
  couponId?: number;
  receiverName: string;
  receiverPhone: string;
  city: string;
  district: string;
  postalCode?: string;
  street: string;
  remark?: string;
  receiptTime?: string;
  createdAt: string;
  items: Array<OrderItem>;
};

export const getOrderDetail = (orderSn: string) => {
  return http.request<OrderDetail>("get", `/api/admin/orders/${orderSn}`);
};

export const shipOrder = (orderSn: string) => {
  return http.request<void>("put", `/api/admin/orders/${orderSn}/ship`);
};

export const cancelOrder = (orderSn: string, reason: string) => {
  return http.request<void>("put", `/api/admin/orders/${orderSn}/cancel`, {
    data: { reason }
  });
};
