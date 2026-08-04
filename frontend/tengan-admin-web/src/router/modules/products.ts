const Layout = () => import("@/layout/index.vue");

/**
 * SPU 新增/編輯精靈是獨立路由頁面（不是 dialog），但不是側邊選單項目——透過列表頁的按鈕導覽進來，
 * 所以用靜態路由掛在 Layout 底下、showLink:false 隱藏，不透過後端動態選單（tengan-admin_web/
 * spu_sku_form_design 記憶：三步驟精靈設計定案）。
 */
export default {
  path: "/products/spu-wizard",
  name: "ProductSpuWizardParent",
  component: Layout,
  meta: {
    title: "SPU商品",
    showLink: false,
    rank: 100
  },
  children: [
    {
      path: "/products/spu/create",
      name: "ProductSpuCreate",
      component: () => import("@/views/products/spu/wizard.vue"),
      meta: {
        title: "新增SPU商品",
        showLink: false
      }
    },
    {
      path: "/products/spu/edit/:id",
      name: "ProductSpuEdit",
      component: () => import("@/views/products/spu/wizard.vue"),
      meta: {
        title: "編輯SPU商品",
        showLink: false
      }
    }
  ]
} satisfies RouteConfigsTable;
