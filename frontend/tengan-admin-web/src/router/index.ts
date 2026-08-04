// import "@/utils/sso";
import Cookies from "js-cookie";
import { getConfig } from "@/config";
import NProgress from "@/utils/progress";
import { buildHierarchyTree } from "@/utils/tree";
import remainingRouter from "./modules/remaining";
import { useMultiTagsStoreHook } from "@/store/modules/multiTags";
import { usePermissionStoreHook } from "@/store/modules/permission";
import {
  isUrl,
  openLink,
  cloneDeep,
  isAllEmpty,
  storageLocal
} from "@pureadmin/utils";
import {
  ascending,
  getTopMenu,
  initRouter,
  isOneOfArray,
  getHistoryMode,
  findRouteByPath,
  handleAliveRoute,
  formatTwoStageRoutes,
  formatFlatteningRoutes
} from "./utils";
import {
  type Router,
  type RouteRecordRaw,
  type RouteComponent,
  createRouter
} from "vue-router";
import {
  type DataInfo,
  userKey,
  removeToken,
  multipleTabsKey
} from "@/utils/auth";

/** 自動匯入全部靜態路由，無需再手動引入！匹配 src/router/modules 目錄（任何巢狀級別）中具有 .ts 副檔名的所有檔案，除了 remaining.ts 檔案
 * 如何匹配所有檔案請看：https://github.com/mrmlnc/fast-glob#basic-syntax
 * 如何排除檔案請看：https://cn.vitejs.dev/guide/features.html#negative-patterns
 */
const modules: Record<string, any> = import.meta.glob(
  ["./modules/**/*.ts", "!./modules/**/remaining.ts"],
  {
    eager: true
  }
);

/** 原始靜態路由（未做任何處理） */
const routes = [];

Object.keys(modules).forEach(key => {
  routes.push(modules[key].default);
});

/** 匯出處理後的靜態路由（三級及以上的路由全部拍成二級） */
export const constantRoutes: Array<RouteRecordRaw> = formatTwoStageRoutes(
  formatFlatteningRoutes(buildHierarchyTree(ascending(routes.flat(Infinity))))
);

/** 初始的靜態路由，用於退出登入時重置路由 */
const initConstantRoutes: Array<RouteRecordRaw> = cloneDeep(constantRoutes);

/** 用於渲染選單，保持原始層級 */
export const constantMenus: Array<RouteComponent> = ascending(
  routes.flat(Infinity)
).concat(...remainingRouter);

/** 不參與選單的路由 */
export const remainingPaths = Object.keys(remainingRouter).map(v => {
  return remainingRouter[v].path;
});

/** 建立路由例項 */
export const router: Router = createRouter({
  history: getHistoryMode(import.meta.env.VITE_ROUTER_HISTORY),
  routes: constantRoutes.concat(...(remainingRouter as any)),
  strict: true,
  scrollBehavior(to, from, savedPosition) {
    return new Promise(resolve => {
      if (savedPosition) {
        return savedPosition;
      } else {
        if (from.meta.saveSrollTop) {
          const top: number =
            document.documentElement.scrollTop || document.body.scrollTop;
          resolve({ left: 0, top });
        }
      }
    });
  }
});

/** 記錄已經載入的頁面路徑 */
const loadedPaths = new Set<string>();

/** 重置已載入頁面記錄 */
export function resetLoadedPaths() {
  loadedPaths.clear();
}

/** 重置路由 */
export function resetRouter() {
  router.clearRoutes();
  for (const route of initConstantRoutes.concat(...(remainingRouter as any))) {
    router.addRoute(route);
  }
  router.options.routes = formatTwoStageRoutes(
    formatFlatteningRoutes(buildHierarchyTree(ascending(routes.flat(Infinity))))
  );
  usePermissionStoreHook().clearAllCachePage();
  resetLoadedPaths();
}

/** 路由白名單 */
const whiteList = ["/login"];

const { VITE_HIDE_HOME } = import.meta.env;

router.beforeEach((to: ToRouteType, _from, next) => {
  to.meta.loaded = loadedPaths.has(to.path);

  if (!to.meta.loaded) {
    NProgress.start();
  }

  if (to.meta?.keepAlive) {
    handleAliveRoute(to, "add");
    // 頁面整體重新整理和點選標籤頁重新整理
    if (_from.name === undefined || _from.name === "Redirect") {
      handleAliveRoute(to);
    }
  }
  const userInfo = storageLocal().getItem<DataInfo<number>>(userKey);
  const externalLink = isUrl(to?.name as string);
  if (!externalLink) {
    to.matched.some(item => {
      if (!item.meta.title) return "";
      const Title = getConfig().Title;
      if (Title) document.title = `${item.meta.title} | ${Title}`;
      else document.title = item.meta.title as string;
    });
  }
  /** 如果已經登入並存在登入資訊後不能跳轉到路由白名單，而是繼續保持在當前頁面 */
  function toCorrectRoute() {
    whiteList.includes(to.fullPath) ? next(_from.fullPath) : next();
  }
  if (Cookies.get(multipleTabsKey) && userInfo) {
    // 無許可權跳轉403頁面
    if (to.meta?.roles && !isOneOfArray(to.meta?.roles, userInfo?.roles)) {
      next({ path: "/error/403" });
    }
    // 開啟隱藏首頁後在瀏覽器位址列手動輸入首頁welcome路由則跳轉到404頁面
    if (VITE_HIDE_HOME === "true" && to.fullPath === "/welcome") {
      next({ path: "/error/404" });
    }
    if (_from?.name) {
      // name為超連結
      if (externalLink) {
        openLink(to?.name as string);
        NProgress.done();
      } else {
        toCorrectRoute();
      }
    } else {
      // 重新整理
      if (
        usePermissionStoreHook().wholeMenus.length === 0 &&
        to.path !== "/login"
      ) {
        initRouter().then((router: Router) => {
          if (!useMultiTagsStoreHook().getMultiTagsCache) {
            const { path } = to;
            const route = findRouteByPath(
              path,
              router.options.routes[0].children
            );
            getTopMenu(true);
            // query、params模式路由傳引數的標籤頁不在此處處理
            if (route && route.meta?.title) {
              if (isAllEmpty(route.parentId) && route.meta?.backstage) {
                // 此處為動態頂級路由（目錄）
                const { path, name, meta } = route.children[0];
                useMultiTagsStoreHook().handleTags("push", {
                  path,
                  name,
                  meta
                });
              } else {
                const { path, name, meta } = route;
                useMultiTagsStoreHook().handleTags("push", {
                  path,
                  name,
                  meta
                });
              }
            }
          }
          // 確保動態路由完全加入路由列表並且不影響靜態路由（注意：動態路由重新整理時router.beforeEach可能會觸發兩次，第一次觸發動態路由還未完全新增，第二次動態路由才完全新增到路由列表，如果需要在router.beforeEach做一些判斷可以在to.name存在的條件下去判斷，這樣就只會觸發一次）
          if (isAllEmpty(to.name)) router.push(to.fullPath);
        });
      }
      toCorrectRoute();
    }
  } else {
    if (to.path !== "/login") {
      if (whiteList.indexOf(to.path) !== -1) {
        next();
      } else {
        removeToken();
        next({ path: "/login" });
      }
    } else {
      next();
    }
  }
});

router.afterEach(to => {
  loadedPaths.add(to.path);
  NProgress.done();
});

export default router;
