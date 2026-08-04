<script setup lang="ts">
import { h, ref, computed, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { type CategoryTreeItem, getCategoryTree } from "@/api/productCategory";
import {
  type BaseAttrGroupItem,
  type BaseAttrItem,
  type SaleAttrItem,
  getBaseAttrGroups,
  createBaseAttrGroup,
  updateBaseAttrGroup,
  deleteBaseAttrGroup,
  getBaseAttrs,
  createBaseAttr,
  updateBaseAttr,
  deleteBaseAttr,
  getSaleAttrs,
  createSaleAttr,
  updateSaleAttr,
  deleteSaleAttr,
  getCategoriesWithAttrs
} from "@/api/productAttr";
import baseAttrGroupForm from "./baseAttrGroupForm.vue";
import baseAttrForm from "./baseAttrForm.vue";
import saleAttrForm from "./saleAttrForm.vue";

defineOptions({
  name: "ProductAttr"
});

const treeLoading = ref(true);
const categoryTree = ref<Array<CategoryTreeItem>>([]);
const categoriesWithAttrs = ref<Set<number>>(new Set());
const selectedCategory = ref<CategoryTreeItem | null>(null);

const panelLoading = ref(false);
const baseAttrGroups = ref<Array<BaseAttrGroupItem>>([]);
const baseAttrs = ref<Array<BaseAttrItem>>([]);
const saleAttrs = ref<Array<SaleAttrItem>>([]);

/** BASE 樹固定兩層：第一層是分組，第二層是分組底下的屬性。node-key 加前綴避免兩張表的 id 撞號。 */
type BaseTreeNode =
  | {
      type: "group";
      id: string;
      raw: BaseAttrGroupItem;
      children: Array<BaseTreeNode>;
    }
  | { type: "attr"; id: string; raw: BaseAttrItem };

const baseTreeData = computed<Array<BaseTreeNode>>(() => {
  return [...baseAttrGroups.value]
    .sort((a, b) => a.sort - b.sort)
    .map(group => ({
      type: "group" as const,
      id: `group-${group.id}`,
      raw: group,
      children: baseAttrs.value
        .filter(attr => attr.attrGroupId === group.id)
        .sort((a, b) => a.sort - b.sort)
        .map(attr => ({
          type: "attr" as const,
          id: `attr-${attr.id}`,
          raw: attr
        }))
    }));
});

const sortedSaleAttrs = computed(() =>
  [...saleAttrs.value].sort((a, b) => a.sort - b.sort)
);

async function loadCategoryTree() {
  treeLoading.value = true;
  const { items } = await getCategoryTree();
  categoryTree.value = items;
  treeLoading.value = false;
}

async function loadCategoriesWithAttrs() {
  const { categoryIds } = await getCategoriesWithAttrs();
  categoriesWithAttrs.value = new Set(categoryIds);
}

async function loadBaseAttrGroups() {
  if (!selectedCategory.value) return;
  const { items } = await getBaseAttrGroups(selectedCategory.value.id);
  baseAttrGroups.value = items;
}

async function loadBaseAttrs() {
  if (!selectedCategory.value) return;
  const { items } = await getBaseAttrs(selectedCategory.value.id);
  baseAttrs.value = items;
}

async function loadSaleAttrs() {
  if (!selectedCategory.value) return;
  const { items } = await getSaleAttrs(selectedCategory.value.id);
  saleAttrs.value = items;
}

async function selectCategory(data: CategoryTreeItem) {
  selectedCategory.value = data;
  panelLoading.value = true;
  await Promise.all([loadBaseAttrGroups(), loadBaseAttrs(), loadSaleAttrs()]);
  panelLoading.value = false;
}

/** 屬性新增/修改/刪除都可能讓左側「尚未設定屬性」提醒消失或出現，一起刷新。 */
async function reloadAfterMutation() {
  await Promise.all([
    loadBaseAttrGroups(),
    loadBaseAttrs(),
    loadSaleAttrs(),
    loadCategoriesWithAttrs()
  ]);
}

function onNodeClick(data: CategoryTreeItem, node: any) {
  if (node.level !== 3) return;
  selectCategory(data);
}

const groupFormRef = ref();

function openGroupDialog(mode: "create" | "edit", row?: BaseAttrGroupItem) {
  const formInline = {
    name: row?.name ?? "",
    sort: row?.sort ?? 0
  };
  addDialog({
    title: mode === "create" ? "新增屬性分組" : "編輯屬性分組",
    width: "30%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(baseAttrGroupForm, { ref: groupFormRef, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = groupFormRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const action =
          mode === "create"
            ? createBaseAttrGroup({
                categoryId: selectedCategory.value!.id,
                name: formInline.name,
                sort: formInline.sort
              })
            : updateBaseAttrGroup(row!.id, {
                name: formInline.name,
                sort: formInline.sort
              });
        action
          .then(() => {
            message(mode === "create" ? "新增成功" : "修改成功", {
              type: "success"
            });
            done();
            reloadAfterMutation();
          })
          .catch(() => closeLoading());
      });
    }
  });
}

function onDeleteGroup(row: BaseAttrGroupItem) {
  ElMessageBox.confirm(`確定要刪除屬性分組「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    deleteBaseAttrGroup(row.id)
      .then(() => {
        message("刪除成功", { type: "success" });
        reloadAfterMutation();
      })
      .catch((error: any) => {
        message(
          error?.response?.data?.message ?? "刪除失敗，底下可能還有屬性",
          {
            type: "error"
          }
        );
      });
  });
}

const attrFormRef = ref();

function openAttrDialog(
  mode: "create" | "edit",
  group?: BaseAttrGroupItem,
  row?: BaseAttrItem
) {
  const attrGroupId = row?.attrGroupId ?? group!.id;
  const groupName =
    mode === "edit"
      ? (baseAttrGroups.value.find(g => g.id === row!.attrGroupId)?.name ?? "")
      : group!.name;
  const formInline = {
    name: row?.name ?? "",
    searchable: row?.searchable ?? false,
    sort: row?.sort ?? 0
  };
  addDialog({
    title: mode === "create" ? "新增規格屬性" : "編輯規格屬性",
    width: "30%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(baseAttrForm, { ref: attrFormRef, groupName, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = attrFormRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const action =
          mode === "create"
            ? createBaseAttr({
                categoryId: selectedCategory.value!.id,
                attrGroupId,
                name: formInline.name,
                searchable: formInline.searchable,
                sort: formInline.sort
              })
            : updateBaseAttr(row!.id, {
                attrGroupId: row!.attrGroupId,
                name: formInline.name,
                searchable: formInline.searchable,
                sort: formInline.sort
              });
        action
          .then(() => {
            message(mode === "create" ? "新增成功" : "修改成功", {
              type: "success"
            });
            done();
            reloadAfterMutation();
          })
          .catch(() => closeLoading());
      });
    }
  });
}

function onDeleteAttr(row: BaseAttrItem) {
  ElMessageBox.confirm(`確定要刪除規格屬性「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    deleteBaseAttr(row.id)
      .then(() => {
        message("刪除成功", { type: "success" });
        reloadAfterMutation();
      })
      .catch(() => {
        message("刪除失敗", { type: "error" });
      });
  });
}

const saleAttrFormRef = ref();

function openSaleAttrDialog(mode: "create" | "edit", row?: SaleAttrItem) {
  const formInline = {
    name: row?.name ?? "",
    searchable: row?.searchable ?? false,
    sort: row?.sort ?? 0
  };
  addDialog({
    title: mode === "create" ? "新增銷售屬性" : "編輯銷售屬性",
    width: "30%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(saleAttrForm, { ref: saleAttrFormRef, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = saleAttrFormRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const action =
          mode === "create"
            ? createSaleAttr({
                categoryId: selectedCategory.value!.id,
                name: formInline.name,
                searchable: formInline.searchable,
                sort: formInline.sort
              })
            : updateSaleAttr(row!.id, {
                name: formInline.name,
                searchable: formInline.searchable,
                sort: formInline.sort
              });
        action
          .then(() => {
            message(mode === "create" ? "新增成功" : "修改成功", {
              type: "success"
            });
            done();
            reloadAfterMutation();
          })
          .catch(() => closeLoading());
      });
    }
  });
}

function onDeleteSaleAttr(row: SaleAttrItem) {
  ElMessageBox.confirm(`確定要刪除銷售屬性「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    deleteSaleAttr(row.id)
      .then(() => {
        message("刪除成功", { type: "success" });
        reloadAfterMutation();
      })
      .catch(() => {
        message("刪除失敗", { type: "error" });
      });
  });
}

/**
 * el-tree 拖放事件實際傳入的是內部 model/Node 類別，跟 element-plus 頂層匯出的 TreeNode
 * 型別是兩回事，這裡用局部型別只描述用得到的欄位（見 products/category/index.vue 同樣的寫法）。
 */
interface DragNode {
  parent: DragNode | null;
  childNodes: DragNode[];
  data: any;
}

/**
 * BASE 樹的分組只能跟分組同層互換，不能被丟進另一個分組（沒有巢狀分組這種東西）。
 * 屬性則允許跨分組拖曳（丟進某個分組節點本身＝inner，或丟到任一屬性的前/後＝換到那顆屬性所屬的分組），
 * 反映「屬性樣板本來就是同一層級的東西，只是用分組做顯示分類」這件事。
 */
function allowBaseDrop(
  draggingNode: DragNode,
  dropNode: DragNode,
  type: string
) {
  if (draggingNode.data.type === "group") {
    return dropNode.data.type === "group" && type !== "inner";
  }
  if (dropNode.data.type === "group") {
    return type === "inner";
  }
  return type !== "inner";
}

async function onBaseNodeDrop(
  draggingNode: DragNode,
  dropNode: DragNode,
  dropType: string
) {
  if (draggingNode.data.type === "group") {
    const siblings: BaseTreeNode[] = (dropNode.parent?.childNodes ?? []).map(
      node => node.data
    );
    if (siblings.length === 0) return;
    try {
      await Promise.all(
        siblings.map((item, index) => {
          const group = (item as { type: "group"; raw: BaseAttrGroupItem }).raw;
          return updateBaseAttrGroup(group.id, {
            name: group.name,
            sort: index
          });
        })
      );
      message("排序已更新", { type: "success" });
    } catch {
      message("排序更新失敗", { type: "error" });
    } finally {
      await Promise.all([loadBaseAttrGroups(), loadBaseAttrs()]);
    }
    return;
  }

  // 屬性拖放：inner 落在分組節點本身，新父層就是 dropNode；before/after 落在另一顆屬性旁邊，新父層是那顆屬性的父分組。
  const targetParentNode = dropType === "inner" ? dropNode : dropNode.parent;
  if (!targetParentNode || targetParentNode.data.type !== "group") return;
  const targetGroup = (
    targetParentNode.data as { type: "group"; raw: BaseAttrGroupItem }
  ).raw;
  const siblings: BaseTreeNode[] = targetParentNode.childNodes.map(
    node => node.data
  );

  try {
    await Promise.all(
      siblings.map((item, index) => {
        const attr = (item as { type: "attr"; raw: BaseAttrItem }).raw;
        return updateBaseAttr(attr.id, {
          attrGroupId: targetGroup.id,
          name: attr.name,
          searchable: attr.searchable,
          sort: index
        });
      })
    );
    message("排序已更新", { type: "success" });
  } catch {
    message("排序更新失敗", { type: "error" });
  } finally {
    await Promise.all([loadBaseAttrGroups(), loadBaseAttrs()]);
  }
}

function allowSaleDrop(
  _draggingNode: DragNode,
  _dropNode: DragNode,
  type: string
) {
  return type !== "inner";
}

async function onSaleNodeDrop(_draggingNode: DragNode, dropNode: DragNode) {
  const siblings: SaleAttrItem[] = (dropNode.parent?.childNodes ?? []).map(
    node => node.data
  );
  try {
    await Promise.all(
      siblings.map((item, index) =>
        updateSaleAttr(item.id, {
          name: item.name,
          searchable: item.searchable,
          sort: index
        })
      )
    );
    message("排序已更新", { type: "success" });
    loadSaleAttrs();
  } catch {
    message("排序更新失敗", { type: "error" });
    loadSaleAttrs();
  }
}

onMounted(() => {
  loadCategoryTree();
  loadCategoriesWithAttrs();
});
</script>

<template>
  <div class="main flex gap-4">
    <div class="w-72 shrink-0 border-r pr-4">
      <div class="flex justify-between items-center mb-2">
        <p class="font-bold">商品分類</p>
        <el-button link type="primary" size="small" @click="loadCategoryTree">
          重新整理
        </el-button>
      </div>
      <p class="text-gray-400 text-xs mb-2">請選擇第三層分類設定屬性</p>
      <el-tree
        v-loading="treeLoading"
        :data="categoryTree"
        node-key="id"
        default-expand-all
        :props="{ label: 'name', children: 'children' }"
        @node-click="onNodeClick"
      >
        <template #default="{ node, data }">
          <div
            class="flex items-center gap-2"
            :class="{
              'font-bold text-[var(--el-color-primary)]':
                node.level === 3 && selectedCategory?.id === data.id,
              'text-gray-400': node.level !== 3
            }"
          >
            <span>{{ data.name }}</span>
            <el-tag
              v-if="node.level === 3 && !categoriesWithAttrs.has(data.id)"
              size="small"
              type="warning"
              effect="plain"
            >
              尚未設定屬性
            </el-tag>
          </div>
        </template>
      </el-tree>
    </div>

    <div v-loading="panelLoading" class="flex-1 min-w-0">
      <el-empty
        v-if="!selectedCategory"
        description="請先在左側選擇第三層分類"
      />
      <div v-else>
        <div class="flex justify-between items-center mb-2">
          <p class="font-bold">
            規格屬性
            <span class="text-gray-400 text-xs font-normal ml-1"
              >（分組可拖曳排序，屬性可拖曳排序或改掛到其他分組）</span
            >
          </p>
          <el-button
            type="primary"
            size="small"
            @click="openGroupDialog('create')"
          >
            新增屬性分組
          </el-button>
        </div>
        <el-empty
          v-if="baseTreeData.length === 0"
          description="尚未建立屬性分組"
          :image-size="80"
        />
        <el-tree
          v-else
          :data="baseTreeData"
          node-key="id"
          draggable
          default-expand-all
          :allow-drop="allowBaseDrop"
          @node-drop="onBaseNodeDrop"
        >
          <template #default="{ data }">
            <div class="flex items-center justify-between w-full pr-4">
              <div class="flex items-center gap-2">
                <span>{{ data.raw.name }}</span>
                <el-tag
                  v-if="data.type === 'attr' && data.raw.searchable"
                  size="small"
                  type="success"
                  effect="plain"
                >
                  可搜尋
                </el-tag>
              </div>
              <div v-if="data.type === 'group'">
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click.stop="openAttrDialog('create', data.raw)"
                >
                  新增屬性
                </el-button>
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click.stop="openGroupDialog('edit', data.raw)"
                >
                  編輯
                </el-button>
                <el-button
                  link
                  type="danger"
                  size="small"
                  @click.stop="onDeleteGroup(data.raw)"
                >
                  刪除
                </el-button>
              </div>
              <div v-else>
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click.stop="openAttrDialog('edit', undefined, data.raw)"
                >
                  編輯
                </el-button>
                <el-button
                  link
                  type="danger"
                  size="small"
                  @click.stop="onDeleteAttr(data.raw)"
                >
                  刪除
                </el-button>
              </div>
            </div>
          </template>
        </el-tree>

        <el-divider />

        <div class="flex justify-between items-center mb-2">
          <p class="font-bold">
            銷售屬性
            <span class="text-gray-400 text-xs font-normal ml-1"
              >（可拖曳調整排序）</span
            >
          </p>
          <el-button
            type="primary"
            size="small"
            @click="openSaleAttrDialog('create')"
          >
            新增銷售屬性
          </el-button>
        </div>
        <el-empty
          v-if="sortedSaleAttrs.length === 0"
          description="尚未建立銷售屬性"
          :image-size="80"
        />
        <el-tree
          v-else
          :data="sortedSaleAttrs"
          node-key="id"
          draggable
          :allow-drop="allowSaleDrop"
          @node-drop="onSaleNodeDrop"
        >
          <template #default="{ data }">
            <div class="flex items-center justify-between w-full pr-4">
              <div class="flex items-center gap-2">
                <span>{{ data.name }}</span>
                <el-tag
                  v-if="data.searchable"
                  size="small"
                  type="success"
                  effect="plain"
                >
                  可搜尋
                </el-tag>
              </div>
              <div>
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click.stop="openSaleAttrDialog('edit', data)"
                >
                  編輯
                </el-button>
                <el-button
                  link
                  type="danger"
                  size="small"
                  @click.stop="onDeleteSaleAttr(data)"
                >
                  刪除
                </el-button>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </div>
  </div>
</template>
