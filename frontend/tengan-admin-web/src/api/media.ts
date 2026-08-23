import { http } from "@/utils/http";

/** 對齊 tengan-admin BannerController 的回應形狀（BFF 轉發 tengan-media）。 */
export type BannerItem = {
  id: number;
  imageUrl: string;
  linkUrl: string | null;
  title: string | null;
  sortOrder: number;
  enabled: boolean;
};

export type BannerListResult = {
  banners: Array<BannerItem>;
};

export const getBannerList = () => {
  return http.request<BannerListResult>("get", "/api/admin/media/banners");
};

export type BannerInput = {
  imageUrl: string;
  linkUrl: string;
  title: string;
  sortOrder: number;
  enabled: boolean;
};

export const createBanner = (data: BannerInput) => {
  return http.request<{ id: number }>("post", "/api/admin/media/banners", {
    data
  });
};

export const updateBanner = (id: number, data: BannerInput) => {
  return http.request<void>("put", `/api/admin/media/banners/${id}`, {
    data
  });
};

export const deleteBanner = (id: number) => {
  return http.request<void>("delete", `/api/admin/media/banners/${id}`);
};

/**
 * category 限 "banner"（輪播圖）/"product"（商品圖），跟顧客頭像上傳分開命名空間。
 *
 * `headers` 這裡刻意覆寫掉——這個專案的 axios 實例（utils/http/index.ts 的 defaultConfig）
 * 全域預設 `Content-Type: application/json`，axios 內建的 transformRequest 只要偵測到目前
 * Content-Type 像 JSON、且 body 是 FormData，就會把它 JSON.stringify 後當成一般 JSON 送出
 * （axios 原本是為了相容「HTML form 也可能被誤判成 FormData」這個情境才這樣寫），導致真正的
 * multipart 上傳被吃掉，後端收到的請求 Content-Type 其實是 application/json，Spring 報
 * 「Current request is not a multipart request」。這裡只要把 Content-Type 蓋成非 JSON 值，
 * axios 就不會誤判，之後 xhr adapter 偵測到 body 真的是 FormData 時會自動把這個值換成瀏覽器
 * 產生的正確 `multipart/form-data; boundary=...`。
 */
export const uploadImage = (file: File, category: "banner" | "product") => {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("category", category);
  return http.request<{ url: string }>("post", "/api/admin/media/images", {
    data: formData,
    headers: { "Content-Type": "multipart/form-data" }
  });
};
