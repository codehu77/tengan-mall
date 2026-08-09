<script setup lang="ts">
import { ref, reactive } from "vue";
import type { FormInstance } from "element-plus";
import type { WarehouseItem } from "@/api/inventoryWarehouse";
import {
  type SpuSummaryItem,
  type SkuDetailItem,
  searchSpus,
  getSpuDetail
} from "@/api/productSpu";

defineOptions({
  name: "PurchaseOrderCreateForm"
});

interface ItemRow {
  spuId: number | undefined;
  skuId: number | undefined;
  orderedQty: number;
  spuOptions: Array<SpuSummaryItem>;
  spuLoading: boolean;
  skuOptions: Array<SkuDetailItem>;
  skuLoading: boolean;
}

interface FormItemProps {
  wareId: number | undefined;
  supplierName: string;
}

interface FormProps {
  formInline: FormItemProps;
  warehouses: Array<WarehouseItem>;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({ wareId: undefined, supplierName: "" }),
  warehouses: () => []
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

function newRow(): ItemRow {
  return {
    spuId: undefined,
    skuId: undefined,
    orderedQty: 1,
    spuOptions: [],
    spuLoading: false,
    skuOptions: [],
    skuLoading: false
  };
}

const rows = reactive<Array<ItemRow>>([newRow()]);

function addRow() {
  rows.push(newRow());
}

function removeRow(index: number) {
  rows.splice(index, 1);
}

async function onSearchSpu(row: ItemRow, keyword: string) {
  if (!keyword) {
    row.spuOptions = [];
    return;
  }
  row.spuLoading = true;
  const { items } = await searchSpus({ name: keyword, pageNum: 1, pageSize: 20 });
  row.spuOptions = items;
  row.spuLoading = false;
}

async function onSpuChange(row: ItemRow) {
  row.skuId = undefined;
  row.skuOptions = [];
  if (!row.spuId) {
    return;
  }
  row.skuLoading = true;
  const detail = await getSpuDetail(row.spuId);
  row.skuOptions = detail.skus;
  row.skuLoading = false;
}

function skuLabel(sku: SkuDetailItem) {
  return `${sku.name}（NT$${sku.price}）`;
}

function getRef() {
  return ruleFormRef.value;
}

/** 只帶已經選好規格的列，供 index.vue 建單前組裝 payload。 */
function getItems() {
  return rows
    .filter(row => row.skuId)
    .map(row => ({ skuId: row.skuId as number, orderedQty: row.orderedQty }));
}

defineExpose({ getRef, getItems });
</script>

<template>
  <el-form ref="ruleFormRef" :model="newFormInline" label-width="96px">
    <el-form-item label="倉庫" prop="wareId">
      <el-select v-model="newFormInline.wareId" placeholder="請選擇倉庫">
        <el-option
          v-for="w in warehouses"
          :key="w.id"
          :label="w.name"
          :value="w.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="供應商">
      <el-input
        v-model="newFormInline.supplierName"
        placeholder="選填，自由文字"
        clearable
      />
    </el-form-item>
    <el-form-item label="採購項目">
      <div class="w-full">
        <div
          v-for="(row, index) in rows"
          :key="index"
          class="flex items-center gap-2 mb-2"
        >
          <el-select
            v-model="row.spuId"
            filterable
            remote
            reserve-keyword
            placeholder="輸入商品名稱搜尋"
            :remote-method="(kw: string) => onSearchSpu(row, kw)"
            :loading="row.spuLoading"
            style="width: 200px"
            @change="() => onSpuChange(row)"
          >
            <el-option
              v-for="spu in row.spuOptions"
              :key="spu.id"
              :label="spu.name"
              :value="spu.id"
            />
          </el-select>
          <el-select
            v-model="row.skuId"
            placeholder="請先選擇商品"
            :loading="row.skuLoading"
            :disabled="!row.spuId"
            style="width: 200px"
          >
            <el-option
              v-for="sku in row.skuOptions"
              :key="sku.id"
              :label="skuLabel(sku)"
              :value="sku.id"
            />
          </el-select>
          <el-input-number v-model="row.orderedQty" :min="1" />
          <el-button
            link
            type="danger"
            :disabled="rows.length === 1"
            @click="removeRow(index)"
          >
            移除
          </el-button>
        </div>
        <el-button type="primary" link @click="addRow">
          + 新增一列
        </el-button>
      </div>
    </el-form-item>
  </el-form>
</template>
