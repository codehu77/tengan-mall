import { Plugin as importToCDN } from "vite-plugin-cdn-import";

/**
 * @description 打包時採用`cdn`模式，僅限外網使用（預設不採用，如果需要採用cdn模式，請在 .env.production 檔案，將 VITE_CDN 設定成true）
 * 平臺採用國內cdn：https://www.bootcdn.cn，當然你也可以選擇 https://unpkg.com 或者 https://www.jsdelivr.com
 * 注意：上面提到的僅限外網使用也不是完全肯定的，如果你們公司內網部署的有相關js、css檔案，也可以將下面配置對應改一下，整一套內網版cdn
 */
export const cdn = importToCDN({
  //（prodUrl解釋： name: 對應下面modules的name，version: 自動讀取本地package.json中dependencies依賴中對應包的版本號，path: 對應下面modules的path，當然也可寫完整路徑，會替換prodUrl）
  prodUrl: "https://cdn.bootcdn.net/ajax/libs/{name}/{version}/{path}",
  modules: [
    {
      name: "vue",
      var: "Vue",
      path: "vue.global.prod.min.js"
    },
    {
      name: "vue-router",
      var: "VueRouter",
      path: "vue-router.global.min.js"
    },
    // 專案中沒有直接安裝vue-demi，但是pinia用到了，所以需要在引入pinia前引入vue-demi（https://github.com/vuejs/pinia/blob/v2/packages/pinia/package.json#L77）
    {
      name: "vue-demi",
      var: "VueDemi",
      path: "index.iife.min.js"
    },
    {
      name: "pinia",
      var: "Pinia",
      path: "pinia.iife.min.js"
    },
    {
      name: "element-plus",
      var: "ElementPlus",
      path: "index.full.min.js",
      css: "index.min.css"
    },
    {
      name: "axios",
      var: "axios",
      path: "axios.min.js"
    },
    {
      name: "dayjs",
      var: "dayjs",
      path: "dayjs.min.js"
    },
    {
      name: "echarts",
      var: "echarts",
      path: "echarts.min.js"
    }
  ]
});
