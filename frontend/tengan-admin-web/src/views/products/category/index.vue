<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import {
  type CategoryTreeItem,
  getCategoryTree,
  createCategory,
  updateCategory,
  deleteCategory,
  showCategory,
  hideCategory
} from "@/api/productCategory";
import categoryForm from "./form.vue";

defineOptions({
  name: "ProductCategory"
});

const loading = ref(true);
const dataList = ref<Array<CategoryTreeItem>>([]);

async function onSearch() {
  loading.value = true;
  const { items } = await getCategoryTree();
  dataList.value = items;
  loading.value = false;
}

const formRef = ref();

/**
 * 分類最多三層（level 1~3），level 由後端依 parentId 自動推算，前端不用也不能傳。
 * 第四層會被後端 409 擋下（CategoryLevelLimitExceededException），不在前端預先算深度擋——
 * 跟 system/menu 表單同樣的「後端擋一次是最後防線」精神，這裡連前端擋都不做，因為判斷
 * 「這是不是葉節點」本來就要看資料庫當下的真實深度，前端沒有比後端更可靠的依據。
 */
function openCategoryDialog(
  mode: "create" | "edit",
  parent?: CategoryTreeItem,
  row?: CategoryTreeItem
) {
  const formInline = {
    parentId: row ? null : (parent?.id ?? null),
    name: row?.name ?? "",
    icon: row?.icon ?? "",
    sort: row?.sort ?? 0
  };

  addDialog({
    title: mode === "create" ? "新增分類" : "編輯分類",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(categoryForm, {
        ref: formRef,
        mode,
        parentTitle: parent?.name ?? "（無，頂層分類）",
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
            ? createCategory({
                parentId: formInline.parentId,
                name: formInline.name,
                icon: formInline.icon,
                sort: formInline.sort
              })
            : updateCategory(row!.id, {
                name: formInline.name,
                icon: formInline.icon,
                sort: formInline.sort
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

function onDelete(row: CategoryTreeItem) {
  ElMessageBox.confirm(`確定要刪除「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    deleteCategory(row.id)
      .then(() => {
        message("刪除成功", { type: "success" });
        onSearch();
      })
      .catch((error: any) => {
        message(
          error?.response?.data?.message ?? "刪除失敗，底下可能還有子分類",
          { type: "error" }
        );
      });
  });
}

function onToggleStatus(row: CategoryTreeItem) {
  const nextVisible = row.status !== 1;
  ElMessageBox.confirm(
    `確定要${nextVisible ? "顯示" : "隱藏"}分類「${row.name}」嗎？`,
    "提示",
    { type: "warning" }
  ).then(() => {
    const action = nextVisible ? showCategory(row.id) : hideCategory(row.id);
    action.then(() => {
      message("操作成功", { type: "success" });
      onSearch();
    });
  });
}

/**
 * el-tree 拖放事件實際傳入的是內部 model/Node 類別（有 .parent/.childNodes/.data），
 * 跟 element-plus 頂層匯出的 TreeNode 型別是兩回事，這裡用局部型別只描述用得到的欄位
 * （見 system/menu/index.vue 同樣的寫法跟同一段說明）。
 */
interface DragNode {
  parent: DragNode | null;
  childNodes: DragNode[];
  data: any;
}

/** 只准同一個上層分類底下的兄弟節點互相調整順序，不准拖進別的分類底下（改父分類）。 */
function allowDrop(draggingNode: DragNode, dropNode: DragNode, type: string) {
  if (type === "inner") return false;
  return draggingNode.parent === dropNode.parent;
}

/**
 * el-tree 拖放後，內部資料已經是新順序——把同一個父分類底下所有兄弟節點的 sort 依照目前
 * 順序整批寫回後端（PUT 是整包欄位替換，所以其餘欄位照抄原值，只改 sort）。用 dropNode.parent
 * 不是 draggingNode.parent 的理由跟 system/menu 完全一樣：拖放後 draggingNode 已經被
 * el-tree 內部 remove() 掉，它的 .parent 這時候讀出來是空的，只有 dropNode.parent 才是
 * 拖放後真正的父節點。
 */
async function onNodeDrop(draggingNode: DragNode, dropNode: DragNode) {
  const siblings: CategoryTreeItem[] = (dropNode.parent?.childNodes ?? []).map(
    node => node.data
  );
  try {
    await Promise.all(
      siblings.map((item, index) =>
        updateCategory(item.id, {
          name: item.name,
          icon: item.icon,
          sort: index
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
      <p class="font-bold">商品分類（最多三層，可直接拖曳調整同層排序）</p>
      <div>
        <el-button @click="onSearch">重新整理</el-button>
        <el-button type="primary" @click="openCategoryDialog('create')">
          新增根分類
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
      :props="{ label: 'name', children: 'children' }"
      @node-drop="onNodeDrop"
    >
      <template #default="{ data }">
        <div class="flex items-center justify-between w-full pr-4">
          <div class="flex items-center gap-2">
            <span>{{ data.name }}</span>
            <el-tag
              size="small"
              :type="data.status === 1 ? 'success' : 'info'"
              effect="plain"
            >
              {{ data.status === 1 ? "顯示" : "隱藏" }}
            </el-tag>
            <span v-if="data.icon" class="text-gray-400 text-xs">{{
              data.icon
            }}</span>
          </div>
          <div>
            <el-button
              link
              type="primary"
              size="small"
              @click.stop="openCategoryDialog('create', data)"
            >
              新增子分類
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click.stop="openCategoryDialog('edit', undefined, data)"
            >
              編輯
            </el-button>
            <el-button
              link
              :type="data.status === 1 ? 'info' : 'success'"
              size="small"
              @click.stop="onToggleStatus(data)"
            >
              {{ data.status === 1 ? "隱藏" : "顯示" }}
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
