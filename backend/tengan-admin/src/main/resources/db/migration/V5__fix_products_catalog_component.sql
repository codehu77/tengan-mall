-- 同一個坑踩了第二次：V3__fix_catalog_component.sql 已經記錄過「目錄型節點(menu_type=1)沒填
-- component 時，前端會用路徑子字串亂猜」這個問題，comment 裡還明確點名「之後新增商品管理也要
-- 照這個慣例」——V4__seed_product_menu.sql 新增「商品管理」目錄(id=9)時忘了照做，導致
-- /products 這個目錄猜到 products/brand/form.vue 當自己的元件（字母序 brand < category，
-- 猜到最前面那個），子路由(商品分類/品牌管理)完全渲染不出來。
UPDATE menu SET component = 'layout/components/ParentView' WHERE id = 9;
