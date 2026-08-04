import { cdn } from "./cdn";
import vue from "@vitejs/plugin-vue";
import { viteBuildInfo } from "./info";
import svgLoader from "vite-svg-loader";
import Icons from "unplugin-icons/vite";
import type { PluginOption } from "vite";
import vueJsx from "@vitejs/plugin-vue-jsx";
import tailwindcss from "@tailwindcss/vite";
import { configCompressPlugin } from "./compress";
import removeNoMatch from "vite-plugin-router-warn";
import { visualizer } from "rollup-plugin-visualizer";
import removeConsole from "vite-plugin-remove-console";
import { codeInspectorPlugin } from "code-inspector-plugin";
import { vitePluginFakeServer } from "vite-plugin-fake-server";

export function getPluginsList(
  VITE_CDN: boolean,
  VITE_COMPRESSION: ViteCompression
): PluginOption[] {
  const lifecycle = process.env.npm_lifecycle_event;
  return [
    tailwindcss(),
    vue(),
    // jsx、tsx語法支援
    vueJsx(),
    /**
     * 在頁面上按住組合鍵時，滑鼠在頁面移動即會在 DOM 上出現遮罩層並顯示相關資訊，點選一下將自動開啟 IDE 並將游標定位到元素對應的程式碼位置
     * Mac 預設組合鍵 Option + Shift
     * Windows 預設組合鍵 Alt + Shift
     * 更多用法看 https://inspector.fe-dev.cn/guide/start.html
     */
    codeInspectorPlugin({
      bundler: "vite",
      hideConsole: true
    }),
    viteBuildInfo(),
    /**
     * 開發環境下移除非必要的vue-router動態路由警告No match found for location with path
     * 非必要具體看 https://github.com/vuejs/router/issues/521 和 https://github.com/vuejs/router/issues/359
     * vite-plugin-router-warn只在開發環境下啟用，只處理vue-router檔案並且只在服務啟動或重啟時執行一次，效能消耗可忽略不計
     */
    removeNoMatch(),
    // mock支援
    vitePluginFakeServer({
      logger: false,
      include: "mock",
      infixName: false,
      enableProd: true
    }),
    // svg元件化支援
    svgLoader(),
    // 自動按需載入圖示
    Icons({
      compiler: "vue3",
      scale: 1
    }),
    VITE_CDN ? cdn : null,
    configCompressPlugin(VITE_COMPRESSION),
    // 線上環境刪除console
    removeConsole({ external: ["src/assets/iconfont/iconfont.js"] }),
    // 打包分析
    lifecycle === "report"
      ? visualizer({ open: true, brotliSize: true, filename: "report.html" })
      : (null as any)
  ];
}
