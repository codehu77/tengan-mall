-- 目錄型節點（menu_type=1）本來就不該有自己的頁面元件，只是選單分類的容器；但前端
-- addAsyncRoutes() 在 component 空白時，會用路徑子字串比對去猜元件（modulesRoutesKeys
-- .findIndex(ev => ev.includes(v.path))），"/system" 底下四個子頁面的檔案路徑全部都包含
-- "/system" 這個子字串，猜到的是字母序最前面那個（log < menu < role < user），導致「系統管理」
-- 被誤裝上「操作日誌」頁面的元件——Vue Router 巢狀路由需要父層元件先渲染出來才有地方放子路由，
-- 誤裝的頁面沒有內嵌 <router-view />，子節點完全渲染不出來。
--
-- 修法：所有目錄型節點都指定一個共用的純轉發元件（只有 <router-view />，見
-- tengan-admin-web/src/layout/components/ParentView.vue），不要留空給前端去猜。
-- 之後新增其他目錄型節點（例如未來 Phase 的「商品管理」）也要照這個慣例填這個值。

UPDATE menu SET component = 'layout/components/ParentView' WHERE id = 1;
