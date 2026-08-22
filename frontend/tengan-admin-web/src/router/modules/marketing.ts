const Layout = () => import("@/layout/index.vue");

/**
 * 秒殺活動的「設定活動商品」是獨立路由頁面（不是 dialog）——商品一多，dialog 塞不下、逐筆
 * 新增/編輯/刪除也需要完整版面，比照 products/spu-wizard 同樣的模式（靜態路由掛在 Layout 底下、
 * showLink:false 隱藏，不透過後端動態選單，只能從秒殺活動列表頁的按鈕導覽進來）。
 */
export default {
  path: "/marketing/seckill-activity-products",
  name: "SeckillActivityProductsParent",
  component: Layout,
  meta: {
    title: "秒殺活動商品",
    showLink: false,
    rank: 101
  },
  children: [
    {
      path: "/marketing/seckill-activity/:id/products",
      name: "SeckillActivityProducts",
      component: () => import("@/views/marketing/seckill-activity/products.vue"),
      meta: {
        title: "設定活動商品",
        showLink: false
      }
    }
  ]
} satisfies RouteConfigsTable;
