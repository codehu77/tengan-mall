import type { VNode, Component } from "vue";
import type { iconType } from "@/components/ReIcon/src/types.ts";

export interface OptionsType {
  /** 文字 */
  label?: string | (() => VNode | Component);
  /**
   * @description 圖示，採用平臺內建的 `useRenderIcon` 函式渲染
   * @see {@link 用法參考 https://pure-admin.cn/pages/icon/#%E9%80%9A%E7%94%A8%E5%9B%BE%E6%A0%87-userendericon-hooks }
   */
  icon?: string | Component;
  /** 圖示屬性、樣式配置 */
  iconAttrs?: iconType;
  /** 值 */
  value?: any;
  /** 是否停用 */
  disabled?: boolean;
  /** `tooltip` 提示 */
  tip?: string;
}
