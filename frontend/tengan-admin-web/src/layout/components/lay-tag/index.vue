<script setup lang="ts">
import { emitter } from "@/utils/mitt";
import NProgress from "@/utils/progress";
import { RouteConfigs } from "../../types";
import { useTags } from "../../hooks/useTag";
import { routerArrays } from "@/layout/types";
import { onClickOutside } from "@vueuse/core";
import TagChrome from "./components/TagChrome.vue";
import { handleAliveRoute, getTopMenu } from "@/router/utils";
import { useSettingStoreHook } from "@/store/modules/settings";
import { useMultiTagsStoreHook } from "@/store/modules/multiTags";
import { usePermissionStoreHook } from "@/store/modules/permission";
import { ref, watch, unref, toRaw, nextTick, onBeforeUnmount } from "vue";
import {
  delay,
  isEqual,
  isAllEmpty,
  useResizeObserver
} from "@pureadmin/utils";

import ExitFullscreen from "~icons/ri/fullscreen-exit-fill";
import Fullscreen from "~icons/ri/fullscreen-fill";
import ArrowDown from "~icons/ri/arrow-down-s-line";
import ArrowRightSLine from "~icons/ri/arrow-right-s-line";
import ArrowLeftSLine from "~icons/ri/arrow-left-s-line";

const {
  Close,
  route,
  router,
  visible,
  showTags,
  instance,
  multiTags,
  tagsViews,
  buttonTop,
  buttonLeft,
  showModel,
  translateX,
  isFixedTag,
  pureSetting,
  activeIndex,
  getTabStyle,
  isScrolling,
  iconIsActive,
  linkIsActive,
  currentSelect,
  scheduleIsActive,
  getContextMenuStyle,
  closeMenu,
  onMounted,
  onMouseenter,
  onMouseleave,
  onContentFullScreen
} = useTags();

const tabDom = ref();
const containerDom = ref();
const scrollbarDom = ref();
const contextmenuRef = ref();
const isShowArrow = ref(false);
const topPath = getTopMenu()?.path;
const { VITE_HIDE_HOME } = import.meta.env;
const fixedTags = [
  ...routerArrays,
  ...usePermissionStoreHook().flatteningRoutes.filter(v => v?.meta?.fixedTag)
];

const dynamicTagView = async () => {
  await nextTick();
  const index = multiTags.value.findIndex(item => {
    if (!isAllEmpty(route.query)) {
      return isEqual(route.query, item.query);
    } else if (!isAllEmpty(route.params)) {
      return isEqual(route.params, item.params);
    } else {
      return route.path === item.path;
    }
  });
  moveToView(index);
};

const moveToView = async (index: number): Promise<void> => {
  await nextTick();
  const tabNavPadding = 10;
  if (!instance.refs["dynamic" + index]) return;
  const tabItemEl = instance.refs["dynamic" + index][0];
  const tabItemElOffsetLeft = (tabItemEl as HTMLElement)?.offsetLeft;
  const tabItemOffsetWidth = (tabItemEl as HTMLElement)?.offsetWidth;
  // 標籤頁導航欄可視長度（不包含溢位部分）
  const scrollbarDomWidth = scrollbarDom.value
    ? scrollbarDom.value?.offsetWidth
    : 0;

  // 已有標籤頁總長度（包含溢位部分）
  const tabDomWidth = tabDom.value ? tabDom.value?.offsetWidth : 0;

  scrollbarDomWidth <= tabDomWidth
    ? (isShowArrow.value = true)
    : (isShowArrow.value = false);
  if (tabDomWidth < scrollbarDomWidth || tabItemElOffsetLeft === 0) {
    translateX.value = 0;
  } else if (tabItemElOffsetLeft < -translateX.value) {
    // 標籤在可視區域左側
    translateX.value = -tabItemElOffsetLeft + tabNavPadding;
  } else if (
    tabItemElOffsetLeft > -translateX.value &&
    tabItemElOffsetLeft + tabItemOffsetWidth <
      -translateX.value + scrollbarDomWidth
  ) {
    // 標籤在可視區域
    translateX.value = Math.min(
      0,
      scrollbarDomWidth -
        tabItemOffsetWidth -
        tabItemElOffsetLeft -
        tabNavPadding
    );
  } else {
    // 標籤在可視區域右側
    translateX.value = -(
      tabItemElOffsetLeft -
      (scrollbarDomWidth - tabNavPadding - tabItemOffsetWidth)
    );
  }
};

const handleScroll = (offset: number): void => {
  const scrollbarDomWidth = scrollbarDom.value
    ? scrollbarDom.value?.offsetWidth
    : 0;
  const tabDomWidth = tabDom.value ? tabDom.value.offsetWidth : 0;
  if (offset > 0) {
    translateX.value = Math.min(0, translateX.value + offset);
  } else {
    if (scrollbarDomWidth < tabDomWidth) {
      if (translateX.value >= -(tabDomWidth - scrollbarDomWidth)) {
        translateX.value = Math.max(
          translateX.value + offset,
          scrollbarDomWidth - tabDomWidth
        );
      }
    } else {
      translateX.value = 0;
    }
  }
  isScrolling.value = false;
};

const handleWheel = (event: WheelEvent): void => {
  isScrolling.value = true;
  const scrollIntensity = Math.abs(event.deltaX) + Math.abs(event.deltaY);
  let offset = 0;
  if (event.deltaX < 0) {
    offset = scrollIntensity > 0 ? scrollIntensity : 100;
  } else {
    offset = scrollIntensity > 0 ? -scrollIntensity : -100;
  }

  smoothScroll(offset);
};

const smoothScroll = (offset: number): void => {
  // 每幀滾動的距離
  const scrollAmount = 20;
  let remaining = Math.abs(offset);

  const scrollStep = () => {
    const scrollOffset = Math.sign(offset) * Math.min(scrollAmount, remaining);
    handleScroll(scrollOffset);
    remaining -= Math.abs(scrollOffset);

    if (remaining > 0) {
      requestAnimationFrame(scrollStep);
    }
  };

  requestAnimationFrame(scrollStep);
};

function dynamicRouteTag(value: string): void {
  const hasValue = multiTags.value.some(item => {
    return item.path === value;
  });

  function concatPath(arr: object[], value: string) {
    if (!hasValue) {
      arr.forEach((arrItem: any) => {
        if (arrItem.path === value) {
          useMultiTagsStoreHook().handleTags("push", {
            path: value,
            meta: arrItem.meta,
            name: arrItem.name
          });
        } else {
          if (arrItem.children && arrItem.children.length > 0) {
            concatPath(arrItem.children, value);
          }
        }
      });
    }
  }
  concatPath(router.options.routes as any, value);
}

/** 重新整理路由 */
function onFresh() {
  NProgress.start();
  const { fullPath, query } = unref(route);
  router.replace({
    path: "/redirect" + fullPath,
    query
  });
  handleAliveRoute(route as ToRouteType, "refresh");
  NProgress.done();
}

function deleteDynamicTag(obj: any, current: any, tag?: string) {
  const valueIndex: number = multiTags.value.findIndex((item: any) => {
    if (item.query) {
      if (item.path === obj.path) {
        return item.query === obj.query;
      }
    } else if (item.params) {
      if (item.path === obj.path) {
        return item.params === obj.params;
      }
    } else {
      return item.path === obj.path;
    }
  });

  const spliceRoute = (
    startIndex?: number,
    length?: number,
    other?: boolean
  ): void => {
    if (other) {
      useMultiTagsStoreHook().handleTags(
        "equal",
        [
          VITE_HIDE_HOME === "false" ? fixedTags : toRaw(getTopMenu()),
          obj
        ].flat()
      );
    } else {
      useMultiTagsStoreHook().handleTags("splice", "", {
        startIndex,
        length
      }) as any;
    }
    dynamicTagView();
  };

  if (tag === "other") {
    spliceRoute(1, 1, true);
  } else if (tag === "left") {
    spliceRoute(fixedTags.length, valueIndex - fixedTags.length);
  } else if (tag === "right") {
    spliceRoute(valueIndex + 1, multiTags.value.length);
  } else {
    // 從當前匹配到的路徑中刪除
    spliceRoute(valueIndex, 1);
  }
  const newRoute = useMultiTagsStoreHook().handleTags("slice");
  if (current === route.path) {
    // 如果刪除當前啟用tag就自動切換到最後一個tag
    if (tag === "left") return;
    if (newRoute[0]?.query) {
      router.push({ name: newRoute[0].name, query: newRoute[0].query });
    } else if (newRoute[0]?.params) {
      router.push({ name: newRoute[0].name, params: newRoute[0].params });
    } else {
      router.push({ path: newRoute[0].path });
    }
  } else {
    if (!multiTags.value.length) return;
    if (multiTags.value.some(item => item.path === route.path)) return;
    if (newRoute[0]?.query) {
      router.push({ name: newRoute[0].name, query: newRoute[0].query });
    } else if (newRoute[0]?.params) {
      router.push({ name: newRoute[0].name, params: newRoute[0].params });
    } else {
      router.push({ path: newRoute[0].path });
    }
  }
}

function deleteMenu(item, tag?: string) {
  deleteDynamicTag(item, item.path, tag);
  handleAliveRoute(route as ToRouteType);
}

function onClickDrop(key, item, selectRoute?: RouteConfigs) {
  if (item && item.disabled) return;

  let selectTagRoute;
  if (selectRoute) {
    selectTagRoute = {
      path: selectRoute.path,
      meta: selectRoute.meta,
      name: selectRoute.name,
      query: selectRoute?.query,
      params: selectRoute?.params
    };
  } else {
    selectTagRoute = { path: route.path, meta: route.meta };
  }

  // 當前路由資訊
  switch (key) {
    case 0:
      // 重新整理路由
      onFresh();
      break;
    case 1:
      // 關閉當前標籤頁
      deleteMenu(selectTagRoute);
      break;
    case 2:
      // 關閉左側標籤頁
      deleteMenu(selectTagRoute, "left");
      break;
    case 3:
      // 關閉右側標籤頁
      deleteMenu(selectTagRoute, "right");
      break;
    case 4:
      // 關閉其他標籤頁
      deleteMenu(selectTagRoute, "other");
      break;
    case 5:
      // 關閉全部標籤頁
      useMultiTagsStoreHook().handleTags("splice", "", {
        startIndex: fixedTags.length,
        length: multiTags.value.length
      });
      router.push(topPath);
      // router.push(fixedTags[fixedTags.length - 1]?.path);
      handleAliveRoute(route as ToRouteType);
      break;
    case 6:
      // 內容區全屏
      onContentFullScreen();
      setTimeout(() => {
        if (pureSetting.hiddenSideBar) {
          tagsViews[6].icon = ExitFullscreen;
          tagsViews[6].text = "內容區退出全屏";
        } else {
          tagsViews[6].icon = Fullscreen;
          tagsViews[6].text = "內容區全屏";
        }
      }, 100);
      break;
  }
  setTimeout(() => {
    showMenuModel(route.fullPath, route.query, route.params);
  });
}

function handleCommand(command: any) {
  const { key, item } = command;
  onClickDrop(key, item);
}

/** 觸發右鍵中選單的點選事件 */
function selectTag(key, item) {
  closeMenu();
  onClickDrop(key, item, currentSelect.value);
}

function showMenus(value: boolean) {
  Array.of(1, 2, 3, 4, 5).forEach(v => {
    tagsViews[v].show = value;
  });
}

function disabledMenus(value: boolean, fixedTag = false) {
  Array.of(1, 2, 3, 4, 5).forEach(v => {
    tagsViews[v].disabled = value;
  });
  if (fixedTag) {
    tagsViews[2].show = false;
    tagsViews[2].disabled = true;
  }
}

/** 檢查當前右鍵的選單兩邊是否存在別的選單，如果左側的選單是頂級選單，則不顯示關閉左側標籤頁，如果右側沒有選單，則不顯示關閉右側標籤頁 */
function showMenuModel(
  currentPath: string,
  query: object = {},
  params: object = {},
  refresh = false
) {
  const allRoute = multiTags.value;
  const routeLength = multiTags.value.length;
  let currentIndex = -1;
  if (!isAllEmpty(params)) {
    currentIndex = allRoute.findIndex(v => isEqual(v.params, params));
  } else if (!isAllEmpty(query)) {
    currentIndex = allRoute.findIndex(v => isEqual(v.query, query));
  } else {
    currentIndex = allRoute.findIndex(v => v.path === currentPath);
  }
  function fixedTagDisabled() {
    if (allRoute[currentIndex]?.meta?.fixedTag) {
      Array.of(1, 2, 3, 4, 5).forEach(v => {
        tagsViews[v].disabled = true;
      });
    }
  }

  showMenus(true);

  if (refresh) {
    tagsViews[0].show = true;
  }

  /**
   * currentIndex為1時，左側的選單頂級選單，則不顯示關閉左側標籤頁
   * 如果currentIndex等於routeLength-1，右側沒有選單，則不顯示關閉右側標籤頁
   */
  if (currentIndex === 1 && routeLength !== 2) {
    // 左側的選單是頂級選單，右側存在別的選單
    tagsViews[2].show = false;
    Array.of(1, 3, 4, 5).forEach(v => {
      tagsViews[v].disabled = false;
    });
    tagsViews[2].disabled = true;
    fixedTagDisabled();
  } else if (currentIndex === 1 && routeLength === 2) {
    disabledMenus(false);
    // 左側的選單是頂級選單，右側不存在別的選單
    Array.of(2, 3, 4).forEach(v => {
      tagsViews[v].show = false;
      tagsViews[v].disabled = true;
    });
    fixedTagDisabled();
  } else if (routeLength - 1 === currentIndex && currentIndex !== 0) {
    // 當前路由是所有路由中的最後一個
    tagsViews[3].show = false;
    Array.of(1, 2, 4, 5).forEach(v => {
      tagsViews[v].disabled = false;
    });
    tagsViews[3].disabled = true;
    if (allRoute[currentIndex - 1]?.meta?.fixedTag) {
      tagsViews[2].show = false;
      tagsViews[2].disabled = true;
    }
    fixedTagDisabled();
  } else if (currentIndex === 0 || currentPath === `/redirect${topPath}`) {
    // 當前路由為頂級選單
    disabledMenus(true);
  } else {
    disabledMenus(false, allRoute[currentIndex - 1]?.meta?.fixedTag);
    fixedTagDisabled();
  }
}

function openMenu(tag, e) {
  closeMenu();
  if (tag.path === topPath || tag?.meta?.fixedTag) {
    // 右鍵選單為頂級選單或擁有 fixedTag 屬性，只顯示重新整理
    showMenus(false);
    tagsViews[0].show = true;
  } else if (route.path !== tag.path && route.name !== tag.name) {
    // 右鍵選單不匹配當前路由，隱藏重新整理
    tagsViews[0].show = false;
    showMenuModel(tag.path, tag.query, tag.params);
  } else if (multiTags.value.length === 2 && route.path !== tag.path) {
    showMenus(true);
    // 只有兩個標籤時不顯示關閉其他標籤頁
    tagsViews[4].show = false;
    showMenuModel(tag.path, tag.query, tag.params);
  } else {
    showMenuModel(tag.path, tag.query, tag.params, true);
  }

  currentSelect.value = tag;
  const menuMinWidth = 140;
  const offsetLeft = unref(containerDom).getBoundingClientRect().left;
  const offsetWidth = unref(containerDom).offsetWidth;
  const maxLeft = offsetWidth - menuMinWidth;
  const left = e.clientX - offsetLeft + 5;
  if (left > maxLeft) {
    buttonLeft.value = maxLeft;
  } else {
    buttonLeft.value = left;
  }
  useSettingStoreHook().hiddenSideBar
    ? (buttonTop.value = e.clientY)
    : (buttonTop.value = e.clientY - 40);
  nextTick(() => {
    visible.value = true;
  });
}

/** 觸發tags標籤切換 */
function tagOnClick(item) {
  const { name, path } = item;
  if (name) {
    if (item.query) {
      router.push({
        name,
        query: item.query
      });
    } else if (item.params) {
      router.push({
        name,
        params: item.params
      });
    } else {
      router.push({ name });
    }
  } else {
    router.push({ path });
  }
  emitter.emit("tagOnClick", item);
}

onClickOutside(contextmenuRef, closeMenu, {
  detectIframe: true
});

watch(route, () => {
  activeIndex.value = -1;
  dynamicTagView();
});

onMounted(() => {
  if (!instance) return;

  // 根據當前路由初始化操作標籤頁的停用狀態
  showMenuModel(route.fullPath);

  // 觸發隱藏標籤頁
  emitter.on("tagViewsChange", (key: any) => {
    if (unref(showTags as any) === key) return;
    (showTags as any).value = key;
  });

  // 改變標籤風格
  emitter.on("tagViewsShowModel", key => {
    showModel.value = key;
  });

  //  接收側邊欄切換傳遞過來的引數
  emitter.on("changLayoutRoute", indexPath => {
    dynamicRouteTag(indexPath);
    setTimeout(() => {
      showMenuModel(indexPath);
    });
  });

  useResizeObserver(scrollbarDom, dynamicTagView);
  delay().then(() => dynamicTagView());
});

onBeforeUnmount(() => {
  // 解綁`tagViewsChange`、`tagViewsShowModel`、`changLayoutRoute`公共事件，防止多次觸發
  emitter.off("tagViewsChange");
  emitter.off("tagViewsShowModel");
  emitter.off("changLayoutRoute");
});
</script>

<template>
  <div v-if="!showTags" ref="containerDom" class="tags-view">
    <span v-show="isShowArrow" class="arrow-left">
      <IconifyIconOffline :icon="ArrowLeftSLine" @click="handleScroll(200)" />
    </span>
    <div
      ref="scrollbarDom"
      class="scroll-container"
      :class="showModel === 'chrome' && 'chrome-scroll-container'"
      @wheel.prevent="handleWheel"
    >
      <div ref="tabDom" class="tab select-none" :style="getTabStyle">
        <div
          v-for="(item, index) in multiTags"
          :ref="'dynamic' + index"
          :key="index"
          :class="[
            'scroll-item is-closable',
            linkIsActive(item),
            showModel === 'chrome' && 'chrome-item',
            isFixedTag(item) && 'fixed-tag'
          ]"
          @contextmenu.prevent="openMenu(item, $event)"
          @mouseenter.prevent="onMouseenter(index)"
          @mouseleave.prevent="onMouseleave(index)"
          @click="tagOnClick(item)"
        >
          <template v-if="showModel !== 'chrome'">
            <span
              class="tag-title dark:text-text_color_primary! dark:hover:text-primary!"
            >
              {{ item.meta.title }}
            </span>
            <span
              v-if="
                isFixedTag(item)
                  ? false
                  : iconIsActive(item, index) ||
                    (index === activeIndex && index !== 0)
              "
              class="el-icon-close"
              @click.stop="deleteMenu(item)"
            >
              <IconifyIconOffline :icon="Close" />
            </span>
            <span
              v-if="showModel !== 'card'"
              :ref="'schedule' + index"
              :class="[scheduleIsActive(item)]"
            />
          </template>
          <div v-else class="chrome-tab">
            <div class="chrome-tab__bg">
              <TagChrome />
            </div>
            <span class="tag-title">
              {{ item.meta.title }}
            </span>
            <span
              v-if="isFixedTag(item) ? false : index !== 0"
              class="chrome-close-btn"
              @click.stop="deleteMenu(item)"
            >
              <IconifyIconOffline :icon="Close" />
            </span>
            <span class="chrome-tab-divider" />
          </div>
        </div>
      </div>
    </div>
    <span v-show="isShowArrow" class="arrow-right">
      <IconifyIconOffline :icon="ArrowRightSLine" @click="handleScroll(-200)" />
    </span>
    <!-- 右鍵選單按鈕 -->
    <transition name="el-zoom-in-top">
      <ul
        v-show="visible"
        ref="contextmenuRef"
        :key="Math.random()"
        :style="getContextMenuStyle"
        class="contextmenu"
      >
        <div
          v-for="(item, key) in tagsViews.slice(0, 6)"
          :key="key"
          style="display: flex; align-items: center"
        >
          <li v-if="item.show" @click="selectTag(key, item)">
            <IconifyIconOffline :icon="item.icon" />
            {{ item.text }}
          </li>
        </div>
      </ul>
    </transition>
    <!-- 右側功能按鈕 -->
    <el-dropdown
      trigger="click"
      placement="bottom-end"
      @command="handleCommand"
    >
      <span class="arrow-down">
        <IconifyIconOffline :icon="ArrowDown" class="dark:text-white" />
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="(item, key) in tagsViews"
            :key="key"
            :command="{ key, item }"
            :divided="item.divided"
            :disabled="item.disabled"
          >
            <IconifyIconOffline :icon="item.icon" />
            {{ item.text }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<style lang="scss" scoped>
@import url("./index.scss");
</style>
