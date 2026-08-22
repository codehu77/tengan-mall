import { http } from "@/utils/http";

/** 對齊 tengan-admin SeckillSessionController 的回應形狀（BFF 轉發 tengan-seckill）。 */
export type SessionItem = {
  id: number;
  name: string;
  timeOfDay: string; // "HH:mm:ss"
  durationMinutes: number;
  sortOrder: number;
  enabled: boolean;
};

export type SessionListResult = {
  sessions: Array<SessionItem>;
};

export const getSessionList = () => {
  return http.request<SessionListResult>("get", "/api/admin/seckill/sessions");
};

export type SessionInput = {
  name: string;
  timeOfDay: string;
  durationMinutes: number;
  sortOrder: number;
  enabled: boolean;
};

export const createSession = (data: SessionInput) => {
  return http.request<{ id: number }>("post", "/api/admin/seckill/sessions", {
    data
  });
};

export const updateSession = (id: number, data: SessionInput) => {
  return http.request<void>("put", `/api/admin/seckill/sessions/${id}`, {
    data
  });
};

export const deleteSession = (id: number) => {
  return http.request<void>("delete", `/api/admin/seckill/sessions/${id}`);
};
