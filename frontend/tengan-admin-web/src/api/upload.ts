import { http } from "@/utils/http";

export type UploadFileResult = {
  /** 上傳成功後可直接用來顯示圖片的網址 */
  url: string;
};

/** 上傳圖片檔案（目前僅供頭像使用），走本機磁碟儲存，見後端 FileUploadController。 */
export const uploadFile = (file: File) => {
  const formData = new FormData();
  formData.append("file", file);
  return http.request<UploadFileResult>("post", "/api/admin/files/upload", {
    data: formData,
    headers: { "Content-Type": "multipart/form-data" }
  });
};
