<script setup lang="ts">
import { ref, computed } from "vue";
import type { MenuTreeItem } from "@/api/menu";

defineOptions({
  name: "RoleMenuForm"
});

interface FormProps {
  menuTree: Array<MenuTreeItem>;
  /** 父層傳進來的可變陣列，勾選變動時原地寫回（splice），不是重新賦值——
   *  跟 form.vue 用同一招：狀態活在父層閉包變數裡，不是只活在這個元件自己的內部狀態，
   *  這樣即使對話框開著期間 el-tree 被重新掛載過一次，使用者勾好的結果也不會被沖掉。 */
  checkedKeys: Array<number>;
}

const props = withDefaults(defineProps<FormProps>(), {
  menuTree: () => [],
  checkedKeys: () => []
});

const treeRef = ref();

/**
 * 用 el-tree 預設的父子連動勾選（沒有 check-strictly）：勾上層會連帶勾滿底下所有子節點，
 * 符合管理員操作直覺——勾「選單」預期連同「新增選單」按鈕權限一起給，不用逐一點開每個按鈕。
 * 只保留部分子節點時，父節點會是半勾（indeterminate）狀態，這裡也要算「有授權」，
 * 所以已勾選 + 半勾選都要送出去，不然「只給部分子權限」這個操作結果會在存檔時整個消失。
 *
 * 每次勾選變動（@check）都立刻同步寫回 props.checkedKeys，不是等使用者按「確定」時才
 * 一次性去讀 el-tree 當下的狀態——後者實測會遇到「顯示更新成功、但實際內容跟修改前一樣」
 * 的情況（見 role/index.vue 選單授權對話框），確定點下去讀到的是初始值，不是使用者剛才
 * 勾選的結果。改成即時同步後，getCheckedKeys() 永遠回傳的是父層那份最新資料，不依賴
 * confirm 當下 el-tree 元件實例是不是還是使用者互動的那一個。
 */
function syncChecked() {
  const tree = treeRef.value;
  if (!tree) return;
  const keys = [...tree.getCheckedKeys(), ...tree.getHalfCheckedKeys()] as number[];
  props.checkedKeys.splice(0, props.checkedKeys.length, ...keys);
}

function getCheckedKeys(): number[] {
  syncChecked();
  return [...props.checkedKeys];
}

/**
 * el-tree 的 default-checked-keys 在父子連動模式下，只要清單裡出現「父節點自己的 id」，
 * 初始化時就會把那個父節點標成已勾選並整層 cascade 下去蓋過所有子孫——不管清單裡其他子節點
 * 是不是只勾了一部分。實測過：儲存的 menuIds 是 [系統管理, 管理員, 新增管理員] 這種
 * 「目錄+其中一個選單+其按鈕」的組合，重新打開時因為清單裡有「系統管理」這個最上層目錄的 id，
 * el-tree 直接把整棵樹全部勾滿。
 *
 * 修法：default-checked-keys 只餵「樹狀結構裡真正的葉節點」（沒有 children 的節點，也就是
 * 按鈕，或沒有掛按鈕的選單），非葉節點的勾選/半勾選狀態全部交給 el-tree 自己由下往上推導，
 * 不要把父節點的 id 也塞進去——這樣不管原始清單裡有沒有混到父節點的 id 都不會被強制 cascade，
 * 呈現出來的永遠是跟葉節點一致的正確狀態。
 */
const leafCheckedKeys = computed(() => {
  const checkedSet = new Set(props.checkedKeys);
  const result: number[] = [];
  function walk(nodes: Array<MenuTreeItem>) {
    for (const node of nodes) {
      const children = node.children ?? [];
      if (children.length > 0) {
        walk(children);
      } else if (checkedSet.has(node.id)) {
        result.push(node.id);
      }
    }
  }
  walk(props.menuTree);
  return result;
});

defineExpose({ getCheckedKeys });
</script>

<template>
  <el-tree
    ref="treeRef"
    :data="menuTree"
    node-key="id"
    show-checkbox
    default-expand-all
    :default-checked-keys="leafCheckedKeys"
    :props="{ label: 'title', children: 'children' }"
    style="max-height: 60vh; overflow-y: auto"
    @check="syncChecked"
  />
</template>
