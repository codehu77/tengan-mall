<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import {
  type BaseAttrStandardValueItem,
  type SaleAttrStandardValueItem,
  getBaseAttrStandardValues,
  createBaseAttrStandardValue,
  updateBaseAttrStandardValue,
  getSaleAttrStandardValues,
  createSaleAttrStandardValue,
  updateSaleAttrStandardValue
} from "@/api/productAttr";

defineOptions({
  name: "StandardValueManager"
});

type StandardValueItem = BaseAttrStandardValueItem | SaleAttrStandardValueItem;

interface Props {
  kind: "base" | "sale";
  attrId: number;
  attrName: string;
}

const props = defineProps<Props>();

const loading = ref(false);
const values = ref<Array<StandardValueItem>>([]);
const newLabel = ref("");

/** 拖曳排序用的內部型別，跟 index.vue 的 sortedSaleAttrs 拖曳寫法同一套模式。 */
interface DragNode {
  parent: DragNode | null;
  childNodes: DragNode[];
  data: any;
}

async function loadValues() {
  loading.value = true;
  try {
    const { items } =
      props.kind === "base"
        ? await getBaseAttrStandardValues(props.attrId)
        : await getSaleAttrStandardValues(props.attrId);
    values.value = [...items].sort((a, b) => a.sort - b.sort);
  } finally {
    loading.value = false;
  }
}

/**
 * 跟 wizard.vue「快速產生SKU組合」的候選值輸入同一種手感——輸入後按 Enter 就送出，
 * 不整批重新讀取列表（那樣會整個對話框跳 loading 遮罩，連續輸入好幾筆時會卡頓、輸入框
 * 也會被打斷），改成直接把新建好的這一筆插進畫面上的陣列，輸入框可以連續打字、按 Enter。
 */
async function onAdd() {
  const label = newLabel.value.trim();
  if (!label) {
    message("請輸入聚合值名稱", { type: "warning" });
    return;
  }
  if (values.value.some(v => v.label === label)) {
    message("已存在相同名稱的聚合值", { type: "warning" });
    return;
  }
  const nextSort =
    values.value.length === 0
      ? 0
      : Math.max(...values.value.map(v => v.sort)) + 1;
  try {
    const { id } =
      props.kind === "base"
        ? await createBaseAttrStandardValue(props.attrId, {
            label,
            sort: nextSort
          })
        : await createSaleAttrStandardValue(props.attrId, {
            label,
            sort: nextSort
          });
    values.value.push({
      id,
      attrId: props.attrId,
      label,
      enabled: true,
      sort: nextSort
    });
    newLabel.value = "";
  } catch {
    message("新增失敗", { type: "error" });
  }
}

function updateOne(item: StandardValueItem, patch: Partial<StandardValueItem>) {
  const next = { ...item, ...patch };
  const action =
    props.kind === "base"
      ? updateBaseAttrStandardValue(item.id, {
          label: next.label,
          enabled: next.enabled,
          sort: next.sort
        })
      : updateSaleAttrStandardValue(item.id, {
          label: next.label,
          enabled: next.enabled,
          sort: next.sort
        });
  return action;
}

const editingId = ref<number | null>(null);
const editingLabel = ref("");

function startEdit(item: StandardValueItem) {
  editingId.value = item.id;
  editingLabel.value = item.label;
}

async function confirmEdit(item: StandardValueItem) {
  const label = editingLabel.value.trim();
  if (!label) {
    message("聚合值名稱不能為空", { type: "warning" });
    return;
  }
  await updateOne(item, { label });
  editingId.value = null;
  message("修改成功", { type: "success" });
  loadValues();
}

/** UI 上是「刪除」，實際是軟刪除（enabled=false）——既有綁定的商品不受影響，只是新增/編輯下拉不再列出。 */
function onDelete(item: StandardValueItem) {
  ElMessageBox.confirm(`確定要刪除標準聚合值「${item.label}」嗎？`, "提示", {
    type: "warning"
  }).then(async () => {
    await updateOne(item, { enabled: false });
    message("刪除成功", { type: "success" });
    loadValues();
  });
}

async function onEnable(item: StandardValueItem) {
  await updateOne(item, { enabled: true });
  message("已重新啟用", { type: "success" });
  loadValues();
}

async function onNodeDrop(_draggingNode: DragNode, dropNode: DragNode) {
  const siblings: StandardValueItem[] = (
    dropNode.parent?.childNodes ?? []
  ).map(node => node.data);
  try {
    await Promise.all(
      siblings.map((item, index) => updateOne(item, { sort: index }))
    );
    message("排序已更新", { type: "success" });
  } catch {
    message("排序更新失敗", { type: "error" });
  } finally {
    loadValues();
  }
}

onMounted(() => {
  loadValues();
});
</script>

<template>
  <div v-loading="loading">
    <p class="text-gray-400 text-xs mb-3">
      「{{ attrName }}」的標準聚合值——SPU/SKU 填值時可選填綁定一個，前台進階篩選會依此正規化分桶；停用不會影響已綁定的商品，只是新增/編輯時不再出現在下拉選單。
    </p>

    <div class="flex gap-2 mb-3">
      <el-input
        v-model="newLabel"
        placeholder="輸入標準聚合值，例如「黑」或「6.6 吋 ~ 7 吋」"
        clearable
        @keyup.enter="onAdd"
      />
      <el-button type="primary" @click="onAdd">新增</el-button>
    </div>

    <el-empty
      v-if="values.length === 0"
      description="尚未建立標準聚合值"
      :image-size="60"
    />
    <el-tree
      v-else
      :data="values"
      node-key="id"
      draggable
      :allow-drop="(_d: any, _n: any, type: string) => type !== 'inner'"
      @node-drop="onNodeDrop"
    >
      <template #default="{ data }">
        <div class="flex items-center justify-between w-full pr-4 py-1">
          <div class="flex items-center gap-2 flex-1">
            <el-input
              v-if="editingId === data.id"
              v-model="editingLabel"
              size="small"
              style="width: 200px"
              @keyup.enter="confirmEdit(data)"
            />
            <span v-else :class="{ 'text-gray-400': !data.enabled }">
              {{ data.label }}
            </span>
            <el-tag v-if="!data.enabled" size="small" type="info" effect="plain">
              已停用
            </el-tag>
          </div>
          <div>
            <template v-if="editingId === data.id">
              <el-button
                link
                type="primary"
                size="small"
                @click.stop="confirmEdit(data)"
              >
                確定
              </el-button>
              <el-button
                link
                size="small"
                @click.stop="editingId = null"
              >
                取消
              </el-button>
            </template>
            <template v-else>
              <el-button
                link
                type="primary"
                size="small"
                @click.stop="startEdit(data)"
              >
                改名
              </el-button>
              <el-button
                v-if="!data.enabled"
                link
                type="success"
                size="small"
                @click.stop="onEnable(data)"
              >
                重新啟用
              </el-button>
              <el-button
                v-else
                link
                type="danger"
                size="small"
                @click.stop="onDelete(data)"
              >
                刪除
              </el-button>
            </template>
          </div>
        </div>
      </template>
    </el-tree>
  </div>
</template>
