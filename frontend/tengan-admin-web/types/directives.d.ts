import type { Directive } from "vue";
import type { CopyEl, OptimizeOptions, RippleOptions } from "@/directives";

declare module "vue" {
  export interface ComponentCustomProperties {
    /** `Loading` 動畫載入指令，具體看：https://element-plus.org/zh-CN/component/loading.html#%E6%8C%87%E4%BB%A4 */
    vLoading: Directive<Element, boolean>;
    /** 按鈕許可權指令（根據路由`meta`中的`auths`欄位進行判斷）*/
    vAuth: Directive<HTMLElement, string | Array<string>>;
    /** 文本複製指令（預設雙擊複製） */
    vCopy: Directive<CopyEl, string>;
    /** 長按指令 */
    vLongpress: Directive<HTMLElement, Function>;
    /** 防抖、節流指令 */
    vOptimize: Directive<HTMLElement, OptimizeOptions>;
    /** 按鈕許可權指令（根據登入介面返回的`permissions`欄位進行判斷）*/
    vPerms: Directive<HTMLElement, string | Array<string>>;
    /**
     * `v-ripple`指令，用法如下：
     * 1. `v-ripple`代表啟用基本的`ripple`功能
     * 2. `v-ripple="{ class: 'text-red' }"`代表自定義`ripple`顏色，支援`tailwindcss`，生效樣式是`color`
     * 3. `v-ripple.center`代表從中心擴散
     */
    vRipple: Directive<HTMLElement, RippleOptions>;
  }
}

export {};
