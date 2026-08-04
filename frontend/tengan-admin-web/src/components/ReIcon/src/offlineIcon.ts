// 這裡存放本地圖示，在 src/layout/index.vue 檔案中載入，避免在首啟動載入
import { getSvgInfo } from "@pureadmin/utils";
import { addIcon } from "@iconify/vue/dist/offline";

// https://icon-sets.iconify.design/ep/?keyword=ep
import EpHomeFilled from "~icons/ep/home-filled?raw";

// https://icon-sets.iconify.design/ri/?keyword=ri
import RiSearchLine from "~icons/ri/search-line?raw";
import RiInformationLine from "~icons/ri/information-line?raw";

const icons = [
  // Element Plus Icon: https://github.com/element-plus/element-plus-icons
  ["ep/home-filled", EpHomeFilled],
  // Remix Icon: https://github.com/Remix-Design/RemixIcon
  ["ri/search-line", RiSearchLine],
  ["ri/information-line", RiInformationLine]
];

// 本地選單圖示，後端在路由的 icon 中返回對應的圖示字串並且前端在此處使用 addIcon 新增即可渲染選單圖示
icons.forEach(([name, icon]) => {
  addIcon(name as string, getSvgInfo(icon as string));
});
