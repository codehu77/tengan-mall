// 全域性路由型別宣告

import type { RouteComponent, RouteLocationNormalized } from "vue-router";
import type { FunctionalComponent } from "vue";

declare global {
  interface ToRouteType extends RouteLocationNormalized {
    meta: CustomizeRouteMeta;
  }

  /**
   * @description 完整子路由的`meta`配置表
   */
  interface CustomizeRouteMeta {
    /** 選單名稱（相容國際化、非國際化，如何用國際化的寫法就必須在根目錄的`locales`資料夾下對應新增） `必填` */
    title: string;
    /** 選單圖示 `可選` */
    icon?: string | FunctionalComponent;
    /** 選單名稱右側的額外圖示 */
    extraIcon?: string | FunctionalComponent;
    /** 是否在選單中顯示（預設`true`）`可選` */
    showLink?: boolean;
    /** 是否顯示父級選單 `可選` */
    showParent?: boolean;
    /** 頁面級別許可權設定 `可選` */
    roles?: Array<string>;
    /** 按鈕級別許可權設定 `可選` */
    auths?: Array<string>;
    /** 路由元件快取（開啟 `true`、關閉 `false`）`可選` */
    keepAlive?: boolean;
    /** 內嵌的`iframe`連結 `可選` */
    frameSrc?: string;
    /** `iframe`頁是否開啟首次載入動畫（預設`true`）`可選` */
    frameLoading?: boolean;
    /** 頁面載入動畫（兩種模式，第二種權重更高，第一種直接採用`vue`內建的`transitions`動畫，第二種是使用`animate.css`編寫進、離場動畫，平臺更推薦使用第二種模式，已經內建了`animate.css`，直接寫對應的動畫名即可）`可選` */
    transition?: {
      /**
       * @description 當前路由動畫效果
       * @see {@link https://next.router.vuejs.org/guide/advanced/transitions.html#transitions}
       * @see animate.css {@link https://animate.style}
       */
      name?: string;
      /** 進場動畫 */
      enterTransition?: string;
      /** 離場動畫 */
      leaveTransition?: string;
    };
    /** 當前選單名稱或自定義資訊禁止新增到標籤頁（預設`false`） */
    hiddenTag?: boolean;
    /** 當前選單名稱是否固定顯示在標籤頁且不可關閉（預設`false`） */
    fixedTag?: boolean;
    /** 動態路由可開啟的最大數量 `可選` */
    dynamicLevel?: number;
    /** 將某個選單啟用
     * （主要用於通過`query`或`params`傳參的路由，當它們通過配置`showLink: false`後不在選單中顯示，就不會有任何選單高亮，
     * 而通過設定`activePath`指定啟用選單即可獲得高亮，`activePath`為指定啟用選單的`path`）
     */
    activePath?: string;
    /** 當前頁面是否已經載入過 */
    loaded?: boolean;
  }

  /**
   * @description 完整子路由配置表
   */
  interface RouteChildrenConfigsTable {
    /** 子路由地址 `必填` */
    path: string;
    /** 路由名字（對應不要重複，和當前元件的`name`保持一致）`必填` */
    name?: string;
    /** 路由重定向 `可選` */
    redirect?: string;
    /** 按需載入元件 `可選` */
    component?: RouteComponent;
    meta?: CustomizeRouteMeta;
    /** 子路由配置項 */
    children?: Array<RouteChildrenConfigsTable>;
  }

  /**
   * @description 整體路由配置表（包括完整子路由）
   */
  interface RouteConfigsTable {
    /** 路由地址 `必填` */
    path: string;
    /** 路由名字（保持唯一）`可選` */
    name?: string;
    /** `Layout`元件 `可選` */
    component?: RouteComponent;
    /** 路由重定向 `可選` */
    redirect?: string;
    meta?: {
      /** 選單名稱（相容國際化、非國際化，如何用國際化的寫法就必須在根目錄的`locales`資料夾下對應新增）`必填` */
      title: string;
      /** 選單圖示 `可選` */
      icon?: string | FunctionalComponent;
      /** 是否在選單中顯示（預設`true`）`可選` */
      showLink?: boolean;
      /** 選單升序排序，值越高排的越後（只針對頂級路由）`可選` */
      rank?: number;
    };
    /** 子路由配置項 */
    children?: Array<RouteChildrenConfigsTable>;
  }
}

// https://router.vuejs.org/zh/guide/advanced/meta.html#typescript
declare module "vue-router" {
  // eslint-disable-next-line
  interface RouteMeta extends CustomizeRouteMeta {}
}
