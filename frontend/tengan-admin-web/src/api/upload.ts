import { http } from "@/utils/http";

export type UploadFileResult = {
  /** 上傳成功後可直接用來顯示圖片的網址 */
  url: string;
};

/** 上傳圖片檔案（目前僅供管理員自己的頭像使用），透過 tengan-media 存進 MinIO，見後端 FileUploadController。 */
export const uploadFile = (file: File) => {
  const formData = new FormData();
  formData.append("file", file);
  return http.request<UploadFileResult>("post", "/api/admin/files/upload", {
    data: formData,
    headers: { "Content-Type": "multipart/form-data" }
  });
};
