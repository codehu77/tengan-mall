// 響應式storage
import type { App } from "vue";
import Storage from "responsive-storage";
import { routerArrays } from "@/layout/types";
import { responsiveStorageNameSpace } from "@/config";

export const injectResponsiveStorage = (app: App, config: PlatformConfigs) => {
  const nameSpace = responsiveStorageNameSpace();
  const configObj = Object.assign(
    {
      // layout模式以及主題
      layout: Storage.getData("layout", nameSpace) ?? {
        layout: config.Layout ?? "vertical",
        theme: config.Theme ?? "light",
        darkMode: config.DarkMode ?? false,
        sidebarStatus: config.SidebarStatus ?? true,
        epThemeColor: config.EpThemeColor ?? "#409EFF",
        themeColor: config.Theme ?? "light", // 主題色（對應系統配置中的主題色，與theme不同的是它不會受到淺色、深色整體風格切換的影響，只會在手動點選主題色時改變）
        overallStyle: config.OverallStyle ?? "light" // 整體風格（淺色：light、深色：dark、自動：system）
      },
      // 系統配置-介面顯示
      configure: Storage.getData("configure", nameSpace) ?? {
        grey: config.Grey ?? false,
        weak: config.Weak ?? false,
        hideTabs: config.HideTabs ?? false,
        hideFooter: config.HideFooter ?? true,
        showLogo: config.ShowLogo ?? true,
        showModel: config.ShowModel ?? "smart",
        multiTagsCache: config.MultiTagsCache ?? false,
        stretch: config.Stretch ?? false
      }
    },
    config.MultiTagsCache
      ? {
          // 預設顯示頂級選單tag
          tags: Storage.getData("tags", nameSpace) ?? routerArrays
        }
      : {}
  );

  app.use(Storage, { nameSpace, memory: configObj });
};
