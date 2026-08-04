import { ref } from "vue";
import reDialog from "./index.vue";
import { useTimeoutFn } from "@vueuse/core";
import { withInstall } from "@pureadmin/utils";
import type {
  EventType,
  ArgsType,
  DialogProps,
  ButtonProps,
  DialogOptions
} from "./type";

const dialogStore = ref<Array<DialogOptions>>([]);

/** 開啟彈框 */
const addDialog = (options: DialogOptions) => {
  const open = () =>
    dialogStore.value.push(Object.assign(options, { visible: true }));
  if (options?.openDelay) {
    useTimeoutFn(() => {
      open();
    }, options.openDelay);
  } else {
    open();
  }
};

/** 關閉彈框 */
const closeDialog = (options: DialogOptions, index: number, args?: any) => {
  dialogStore.value[index].visible = false;
  options.closeCallBack && options.closeCallBack({ options, index, args });

  const closeDelay = options?.closeDelay ?? 200;
  useTimeoutFn(() => {
    dialogStore.value.splice(index, 1);
  }, closeDelay);
};

/**
 * @description 更改彈框自身屬性值
 * @param value 屬性值
 * @param key 屬性，預設`title`
 * @param index 彈框索引（預設`0`，代表只有一個彈框，對於巢狀彈框要改哪個彈框的屬性值就把該彈框索引賦給`index`）
 */
const updateDialog = (value: any, key = "title", index = 0) => {
  dialogStore.value[index][key] = value;
};

/** 關閉所有彈框 */
const closeAllDialog = () => {
  dialogStore.value = [];
};

/** 千萬別忘了在下面這三處引入並註冊下，放心註冊，不使用`addDialog`呼叫就不會被掛載
 * https://github.com/pure-admin/vue-pure-admin/blob/main/src/App.vue#L4
 * https://github.com/pure-admin/vue-pure-admin/blob/main/src/App.vue#L12
 * https://github.com/pure-admin/vue-pure-admin/blob/main/src/App.vue#L22
 */
const ReDialog = withInstall(reDialog);

export type { EventType, ArgsType, DialogProps, ButtonProps, DialogOptions };
export {
  ReDialog,
  dialogStore,
  addDialog,
  closeDialog,
  updateDialog,
  closeAllDialog
};
