import NProgress from "nprogress";
import "nprogress/nprogress.css";

NProgress.configure({
  // 動畫方式
  easing: "ease",
  // 遞增進度條的速度
  speed: 500,
  // 是否顯示載入ico
  showSpinner: false,
  // 自動遞增間隔
  trickleSpeed: 200,
  // 初始化時的最小百分比
  minimum: 0.3
});

export default NProgress;
