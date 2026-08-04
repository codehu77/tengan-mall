import { useEventListener } from "@vueuse/core";

/** 是否為`img`標籤 */
function isImgElement(element) {
  return typeof HTMLImageElement !== "undefined"
    ? element instanceof HTMLImageElement
    : element.tagName.toLowerCase() === "img";
}

// 在 src/main.ts 引入並呼叫即可 import { addPreventDefault } from "@/utils/preventDefault"; addPreventDefault();
export const addPreventDefault = () => {
  // 阻止通過鍵盤F12快捷鍵開啟瀏覽器開發者工具面板
  useEventListener(
    window.document,
    "keydown",
    ev => ev.key === "F12" && ev.preventDefault()
  );
  // 阻止瀏覽器預設的右鍵選單彈出（不會影響自定義右鍵事件）
  useEventListener(window.document, "contextmenu", ev => ev.preventDefault());
  // 阻止頁面元素選中
  useEventListener(window.document, "selectstart", ev => ev.preventDefault());
  // 瀏覽器中圖片通常預設是可拖動的，並且可以在新標籤頁或視窗中開啟，或者將其拖動到其他應用程式中，此處將其停用，使其預設不可拖動
  useEventListener(
    window.document,
    "dragstart",
    ev => isImgElement(ev?.target) && ev.preventDefault()
  );
};
