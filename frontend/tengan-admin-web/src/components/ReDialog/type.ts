import type { CSSProperties, VNode, Component } from "vue";

type DoneFn = (cancel?: boolean) => void;
type EventType =
  | "open"
  | "close"
  | "openAutoFocus"
  | "closeAutoFocus"
  | "fullscreenCallBack";
type ArgsType = {
  /** `cancel` 點選取消按鈕、`sure` 點選確定按鈕、`close` 點選右上角關閉按鈕或空白頁或按下了esc鍵 */
  command: "cancel" | "sure" | "close";
};
type ButtonType =
  | "primary"
  | "success"
  | "warning"
  | "danger"
  | "info"
  | "text";

/** https://element-plus.org/zh-CN/component/dialog.html#attributes */
type DialogProps = {
  /** `Dialog` 的顯示與隱藏 */
  visible?: boolean;
  /** `Dialog` 的標題 */
  title?: string;
  /** `Dialog` 的寬度，預設 `50%` */
  width?: string | number;
  /** 是否為全屏 `Dialog`（會一直處於全屏狀態，除非彈框關閉），預設 `false`，`fullscreen` 和 `fullscreenIcon` 都傳時只有 `fullscreen` 會生效 */
  fullscreen?: boolean;
  /** 是否顯示全屏操作圖示，預設 `false`，`fullscreen` 和 `fullscreenIcon` 都傳時只有 `fullscreen` 會生效 */
  fullscreenIcon?: boolean;
  /** `Dialog CSS` 中的 `margin-top` 值，預設 `15vh` */
  top?: string;
  /** 是否需要遮罩層，預設 `true` */
  modal?: boolean;
  /** `Dialog` 自身是否插入至 `body` 元素上。巢狀的 `Dialog` 必須指定該屬性並賦值為 `true`，預設 `false` */
  appendToBody?: boolean;
  /** 是否在 `Dialog` 出現時將 `body` 滾動鎖定，預設 `true` */
  lockScroll?: boolean;
  /** `Dialog` 的自定義類名 */
  class?: string;
  /** `Dialog` 的自定義樣式 */
  style?: CSSProperties;
  /** `Dialog` 開啟的延時時間，單位毫秒，預設 `0` */
  openDelay?: number;
  /** `Dialog` 關閉的延時時間，單位毫秒，預設 `0` */
  closeDelay?: number;
  /** 是否可以通過點選 `modal` 關閉 `Dialog`，預設 `true` */
  closeOnClickModal?: boolean;
  /** 是否可以通過按下 `ESC` 關閉 `Dialog`，預設 `true` */
  closeOnPressEscape?: boolean;
  /** 是否顯示關閉按鈕，預設 `true` */
  showClose?: boolean;
  /** 關閉前的回撥，會暫停 `Dialog` 的關閉. 回撥函式內執行 `done` 引數方法的時候才是真正關閉對話方塊的時候 */
  beforeClose?: (done: DoneFn) => void;
  /** 為 `Dialog` 啟用可拖拽功能，預設 `false` */
  draggable?: boolean;
  /** 是否讓 `Dialog` 的 `header` 和 `footer` 部分居中排列，預設 `false` */
  center?: boolean;
  /** 是否水平垂直對齊對話方塊，預設 `false` */
  alignCenter?: boolean;
  /** 當關閉 `Dialog` 時，銷燬其中的元素，預設 `false` */
  destroyOnClose?: boolean;
};

//element-plus.org/zh-CN/component/popconfirm.html#attributes
type Popconfirm = {
  /** 標題 */
  title?: string;
  /** 確定按鈕文字 */
  confirmButtonText?: string;
  /** 取消按鈕文字 */
  cancelButtonText?: string;
  /** 確定按鈕型別，預設 `primary` */
  confirmButtonType?: ButtonType;
  /** 取消按鈕型別，預設 `text` */
  cancelButtonType?: ButtonType;
  /** 自定義圖示，預設 `QuestionFilled` */
  icon?: string | Component;
  /** `Icon` 顏色，預設 `#f90` */
  iconColor?: string;
  /** 是否隱藏 `Icon`，預設 `false` */
  hideIcon?: boolean;
  /** 關閉時的延遲，預設 `200` */
  hideAfter?: number;
  /** 是否將 `popover` 的下拉式清單插入至 `body` 元素，預設 `true` */
  teleported?: boolean;
  /** 當 `popover` 元件長時間不觸發且 `persistent` 屬性設定為 `false` 時, `popover` 將會被刪除，預設 `false` */
  persistent?: boolean;
  /** 彈層寬度，最小寬度 `150px`，預設 `150` */
  width?: string | number;
};

type BtnClickDialog = {
  options?: DialogOptions;
  index?: number;
};
type BtnClickButton = {
  btn?: ButtonProps;
  index?: number;
};
/** https://element-plus.org/zh-CN/component/button.html#button-attributes */
type ButtonProps = {
  /** 按鈕文字 */
  label: string;
  /** 按鈕尺寸 */
  size?: "large" | "default" | "small";
  /** 按鈕型別 */
  type?: "primary" | "success" | "warning" | "danger" | "info";
  /** 是否為樸素按鈕，預設 `false` */
  plain?: boolean;
  /** 是否為文字按鈕，預設 `false` */
  text?: boolean;
  /** 是否顯示文字按鈕背景顏色，預設 `false` */
  bg?: boolean;
  /** 是否為連結按鈕，預設 `false` */
  link?: boolean;
  /** 是否為圓角按鈕，預設 `false` */
  round?: boolean;
  /** 是否為圓形按鈕，預設 `false` */
  circle?: boolean;
  /** 確定按鈕的 `Popconfirm` 氣泡確認框相關配置 */
  popconfirm?: Popconfirm;
  /** 是否為載入中狀態，預設 `false` */
  loading?: boolean;
  /** 自定義載入中狀態圖示元件 */
  loadingIcon?: string | Component;
  /** 按鈕是否為停用狀態，預設 `false` */
  disabled?: boolean;
  /** 圖示元件 */
  icon?: string | Component;
  /** 是否開啟原生 `autofocus` 屬性，預設 `false` */
  autofocus?: boolean;
  /** 原生 `type` 屬性，預設 `button` */
  nativeType?: "button" | "submit" | "reset";
  /** 自動在兩個中文字元之間插入空格 */
  autoInsertSpace?: boolean;
  /** 自定義按鈕顏色, 並自動計算 `hover` 和 `active` 觸發後的顏色 */
  color?: string;
  /** `dark` 模式, 意味著自動設定 `color` 為 `dark` 模式的顏色，預設 `false` */
  dark?: boolean;
  /** 自定義元素標籤 */
  tag?: string | Component;
  /** 點選按鈕後觸發的回撥 */
  btnClick?: ({
    dialog,
    button
  }: {
    /** 當前 `Dialog` 資訊 */
    dialog: BtnClickDialog;
    /** 當前 `button` 資訊 */
    button: BtnClickButton;
  }) => void;
};

interface DialogOptions extends DialogProps {
  /** 內容區元件的 `props`，可通過 `defineProps` 接收 */
  props?: any;
  /** 是否隱藏 `Dialog` 按鈕操作區的內容 */
  hideFooter?: boolean;
  /** 確定按鈕的 `Popconfirm` 氣泡確認框相關配置 */
  popconfirm?: Popconfirm;
  /** 點選確定按鈕後是否開啟 `loading` 載入動畫 */
  sureBtnLoading?: boolean;
  /**
   * @description 自定義對話方塊標題的內容渲染器
   * @see {@link https://element-plus.org/zh-CN/component/dialog.html#%E8%87%AA%E5%AE%9A%E4%B9%89%E5%A4%B4%E9%83%A8}
   */
  headerRenderer?: ({
    close,
    titleId,
    titleClass
  }: {
    close: Function;
    titleId: string;
    titleClass: string;
  }) => VNode | Component;
  /** 自定義內容渲染器 */
  contentRenderer?: ({
    options,
    index
  }: {
    options: DialogOptions;
    index: number;
  }) => VNode | Component;
  /** 自定義按鈕操作區的內容渲染器，會覆蓋`footerButtons`以及預設的 `取消` 和 `確定` 按鈕 */
  footerRenderer?: ({
    options,
    index
  }: {
    options: DialogOptions;
    index: number;
  }) => VNode | Component;
  /** 自定義底部按鈕操作 */
  footerButtons?: Array<ButtonProps>;
  /** `Dialog` 開啟後的回撥 */
  open?: ({
    options,
    index
  }: {
    options: DialogOptions;
    index: number;
  }) => void;
  /** `Dialog` 關閉後的回撥（只有點選右上角關閉按鈕或空白頁或按下了esc鍵關閉頁面時才會觸發） */
  close?: ({
    options,
    index
  }: {
    options: DialogOptions;
    index: number;
  }) => void;
  /** `Dialog` 關閉後的回撥。 `args` 返回的 `command` 值解析：`cancel` 點選取消按鈕、`sure` 點選確定按鈕、`close` 點選右上角關閉按鈕或空白頁或按下了esc鍵  */
  closeCallBack?: ({
    options,
    index,
    args
  }: {
    options: DialogOptions;
    index: number;
    args: any;
  }) => void;
  /** 點選全屏按鈕時的回撥 */
  fullscreenCallBack?: ({
    options,
    index
  }: {
    options: DialogOptions;
    index: number;
  }) => void;
  /** 輸入焦點聚焦在 `Dialog` 內容時的回撥 */
  openAutoFocus?: ({
    options,
    index
  }: {
    options: DialogOptions;
    index: number;
  }) => void;
  /** 輸入焦點從 `Dialog` 內容失焦時的回撥 */
  closeAutoFocus?: ({
    options,
    index
  }: {
    options: DialogOptions;
    index: number;
  }) => void;
  /** 點選底部取消按鈕的回撥，會暫停 `Dialog` 的關閉. 回撥函式內執行 `done` 引數方法的時候才是真正關閉對話方塊的時候 */
  beforeCancel?: (
    done: Function,
    {
      options,
      index
    }: {
      options: DialogOptions;
      index: number;
    }
  ) => void;
  /** 點選底部確定按鈕的回撥，會暫停 `Dialog` 的關閉. 回撥函式內執行 `done` 引數方法的時候才是真正關閉對話方塊的時候 */
  beforeSure?: (
    done: Function,
    {
      options,
      index,
      closeLoading
    }: {
      options: DialogOptions;
      index: number;
      /** 關閉確定按鈕的 `loading` 載入動畫 */
      closeLoading: Function;
    }
  ) => void;
}

export type { EventType, ArgsType, DialogProps, ButtonProps, DialogOptions };
