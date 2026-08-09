import { http } from "@/utils/http";

/** 對齊 tengan-admin CouponTemplateController 的回應形狀（BFF 轉發 tengan-coupon）。 */
export type TemplateItem = {
  id: number;
  name: string;
  thresholdAmount: number;
  discountAmount: number;
  totalCount: number;
  issuedCount: number;
  effectiveStart: string;
  effectiveEnd: string;
  status: number;
};

export type TemplateListResult = {
  items: Array<TemplateItem>;
};

export const getTemplateList = () => {
  return http.request<TemplateListResult>(
    "get",
    "/api/admin/coupons/templates"
  );
};

export type TemplateFormData = {
  name: string;
  thresholdAmount: number;
  discountAmount: number;
  totalCount: number;
  effectiveStart: string;
  effectiveEnd: string;
};

export const createTemplate = (data: TemplateFormData) => {
  return http.request<{ id: number }>(
    "post",
    "/api/admin/coupons/templates",
    { data }
  );
};

export const updateTemplate = (id: number, data: TemplateFormData) => {
  return http.request<void>("put", `/api/admin/coupons/templates/${id}`, {
    data
  });
};

export const delistTemplate = (id: number) => {
  return http.request<void>("delete", `/api/admin/coupons/templates/${id}`);
};

export type GrantCouponsData = {
  templateId: number;
  userIds: Array<number>;
};

export type GrantCouponsResult = {
  succeededUserIds: Array<number>;
  skippedUserIds: Array<number>;
};

export const grantCoupons = (data: GrantCouponsData) => {
  return http.request<GrantCouponsResult>(
    "post",
    "/api/admin/coupons/grants",
    { data }
  );
};
