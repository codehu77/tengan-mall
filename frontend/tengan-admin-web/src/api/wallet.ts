import { http } from "@/utils/http";

/** 對齊 tengan-admin WalletController 的回應形狀（BFF 轉發 tengan-wallet）。 */
export type WalletRule = {
  cashbackRatePro: number;
  cashbackRateProPlus: number;
  monthlyCapPro?: number;
  monthlyCapProPlus?: number;
  pointExpiryDays: number;
  gracePeriodMinutes: number;
  pointValueRatio: number;
};

export const getWalletRule = () => {
  return http.request<WalletRule>("get", "/api/admin/wallet/rules");
};

export const updateWalletRule = (rule: WalletRule) => {
  return http.request<void>("put", "/api/admin/wallet/rules", { data: rule });
};

export const adjustMemberPoints = (memberId: number, points: number, reason: string) => {
  return http.request<void>("post", `/api/admin/wallet/members/${memberId}/points/adjust`, {
    data: { points, reason }
  });
};

/** FREE/PRO/PRO_PLUS，跟 tengan-wallet MemberTierLevel 對齊。 */
export const updateMemberTier = (memberId: number, tier: string, reason: string) => {
  return http.request<void>("post", `/api/admin/wallet/members/${memberId}/tier`, {
    data: { tier, reason }
  });
};
