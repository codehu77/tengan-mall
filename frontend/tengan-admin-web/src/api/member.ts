import { http } from "@/utils/http";

/**
 * 對齊 tengan-admin MemberController 的回應形狀。status 不是 tengan-member 自己存的，是
 * tengan-admin 即時向 tengan-auth 問來組裝進來的（見後端 MemberController 的說明），所以
 * 只能顯示、不能拿來當伺服器端篩選條件——沒有依狀態篩選的 query param。
 */
export type MemberItem = {
  id: number;
  username: string;
  phone?: string;
  nickname: string;
  avatarUrl?: string;
  status: number;
};

export type MemberListResult = {
  items: Array<MemberItem>;
  total: number;
};

export type SearchMembersParams = {
  keyword?: string;
  pageNum: number;
  pageSize: number;
};

export const searchMembers = (params: SearchMembersParams) => {
  return http.request<MemberListResult>("get", "/api/admin/members", {
    params
  });
};

export const banMember = (id: number) => {
  return http.request<void>("put", `/api/admin/members/${id}/ban`);
};

export const unbanMember = (id: number) => {
  return http.request<void>("put", `/api/admin/members/${id}/unban`);
};
