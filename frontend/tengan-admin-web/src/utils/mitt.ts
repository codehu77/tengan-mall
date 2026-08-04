import type { Emitter } from "mitt";
import mitt from "mitt";

/** 全域性公共事件需要在此處新增型別 */
type Events = {
  openPanel: string;
  tagOnClick: string;
  logoChange: boolean;
  tagViewsChange: string;
  changLayoutRoute: string;
  tagViewsShowModel: string;
};

export const emitter: Emitter<Events> = mitt<Events>();
