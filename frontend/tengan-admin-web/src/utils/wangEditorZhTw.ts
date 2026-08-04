import { i18nAddResources, i18nChangeLanguage } from "@wangeditor/editor";

/**
 * wangEditor 內建 i18n 只有 'en'/'zh-CN'（簡體）兩組，沒有繁體中文——套件本身的
 * locale 原始碼分散在 @wangeditor/editor、basic-modules、list-module、table-module、
 * upload-image-module、video-module、code-highlight 好幾個子套件裡，各自維護一份
 * 'zh-CN' 字典。這裡把每個子套件的字典整份翻成繁體中文，用 i18nAddResources 覆蓋掉
 * 'zh-CN' 這個 locale 底下的字串（不新開 'zh-TW' locale——新開一個 locale 意味著
 * 沒被覆蓋到的 key 會顯示原始英文/找不到翻譯，而不是自動 fallback 回簡體），
 * 直接覆蓋既有 'zh-CN' 最簡單可靠。
 */
export function applyWangEditorTraditionalChinese() {
  i18nAddResources("zh-CN", {
    editor: {
      more: "更多",
      justify: "對齊",
      indent: "縮排",
      image: "圖片",
      video: "影片"
    },
    common: {
      ok: "確定",
      delete: "刪除",
      enter: "Enter"
    },
    blockQuote: {
      title: "引用"
    },
    codeBlock: {
      title: "程式碼區塊"
    },
    color: {
      color: "文字顏色",
      bgColor: "背景色",
      default: "預設顏色",
      clear: "清除背景色"
    },
    divider: {
      title: "分隔線"
    },
    emotion: {
      title: "表情"
    },
    fontSize: {
      title: "字型大小",
      default: "預設大小"
    },
    fontFamily: {
      title: "字型",
      default: "預設字型"
    },
    fullScreen: {
      title: "全螢幕"
    },
    header: {
      title: "標題",
      text: "內文"
    },
    image: {
      netImage: "網路圖片",
      delete: "刪除圖片",
      edit: "編輯圖片",
      viewLink: "檢視連結",
      src: "圖片網址",
      desc: "圖片描述",
      link: "圖片連結"
    },
    indent: {
      decrease: "減少縮排",
      increase: "增加縮排"
    },
    justify: {
      left: "靠左對齊",
      right: "靠右對齊",
      center: "置中對齊",
      justify: "左右對齊"
    },
    lineHeight: {
      title: "行高",
      default: "預設行高"
    },
    link: {
      insert: "插入連結",
      text: "連結文字",
      url: "連結網址",
      unLink: "取消連結",
      edit: "修改連結",
      view: "檢視連結"
    },
    textStyle: {
      bold: "粗體",
      clear: "清除格式",
      code: "行內程式碼",
      italic: "斜體",
      sub: "下標",
      sup: "上標",
      through: "刪除線",
      underline: "底線"
    },
    undo: {
      undo: "復原",
      redo: "重做"
    },
    todo: {
      todo: "待辦"
    },
    highLightModule: {
      selectLang: "選擇語言"
    },
    listModule: {
      unOrderedList: "項目符號清單",
      orderedList: "編號清單"
    },
    tableModule: {
      deleteCol: "刪除欄",
      deleteRow: "刪除列",
      deleteTable: "刪除表格",
      widthAuto: "寬度自適應",
      insertCol: "插入欄",
      insertRow: "插入列",
      insertTable: "插入表格",
      header: "表頭"
    },
    uploadImgModule: {
      uploadImage: "上傳圖片",
      uploadError: "{{fileName}} 上傳失敗"
    },
    videoModule: {
      delete: "刪除影片",
      uploadVideo: "上傳影片",
      insertVideo: "插入影片",
      videoSrc: "影片網址",
      videoSrcPlaceHolder: "影片檔案 URL 或第三方 <iframe>",
      videoPoster: "影片封面",
      videoPosterPlaceHolder: "封面圖片 URL",
      ok: "確定",
      editSize: "修改尺寸",
      width: "寬度",
      height: "高度"
    }
  });
  i18nChangeLanguage("zh-CN");
}
