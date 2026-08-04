import { http } from "@/utils/http";

/** 對齊 tengan-admin AdminMenuController 的回應形狀（管理視角完整選單樹，不做權限篩選）。 */
export type MenuTreeItem = {
  id: number;
  parentId: number;
  menuType: number; // 1=CATALOG 2=MENU 3=BUTTON
  title: string;
  path?: string;
  component?: string;
  routeName?: string;
  icon?: string;
  permissionCode?: string;
  sortOrder: number;
  status: number;
  children?: Array<MenuTreeItem>;
};

export type MenuListResult = {
  items: Array<MenuTreeItem>;
};

/** 選單樹（資料量小，不分頁），選單授權對話框的 el-tree 資料來源。 */
export const getMenuList = () => {
  return http.request<MenuListResult>("get", "/api/admin/system/menus");
};

export type MenuFormData = {
  parentId: number | null;
  menuType: number;
  title: string;
  path?: string;
  component?: string;
  routeName?: string;
  icon?: string;
  permissionCode?: string;
  sortOrder: number;
};

export const createMenu = (data: MenuFormData) => {
  return http.request<{ id: number }>("post", "/api/admin/system/menus", {
    data
  });
};

/** menuType/parentId 建立後不可變更（見 domain Menu 聚合根設計），編輯只送其餘欄位。 */
export const updateMenu = (
  id: number,
  data: Omit<MenuFormData, "parentId" | "menuType">
) => {
  return http.request<void>("put", `/api/admin/system/menus/${id}`, {
    data
  });
};

/** 底下還有子節點時後端會回 409（MenuHasChildrenException），呼叫端要接住錯誤訊息。 */
export const deleteMenu = (id: number) => {
  return http.request<void>("delete", `/api/admin/system/menus/${id}`);
};
