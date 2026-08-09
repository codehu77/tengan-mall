const Layout = () => import("@/layout/index.vue");

export default [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/index.vue"),
    meta: {
      title: "登入",
      showLink: false
    }
  },
  // 全屏403（無權訪問）頁面
  {
    path: "/access-denied",
    name: "AccessDenied",
    component: () => import("@/views/error/403.vue"),
    meta: {
      title: "403",
      showLink: false
    }
  },
  // 全屏500（伺服器出錯）頁面
  {
    path: "/server-error",
    name: "ServerError",
    component: () => import("@/views/error/500.vue"),
    meta: {
      title: "500",
      showLink: false
    }
  },
  {
    path: "/redirect",
    component: Layout,
    meta: {
      title: "載入中...",
      showLink: false
    },
    children: [
      {
        path: "/redirect/:path(.*)",
        name: "Redirect",
        component: () => import("@/layout/redirect.vue")
      }
    ]
  },
  // 個人中心：任何登入的管理員都能進，不掛在 RBAC 選單樹上，所以不出現在側邊欄。
  {
    path: "/personal",
    component: Layout,
    meta: {
      title: "個人中心",
      showLink: false
    },
    children: [
      {
        path: "/personal",
        name: "Personal",
        component: () => import("@/views/personal/index.vue"),
        meta: {
          title: "個人中心",
          showLink: false
        }
      }
    ]
  }
] satisfies Array<RouteConfigsTable>;
