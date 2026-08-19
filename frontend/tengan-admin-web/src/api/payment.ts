import { http } from "@/utils/http";

/** 對齊 tengan-admin PaymentController 的回應形狀（BFF 轉發 tengan-payment）。 */
export type PaymentRecord = {
  id: number;
  orderSn: string;
  memberId: number;
  method: string;
  amount: number;
  status: number;
  gatewayTradeNo?: string;
  paidAt?: string;
  createdAt: string;
};

export type PaymentRecordListResult = {
  items: Array<PaymentRecord>;
  total: number;
};

export type PaymentRecordQuery = {
  orderSn?: string;
  method?: string;
  page: number;
  pageSize: number;
};

export const getPaymentRecordList = (params: PaymentRecordQuery) => {
  return http.request<PaymentRecordListResult>("get", "/api/admin/payments", { params });
};

export type PaymentMethodConfig = {
  method: string;
  enabled: boolean;
};

export const getPaymentMethods = () => {
  return http.request<Array<PaymentMethodConfig>>("get", "/api/admin/payments/methods");
};

export const updatePaymentMethodStatus = (method: string, enabled: boolean) => {
  return http.request<void>("put", `/api/admin/payments/methods/${method}/status`, {
    data: { enabled }
  });
};

/** 對齊 tengan-admin ReconcileNowResponse——converged+failed 可能小於 checked（單筆查 ECPay 失敗時）。 */
export type ReconcileNowResult = {
  paymentChecked: number;
  paymentConverged: number;
  paymentFailed: number;
  subscriptionChecked: number;
  subscriptionConverged: number;
  subscriptionFailed: number;
  /** 情境 B：ACTIVE 訂閱續期查帳（paidUntil 已過期但通知遲遲沒進來的候選數）。 */
  renewalChecked: number;
  /** 這次查帳後 paidUntil 真的往後延長成功的筆數。 */
  renewalRecovered: number;
};

export const triggerReconcileNow = () => {
  return http.request<ReconcileNowResult>("post", "/api/admin/payments/reconcile-now");
};
