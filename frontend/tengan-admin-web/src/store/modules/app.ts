import { defineStore } from "pinia";
import {
  type appType,
  store,
  getConfig,
  storageLocal,
  deviceDetection,
  responsiveStorageNameSpace
} from "../utils";

export const useAppStore = defineStore("pure-app", {
  state: (): appType => ({
    sidebar: {
      opened:
        storageLocal().getItem<StorageConfigs>(
          `${responsiveStorageNameSpace()}layout`
        )?.sidebarStatus ?? getConfig().SidebarStatus,
      withoutAnimation: false,
      isClickCollapse: false
    },
    // 這裡的layout用於監聽容器拖拉後恢復對應的導航模式
    layout:
      storageLocal().getItem<StorageConfigs>(
        `${responsiveStorageNameSpace()}layout`
      )?.layout ?? getConfig().Layout,
    device: deviceDetection() ? "mobile" : "desktop",
    // 瀏覽器視窗的可視區域大小
    viewportSize: {
      width: document.documentElement.clientWidth,
      height: document.documentElement.clientHeight
    }
  }),
  getters: {
    getSidebarStatus(state) {
      return state.sidebar.opened;
    },
    getDevice(state) {
      return state.device;
    },
    getViewportWidth(state) {
      return state.viewportSize.width;
    },
    getViewportHeight(state) {
      return state.viewportSize.height;
    }
  },
  actions: {
    TOGGLE_SIDEBAR(opened?: boolean, resize?: string) {
      const layout = storageLocal().getItem<StorageConfigs>(
        `${responsiveStorageNameSpace()}layout`
      );
      if (opened && resize) {
        this.sidebar.withoutAnimation = true;
        this.sidebar.opened = true;
        layout.sidebarStatus = true;
      } else if (!opened && resize) {
        this.sidebar.withoutAnimation = true;
        this.sidebar.opened = false;
        layout.sidebarStatus = false;
      } else if (!opened && !resize) {
        this.sidebar.withoutAnimation = false;
        this.sidebar.opened = !this.sidebar.opened;
        this.sidebar.isClickCollapse = !this.sidebar.opened;
        layout.sidebarStatus = this.sidebar.opened;
      }
      storageLocal().setItem(`${responsiveStorageNameSpace()}layout`, layout);
    },
    async toggleSideBar(opened?: boolean, resize?: string) {
      await this.TOGGLE_SIDEBAR(opened, resize);
    },
    toggleDevice(device: string) {
      this.device = device;
    },
    setLayout(layout) {
      this.layout = layout;
    },
    setViewportSize(size) {
      this.viewportSize = size;
    }
  }
});

export function useAppStoreHook() {
  return useAppStore(store);
}
