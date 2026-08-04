<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import {
  type MenuTreeItem,
  getMenuList,
  createMenu,
  updateMenu,
  deleteMenu
} from "@/api/menu";
import menuForm from "./form.vue";

defineOptions({
  name: "SystemMenu"
});

const loading = ref(true);
const dataList = ref<Array<MenuTreeItem>>([]);

const menuTypeLabel: Record<number, string> = {
  1: "目錄",
  2: "選單",
  3: "按鈕"
};

async function onSearch() {
  loading.value = true;
  const { items } = await getMenuList();
  dataList.value = items;
  loading.value = false;
}

const formRef = ref();

/**
 * 目錄底下只能放選單、選單底下只能放按鈕、頂層只能是目錄——每個新增情境下合法的類型只有
 * 一種（後端 CreateMenuService.validateHierarchy 也是同一條規則），與其讓使用者自己選、
 * 選錯了才被 400 擋下來，不如直接依情境算出來、鎖死不給選。
 */
function nextMenuType(parentType?: number): number {
  if (parentType === 1) return 2; // 目錄 -> 選單
  if (parentType === 2) return 3; // 選單 -> 按鈕
  return 1; // 無上層節點 -> 只能是目錄
}

function openMenuDialog(
  mode: "create" | "edit",
  parent?: MenuTreeItem,
  row?: MenuTreeItem
) {
  const formInline = {
    parentId: row ? row.parentId : (parent?.id ?? null),
    menuType: mode === "create" ? nextMenuType(parent?.menuType) : (row?.menuType ?? 2),
    title: row?.title ?? "",
    path: row?.path ?? "",
    component: row?.component ?? "",
    routeName: row?.routeName ?? "",
    icon: row?.icon ?? "",
    permissionCode: row?.permissionCode ?? "",
    sortOrder: row?.sortOrder ?? 0
  };

  addDialog({
    title: mode === "create" ? "新增選單節點" : "編輯選單節點",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(menuForm, {
        ref: formRef,
        mode,
        parentTitle: parent?.title ?? "（無，頂層節點）",
        formInline
      }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = formRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const action =
          mode === "create"
            ? createMenu(formInline)
            : updateMenu(row!.id, {
                title: formInline.title,
                path: formInline.path,
                component: formInline.component,
                routeName: formInline.routeName,
                icon: formInline.icon,
                permissionCode: formInline.permissionCode,
                sortOrder: formInline.sortOrder
              });
        action
          .then(() => {
            message(mode === "create" ? "新增成功" : "修改成功", {
              type: "success"
            });
            done();
            onSearch();
          })
          .catch(() => closeLoading());
      });
    }
  });
}

function onDelete(row: MenuTreeItem) {
  ElMessageBox.confirm(`確定要刪除「${row.title}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    deleteMenu(row.id)
      .then(() => {
        message("刪除成功", { type: "success" });
        onSearch();
      })
      .catch((error: any) => {
        message(error?.response?.data?.message ?? "刪除失敗，底下可能還有子節點", {
          type: "error"
        });
      });
  });
}

/**
 * el-tree 拖放事件實際傳入的是內部 model/Node 類別（有 .parent/.childNodes/.data），
 * 跟 element-plus 頂層匯出的 TreeNode 型別是兩回事，這裡用局部型別只描述用得到的欄位，
 * 不硬套內部 class 的完整型別（版本間內部結構容易變動）。
 */
interface DragNode {
  parent: DragNode | null;
  childNodes: DragNode[];
  data: any;
}

/** 只准同一個上層節點底下的兄弟節點互相調整順序，不准拖進別的節點底下（改父節點）。 */
function allowDrop(draggingNode: DragNode, dropNode: DragNode, type: string) {
  if (type === "inner") return false;
  return draggingNode.parent === dropNode.parent;
}

/**
 * el-tree 拖放後，內部資料已經是新順序——把同一個父節點底下所有兄弟節點的 sortOrder
 * 依照目前順序整批寫回後端（PUT 是整包欄位替換，所以其餘欄位照抄原值，只改 sortOrder）。
 *
 * 這裡故意用 dropNode.parent 而不是 draggingNode.parent：el-tree 內部放開滑鼠時會先把
 * draggingNode 從樹上 remove() 掉，再用一份新的資料副本插進 dropNode 旁邊，但 node-drop
 * 事件丟出來的 draggingNode 還是那個「已經被 remove 掉」的舊物件參照，此時它的 .parent
 * 已經失效（讀出來是空陣列），送出去的 Promise.all([]) 會直接秒過、完全沒打到後端，
 * 畫面卻照樣顯示「更新成功」。dropNode 沒有被 remove，它的 .parent 才是拖放後真正的
 * 那個父節點，讀出來的 childNodes 才是新順序。
 */
async function onNodeDrop(draggingNode: DragNode, dropNode: DragNode) {
  const siblings: MenuTreeItem[] = (dropNode.parent?.childNodes ?? []).map(
    node => node.data
  );
  try {
    await Promise.all(
      siblings.map((item, index) =>
        updateMenu(item.id, {
          title: item.title,
          path: item.path,
          component: item.component,
          routeName: item.routeName,
          icon: item.icon,
          permissionCode: item.permissionCode,
          sortOrder: index
        })
      )
    );
    message("排序已更新", { type: "success" });
    onSearch();
  } catch {
    message("排序更新失敗", { type: "error" });
    onSearch();
  }
}

onMounted(() => {
  onSearch();
});
</script>

<template>
  <div class="main">
    <div class="flex justify-between items-center mb-2">
      <p class="font-bold">選單樹（可直接拖曳調整同層排序）</p>
      <div>
        <el-button @click="onSearch">重新整理</el-button>
        <el-button type="primary" @click="openMenuDialog('create')">
          新增根目錄
        </el-button>
      </div>
    </div>

    <el-tree
      v-loading="loading"
      :data="dataList"
      node-key="id"
      draggable
      default-expand-all
      :allow-drop="allowDrop"
      :props="{ label: 'title', children: 'children' }"
      @node-drop="onNodeDrop"
    >
      <template #default="{ data }">
        <div class="flex items-center justify-between w-full pr-4">
          <div class="flex items-center gap-2">
            <span>{{ data.title }}</span>
            <el-tag size="small">{{ menuTypeLabel[data.menuType] }}</el-tag>
            <el-tag
              size="small"
              :type="data.status === 1 ? 'success' : 'info'"
              effect="plain"
            >
              {{ data.status === 1 ? "啟用" : "停用" }}
            </el-tag>
            <span v-if="data.path" class="text-gray-400 text-xs">{{ data.path }}</span>
            <span v-if="data.permissionCode" class="text-gray-400 text-xs">
              {{ data.permissionCode }}
            </span>
          </div>
          <div>
            <el-button
              v-if="data.menuType !== 3"
              link
              type="primary"
              size="small"
              @click.stop="openMenuDialog('create', data)"
            >
              新增子項
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click.stop="openMenuDialog('edit', undefined, data)"
            >
              編輯
            </el-button>
            <el-button link type="danger" size="small" @click.stop="onDelete(data)">
              刪除
            </el-button>
          </div>
        </div>
      </template>
    </el-tree>
  </div>
</template>
