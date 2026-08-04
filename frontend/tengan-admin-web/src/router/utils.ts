import {
  type RouterHistory,
  type RouteRecordRaw,
  type RouteComponent,
  createWebHistory,
  createWebHashHistory
} from "vue-router";
import { router } from "./index";
import { isProxy, toRaw } from "vue";
import { useTimeoutFn } from "@vueuse/core";
import {
  isString,
  cloneDeep,
  isAllEmpty,
  intersection,
  storageLocal,
  isIncludeAllChildren
} from "@pureadmin/utils";
import { getConfig } from "@/config";
import { buildHierarchyTree } from "@/utils/tree";
import { userKey, type DataInfo } from "@/utils/auth";
import { type menuType, routerArrays } from "@/layout/types";
import { useMultiTagsStoreHook } from "@/store/modules/multiTags";
import { usePermissionStoreHook } from "@/store/modules/permission";
const IFrame = () => import("@/layout/frame.vue");
// https://cn.vitejs.dev/guide/features.html#glob-import
const modulesRoutes = import.meta.glob("/src/views/**/*.{vue,tsx}");

// 動態路由
import { getAsyncRoutes } from "@/api/routes";

function handRank(routeInfo: any) {
  const { name, path, parentId, meta } = routeInfo;
  return isAllEmpty(parentId)
    ? isAllEmpty(meta?.rank) ||
      (meta?.rank === 0 && name !== "Home" && path !== "/")
      ? true
      : false
    : false;
}

/** 按照路由中meta下的rank等級升序來排序路由 */
function ascending(arr: any[]) {
  arr.forEach((v, index) => {
    // 當rank不存在時，根據順序自動建立，首頁路由永遠在第一位
    if (handRank(v)) v.meta.rank = index + 2;
  });
  return arr.sort(
    (a: { meta: { rank: number } }, b: { meta: { rank: number } }) => {
      return a?.meta.rank - b?.meta.rank;
    }
  );
}

/** 過濾meta中showLink為false的選單 */
function filterTree(data: RouteComponent[]) {
  const newTree = cloneDeep(data).filter(
    (v: { meta: { showLink: boolean } }) => v.meta?.showLink !== false
  );
  newTree.forEach(
    (v: { children }) => v.children && (v.children = filterTree(v.children))
  );
  return newTree;
}

/** 過濾children長度為0的的目錄，當目錄下沒有選單時，會過濾此目錄，目錄沒有賦予roles許可權，當目錄下只要有一個選單有顯示許可權，那麼此目錄就會顯示 */
function filterChildrenTree(data: RouteComponent[]) {
  const newTree = cloneDeep(data).filter((v: any) => v?.children?.length !== 0);
  newTree.forEach(
    (v: { children }) => v.children && (v.children = filterTree(v.children))
  );
  return newTree;
}

/** 判斷兩個陣列彼此是否存在相同值 */
function isOneOfArray(a: Array<string>, b: Array<string>) {
  return Array.isArray(a) && Array.isArray(b)
    ? intersection(a, b).length > 0
      ? true
      : false
    : true;
}

/** 從localStorage裡取出當前登入使用者的角色roles，過濾無許可權的選單 */
function filterNoPermissionTree(data: RouteComponent[]) {
  const currentRoles =
    storageLocal().getItem<DataInfo<number>>(userKey)?.roles ?? [];
  const newTree = cloneDeep(data).filter((v: any) =>
    isOneOfArray(v.meta?.roles, currentRoles)
  );
  newTree.forEach(
    (v: any) => v.children && (v.children = filterNoPermissionTree(v.children))
  );
  return filterChildrenTree(newTree);
}

/** 通過指定 `key` 獲取父級路徑集合，預設 `key` 為 `path` */
function getParentPaths(value: string, routes: RouteRecordRaw[], key = "path") {
  // 深度遍歷查詢
  function dfs(routes: RouteRecordRaw[], value: string, parents: string[]) {
    for (let i = 0; i < routes.length; i++) {
      const item = routes[i];
      // 返回父級path
      if (item[key] === value) return parents;
      // children不存在或為空則不遞迴
      if (!item.children || !item.children.length) continue;
      // 往下查詢時將當前path入棧
      parents.push(item.path);

      if (dfs(item.children, value, parents).length) return parents;
      // 深度遍歷查詢未找到時當前path 出棧
      parents.pop();
    }
    // 未找到時返回空陣列
    return [];
  }

  return dfs(routes, value, []);
}

/** 查詢對應 `path` 的路由資訊 */
function findRouteByPath(path: string, routes: RouteRecordRaw[]) {
  let res = routes.find((item: { path: string }) => item.path == path);
  if (res) {
    return isProxy(res) ? toRaw(res) : res;
  } else {
    for (let i = 0; i < routes.length; i++) {
      if (
        routes[i].children instanceof Array &&
        routes[i].children.length > 0
      ) {
        res = findRouteByPath(path, routes[i].children);
        if (res) {
          return isProxy(res) ? toRaw(res) : res;
        }
      }
    }
    return null;
  }
}

/** 動態路由註冊完成後，再新增全屏404（頁面不存在）頁面，避免重新整理動態路由頁面時誤跳轉到404頁面 */
function addPathMatch() {
  if (!router.hasRoute("pathMatch")) {
    router.addRoute({
      path: "/:pathMatch(.*)*",
      name: "PageNotFound",
      component: () => import("@/views/error/404.vue"),
      meta: {
        title: "404",
        showLink: false
      }
    });
  }
}

/** 處理動態路由（後端返回的路由） */
function handleAsyncRoutes(routeList) {
  if (routeList.length === 0) {
    usePermissionStoreHook().handleWholeMenus(routeList);
  } else {
    formatFlatteningRoutes(addAsyncRoutes(routeList)).map(
      (v: RouteRecordRaw) => {
        // 防止重複新增路由
        if (
          router.options.routes[0].children.findIndex(
            value => value.path === v.path
          ) !== -1
        ) {
          return;
        } else {
          // 切記將路由push到routes後還需要使用addRoute，這樣路由才能正常跳轉
          router.options.routes[0].children.push(v);
          // 最終路由進行升序
          ascending(router.options.routes[0].children);
          if (!router.hasRoute(v?.name)) router.addRoute(v);
          const flattenRouters: any = router
            .getRoutes()
            .find(n => n.path === "/");
          // 保持router.options.routes[0].children與path為"/"的children一致，防止資料不一致導致異常
          flattenRouters.children = router.options.routes[0].children;
          router.addRoute(flattenRouters);
        }
      }
    );
    usePermissionStoreHook().handleWholeMenus(routeList);
  }
  if (!useMultiTagsStoreHook().getMultiTagsCache) {
    useMultiTagsStoreHook().handleTags("equal", [
      ...routerArrays,
      ...usePermissionStoreHook().flatteningRoutes.filter(
        v => v?.meta?.fixedTag
      )
    ]);
  }
  addPathMatch();
}

/** 初始化路由（`new Promise` 寫法防止在非同步請求中造成無限迴圈）*/
function initRouter() {
  if (getConfig()?.CachingAsyncRoutes) {
    // 開啟動態路由快取本地localStorage
    const key = "async-routes";
    const asyncRouteList = storageLocal().getItem(key) as any;
    if (asyncRouteList && asyncRouteList?.length > 0) {
      return new Promise(resolve => {
        handleAsyncRoutes(asyncRouteList);
        resolve(router);
      });
    } else {
      return new Promise(resolve => {
        getAsyncRoutes().then(data => {
          handleAsyncRoutes(cloneDeep(data));
          storageLocal().setItem(key, data);
          resolve(router);
        });
      });
    }
  } else {
    return new Promise(resolve => {
      getAsyncRoutes().then(data => {
        handleAsyncRoutes(cloneDeep(data));
        resolve(router);
      });
    });
  }
}

/**
 * 將多級巢狀路由處理成一維陣列
 * @param routesList 傳入路由
 * @returns 返回處理後的一維路由
 */
function formatFlatteningRoutes(routesList: RouteRecordRaw[]) {
  if (routesList.length === 0) return routesList;
  let hierarchyList = buildHierarchyTree(routesList);
  for (let i = 0; i < hierarchyList.length; i++) {
    if (hierarchyList[i].children) {
      hierarchyList = hierarchyList
        .slice(0, i + 1)
        .concat(hierarchyList[i].children, hierarchyList.slice(i + 1));
    }
  }
  return hierarchyList;
}

/**
 * 一維陣列處理成多級巢狀陣列（三級及以上的路由全部拍成二級，keep-alive 只支援到二級快取）
 * https://github.com/pure-admin/vue-pure-admin/issues/67
 * @param routesList 處理後的一維路由選單陣列
 * @returns 返回將一維陣列重新處理成規定路由的格式
 */
function formatTwoStageRoutes(routesList: RouteRecordRaw[]) {
  if (routesList.length === 0) return routesList;
  const newRoutesList: RouteRecordRaw[] = [];
  routesList.forEach((v: RouteRecordRaw) => {
    if (v.path === "/") {
      newRoutesList.push({
        component: v.component,
        name: v.name,
        path: v.path,
        redirect: v.redirect,
        meta: v.meta,
        children: []
      });
    } else {
      newRoutesList[0]?.children.push({ ...v });
    }
  });
  return newRoutesList;
}

/** 處理快取路由（新增、刪除、重新整理） */
function handleAliveRoute({ name }: ToRouteType, mode?: string) {
  switch (mode) {
    case "add":
      usePermissionStoreHook().cacheOperate({
        mode: "add",
        name
      });
      break;
    case "delete":
      usePermissionStoreHook().cacheOperate({
        mode: "delete",
        name
      });
      break;
    case "refresh":
      usePermissionStoreHook().cacheOperate({
        mode: "refresh",
        name
      });
      break;
    default:
      usePermissionStoreHook().cacheOperate({
        mode: "delete",
        name
      });
      useTimeoutFn(() => {
        usePermissionStoreHook().cacheOperate({
          mode: "add",
          name
        });
      }, 100);
  }
}

/** 過濾後端傳來的動態路由 重新生成規範路由 */
function addAsyncRoutes(arrRoutes: Array<RouteRecordRaw>) {
  if (!arrRoutes || !arrRoutes.length) return;
  const modulesRoutesKeys = Object.keys(modulesRoutes);
  arrRoutes.forEach((v: RouteRecordRaw) => {
    // 將backstage屬性加入meta，標識此路由為後端返回路由
    v.meta.backstage = true;
    // 父級的redirect屬性取值：如果子級存在且父級的redirect屬性不存在，預設取第一個子級的path；如果子級存在且父級的redirect屬性存在，取存在的redirect屬性，會覆蓋預設值
    if (v?.children && v.children.length && !v.redirect)
      v.redirect = v.children[0].path;
    // 父級的name屬性取值：如果子級存在且父級的name屬性不存在，預設取第一個子級的name；如果子級存在且父級的name屬性存在，取存在的name屬性，會覆蓋預設值（注意：測試中發現父級的name不能和子級name重複，如果重複會造成重定向無效（跳轉404），所以這裡給父級的name起名的時候後面會自動加上`Parent`，避免重複）
    if (v?.children && v.children.length && !v.name)
      v.name = (v.children[0].name as string) + "Parent";
    if (v.meta?.frameSrc) {
      v.component = IFrame;
    } else {
      // 對後端傳component元件路徑和不傳做相容（如果後端傳component元件路徑，那麼path可以隨便寫，如果不傳，component元件路徑會跟path保持一致）
      const index = v?.component
        ? modulesRoutesKeys.findIndex(ev => ev.includes(v.component as any))
        : modulesRoutesKeys.findIndex(ev => ev.includes(v.path));
      v.component = modulesRoutes[modulesRoutesKeys[index]];
    }
    if (v?.children && v.children.length) {
      addAsyncRoutes(v.children);
    }
  });
  return arrRoutes;
}

/** 獲取路由歷史模式 https://next.router.vuejs.org/zh/guide/essentials/history-mode.html */
function getHistoryMode(routerHistory): RouterHistory {
  // len為1 代表只有歷史模式 為2 代表歷史模式中存在base引數 https://next.router.vuejs.org/zh/api/#%E5%8F%82%E6%95%B0-1
  const historyMode = routerHistory.split(",");
  const leftMode = historyMode[0];
  const rightMode = historyMode[1];
  // no param
  if (historyMode.length === 1) {
    if (leftMode === "hash") {
      return createWebHashHistory("");
    } else if (leftMode === "h5") {
      return createWebHistory("");
    }
  } //has param
  else if (historyMode.length === 2) {
    if (leftMode === "hash") {
      return createWebHashHistory(rightMode);
    } else if (leftMode === "h5") {
      return createWebHistory(rightMode);
    }
  }
}

/** 獲取當前頁面按鈕級別的許可權 */
function getAuths(): Array<string> {
  return router.currentRoute.value.meta.auths as Array<string>;
}

/** 是否有按鈕級別的許可權（根據路由`meta`中的`auths`欄位進行判斷）*/
function hasAuth(value: string | Array<string>): boolean {
  if (!value) return false;
  /** 從當前路由的`meta`欄位裡獲取按鈕級別的所有自定義`code`值 */
  const metaAuths = getAuths();
  if (!metaAuths) return false;
  const isAuths = isString(value)
    ? metaAuths.includes(value)
    : isIncludeAllChildren(value, metaAuths);
  return isAuths ? true : false;
}

function handleTopMenu(route) {
  if (route?.children && route.children.length > 1) {
    if (route.redirect) {
      return route.children.filter(cur => cur.path === route.redirect)[0];
    } else {
      return route.children[0];
    }
  } else {
    return route;
  }
}

/** 獲取所有選單中的第一個選單（頂級選單）*/
function getTopMenu(tag = false): menuType {
  const topMenu = handleTopMenu(
    usePermissionStoreHook().wholeMenus[0]?.children[0]
  );
  tag && useMultiTagsStoreHook().handleTags("push", topMenu);
  return topMenu;
}

export {
  hasAuth,
  getAuths,
  ascending,
  filterTree,
  initRouter,
  getTopMenu,
  addPathMatch,
  isOneOfArray,
  getHistoryMode,
  addAsyncRoutes,
  getParentPaths,
  findRouteByPath,
  handleAliveRoute,
  formatTwoStageRoutes,
  formatFlatteningRoutes,
  filterNoPermissionTree
};
