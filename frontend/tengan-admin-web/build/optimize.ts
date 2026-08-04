/**
 * 此檔案作用於 `vite.config.ts` 的 `optimizeDeps.include` 依賴預構建配置項
 * 依賴預構建，`vite` 啟動時會將下面 include 裡的模組，編譯成 esm 格式並快取到 node_modules/.vite 資料夾，頁面載入到對應模組時如果瀏覽器有快取就讀取瀏覽器快取，如果沒有會讀取本地快取並按需載入
 * 尤其當您停用瀏覽器快取時（這種情況只應該發生在除錯階段）必須將對應模組加入到 include裡，否則會遇到開發環境切換頁面卡頓的問題（vite 會認為它是一個新的依賴包會重新載入並強制重新整理頁面），因為它既無法使用瀏覽器快取，又沒有在本地 node_modules/.vite 裡快取
 * 溫馨提示：如果您使用的第三方庫是全域性引入，也就是引入到 src/main.ts 檔案裡，就不需要再新增到 include 裡了，因為 vite 會自動將它們快取到 node_modules/.vite
 */
const include = [
  "qs",
  "mitt",
  "dayjs",
  "axios",
  "pinia",
  "vue-types",
  "js-cookie",
  "vue-tippy",
  "pinyin-pro",
  "sortablejs",
  "@vueuse/core",
  "@pureadmin/utils",
  "responsive-storage"
];

/**
 * 在預構建中強制排除的依賴項
 * 溫馨提示：平臺推薦的使用方式是哪裡需要哪裡引入而且都是單個的引入，不需要預構建，直接讓瀏覽器載入就好
 */
const exclude = ["@iconify/json"];

export { include, exclude };
