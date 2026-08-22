import { http } from "@/utils/http";

/** 對齊 tengan-admin SubscriptionController 的回應形狀（BFF 轉發 tengan-payment）。
 * status: 0=PENDING 1=ACTIVE 2=CANCELLED，跟 tengan-payment SubscriptionStatus 對齊。 */
export type SubscriptionRecord = {
  id: number;
  memberId: number;
  memberUsername?: string;
  memberNickname?: string;
  targetTier: string;
  status: number;
  ecpayMerchantTradeNo: string;
  periodAmount: number;
  consecutiveFailures: number;
  paidUntil: string;
  benefitExpiredAt?: string;
  createdAt: string;
  cancelledAt?: string;
};

export type SubscriptionRecordListResult = {
  items: Array<SubscriptionRecord>;
  total: number;
};

export type SubscriptionRecordQuery = {
  memberId?: number;
  status?: number;
  page: number;
  pageSize: number;
};

export const getSubscriptionList = (params: SubscriptionRecordQuery) => {
  return http.request<SubscriptionRecordListResult>(
    "get",
    "/api/admin/subscriptions",
    { params }
  );
};

export type SubscriptionPayment = {
  id: number;
  gwsr: string;
  success: boolean;
  amount: number;
  totalSuccessTimes: number;
  processDate: string;
  createdAt: string;
};

export const getSubscriptionPayments = (subscriptionId: number) => {
  return http.request<Array<SubscriptionPayment>>(
    "get",
    `/api/admin/subscriptions/${subscriptionId}/payments`
  );
};
