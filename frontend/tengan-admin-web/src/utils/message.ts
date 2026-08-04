import type { VNode } from "vue";
import { isFunction } from "@pureadmin/utils";
import { type MessageHandler, ElMessage } from "element-plus";

type messageStyle = "el" | "antd";
type messageTypes = "info" | "success" | "warning" | "error";

interface MessageParams {
  /** 訊息型別，可選 `info` 、`success` 、`warning` 、`error` ，預設 `info` */
  type?: messageTypes;
  /** 是否純色，預設 `false` */
  plain?: boolean;
  /** 自定義圖示，該屬性會覆蓋 `type` 的圖示 */
  icon?: any;
  /** 是否將 `message` 屬性作為 `HTML` 片段處理，預設 `false` */
  dangerouslyUseHTMLString?: boolean;
  /** 訊息風格，可選 `el` 、`antd` ，預設 `antd` */
  customClass?: messageStyle;
  /** 顯示時間，單位為毫秒。設為 `0` 則不會自動關閉，`element-plus` 預設是 `3000` ，平臺改成預設 `2000` */
  duration?: number;
  /** 是否顯示關閉按鈕，預設值 `false` */
  showClose?: boolean;
  /** `Message` 距離視窗頂部的偏移量，預設 `16` */
  offset?: number;
  /** 設定元件的根元素，預設 `document.body` */
  appendTo?: string | HTMLElement;
  /** 合併內容相同的訊息，不支援 `VNode` 型別的訊息，預設值 `false` */
  grouping?: boolean;
  /** 重複次數，類似於 `Badge` 。當和 `grouping` 屬性一起使用時作為初始數量使用，預設值 `1` */
  repeatNum?: number;
  /** 關閉時的回撥函式, 引數為被關閉的 `message` 例項 */
  onClose?: Function | null;
}

/** 用法非常簡單，參考 src/views/components/message/index.vue 檔案 */

/**
 * `Message` 訊息提示函式
 */
const message = (
  message: string | VNode | (() => VNode),
  params?: MessageParams
): MessageHandler => {
  if (!params) {
    return ElMessage({
      message,
      customClass: "pure-message"
    });
  } else {
    const {
      icon,
      type = "info",
      plain = false,
      dangerouslyUseHTMLString = false,
      customClass = "antd",
      duration = 2000,
      showClose = false,
      offset = 16,
      appendTo = document.body,
      grouping = false,
      repeatNum = 1,
      onClose
    } = params;

    return ElMessage({
      message,
      icon,
      type,
      plain,
      dangerouslyUseHTMLString,
      duration,
      showClose,
      offset,
      appendTo,
      grouping,
      repeatNum,
      // 全域性搜 pure-message 即可知道該類的樣式位置
      customClass: customClass === "antd" ? "pure-message" : "",
      onClose: () => (isFunction(onClose) ? onClose() : null)
    });
  }
};

/**
 * 關閉所有 `Message` 訊息提示函式
 */
const closeAllMessage = (): void => ElMessage.closeAll();

export { message, closeAllMessage };
