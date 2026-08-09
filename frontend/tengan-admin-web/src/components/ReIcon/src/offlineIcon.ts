// 這裡存放本地圖示，在 src/layout/index.vue 檔案中載入，避免在首啟動載入
import { getSvgInfo } from "@pureadmin/utils";
import { addIcon } from "@iconify/vue/dist/offline";

// https://icon-sets.iconify.design/ep/?keyword=ep
import EpHomeFilled from "~icons/ep/home-filled?raw";
import EpSetting from "~icons/ep/setting?raw";
import EpUser from "~icons/ep/user?raw";
import EpUserFilled from "~icons/ep/user-filled?raw";
import EpGoods from "~icons/ep/goods?raw";
import EpBox from "~icons/ep/box?raw";
import EpPresent from "~icons/ep/present?raw";
import EpMenu from "~icons/ep/menu?raw";
import EpDocument from "~icons/ep/document?raw";
import EpPriceTag from "~icons/ep/price-tag?raw";
import EpNotebook from "~icons/ep/notebook?raw";
import EpSearch from "~icons/ep/search?raw";
import EpList from "~icons/ep/list?raw";
import EpOfficeBuilding from "~icons/ep/office-building?raw";
import EpShoppingCart from "~icons/ep/shopping-cart?raw";
import EpTicket from "~icons/ep/ticket?raw";

// https://icon-sets.iconify.design/ri/?keyword=ri
import RiSearchLine from "~icons/ri/search-line?raw";
import RiInformationLine from "~icons/ri/information-line?raw";

const icons = [
  // Element Plus Icon: https://github.com/element-plus/element-plus-icons
  // 這批名字（PascalCase）直接對應 tengan-admin 選單表 icon 欄位存的 Element Plus icon
  // 元件名稱（見 views/system/menu/form.vue 的輸入提示「Element Plus icon 名稱」），
  // 後端原封不動把 DB 裡的字串送出來，這裡的 key 要跟那個字串完全一致才能對上。
  ["ep/home-filled", EpHomeFilled],
  ["Setting", EpSetting],
  ["User", EpUser],
  ["UserFilled", EpUserFilled],
  ["Goods", EpGoods],
  ["Box", EpBox],
  ["Present", EpPresent],
  ["Menu", EpMenu],
  ["Document", EpDocument],
  ["PriceTag", EpPriceTag],
  ["Notebook", EpNotebook],
  ["Search", EpSearch],
  ["List", EpList],
  ["OfficeBuilding", EpOfficeBuilding],
  ["ShoppingCart", EpShoppingCart],
  ["Ticket", EpTicket],
  // Remix Icon: https://github.com/Remix-Design/RemixIcon
  ["ri/search-line", RiSearchLine],
  ["ri/information-line", RiInformationLine]
];

// 本地選單圖示，後端在路由的 icon 中返回對應的圖示字串並且前端在此處使用 addIcon 新增即可渲染選單圖示
icons.forEach(([name, icon]) => {
  addIcon(name as string, getSvgInfo(icon as string));
});
