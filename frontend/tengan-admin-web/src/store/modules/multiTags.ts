import { defineStore } from "pinia";
import {
  type multiType,
  type positionType,
  store,
  isUrl,
  isEqual,
  isNumber,
  isBoolean,
  getConfig,
  routerArrays,
  storageLocal,
  responsiveStorageNameSpace
} from "../utils";
import { usePermissionStoreHook } from "./permission";

export const useMultiTagsStore = defineStore("pure-multiTags", {
  state: () => ({
    // 儲存標籤頁資訊（路由資訊）
    multiTags: storageLocal().getItem<StorageConfigs>(
      `${responsiveStorageNameSpace()}configure`
    )?.multiTagsCache
      ? storageLocal().getItem<StorageConfigs>(
          `${responsiveStorageNameSpace()}tags`
        )
      : ([
          ...routerArrays,
          ...usePermissionStoreHook().flatteningRoutes.filter(
            v => v?.meta?.fixedTag
          )
        ] as any),
    multiTagsCache: storageLocal().getItem<StorageConfigs>(
      `${responsiveStorageNameSpace()}configure`
    )?.multiTagsCache
  }),
  getters: {
    getMultiTagsCache(state) {
      return state.multiTagsCache;
    }
  },
  actions: {
    multiTagsCacheChange(multiTagsCache: boolean) {
      this.multiTagsCache = multiTagsCache;
      if (multiTagsCache) {
        storageLocal().setItem(
          `${responsiveStorageNameSpace()}tags`,
          this.multiTags
        );
      } else {
        storageLocal().removeItem(`${responsiveStorageNameSpace()}tags`);
      }
    },
    tagsCache(multiTags) {
      this.getMultiTagsCache &&
        storageLocal().setItem(
          `${responsiveStorageNameSpace()}tags`,
          multiTags
        );
    },
    handleTags<T>(
      mode: string,
      value?: T | multiType,
      position?: positionType
    ): T {
      switch (mode) {
        case "equal":
          this.multiTags = value;
          this.tagsCache(this.multiTags);
          break;
        case "push":
          {
            const tagVal = value as multiType;
            // 不新增到標籤頁
            if (tagVal?.meta?.hiddenTag) return;
            // 如果是外鏈無需新增資訊到標籤頁
            if (isUrl(tagVal?.name)) return;
            // 如果title為空拒絕新增空資訊到標籤頁
            if (tagVal?.meta?.title.length === 0) return;
            // showLink:false 不新增到標籤頁
            if (isBoolean(tagVal?.meta?.showLink) && !tagVal?.meta?.showLink)
              return;
            const tagPath = tagVal.path;
            const tagHasExits = this.multiTags.some(tag => {
              return (
                tag.path === tagPath &&
                isEqual(tag?.query, tagVal?.query) &&
                isEqual(tag?.params, tagVal?.params)
              );
            });

            if (tagHasExits) return;

            // 動態路由可開啟的最大數量
            const dynamicLevel = tagVal?.meta?.dynamicLevel ?? -1;
            if (dynamicLevel > 0) {
              if (
                this.multiTags.filter(e => e?.path === tagPath).length >=
                dynamicLevel
              ) {
                // 如果當前已開啟的動態路由數大於dynamicLevel，替換第一個動態路由標籤
                const index = this.multiTags.findIndex(
                  item => item?.path === tagPath
                );
                index !== -1 && this.multiTags.splice(index, 1);
              }
            }
            this.multiTags.push(value);
            this.tagsCache(this.multiTags);
            if (
              getConfig()?.MaxTagsLevel &&
              isNumber(getConfig().MaxTagsLevel)
            ) {
              if (this.multiTags.length > getConfig().MaxTagsLevel) {
                this.multiTags.splice(1, 1);
              }
            }
          }
          break;
        case "splice":
          if (!position) {
            const index = this.multiTags.findIndex(v => v.path === value);
            if (index === -1) return;
            this.multiTags.splice(index, 1);
          } else {
            this.multiTags.splice(position?.startIndex, position?.length);
          }
          this.tagsCache(this.multiTags);
          return this.multiTags;
        case "slice":
          return this.multiTags.slice(-1);
      }
    }
  }
});

export function useMultiTagsStoreHook() {
  return useMultiTagsStore(store);
}
