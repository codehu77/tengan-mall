<script setup lang="ts">
import { h, ref, reactive, onMounted } from "vue";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type SkuStockItem,
  getSkuStockList,
  createStock,
  adjustStock
} from "@/api/inventoryStock";
import { type WarehouseItem, getWarehouseList } from "@/api/inventoryWarehouse";
import adjustForm from "./adjustForm.vue";
import createForm from "./createForm.vue";

defineOptions({
  name: "InventoryStock"
});

const loading = ref(true);
const dataList = ref<Array<SkuStockItem>>([]);
const warehouses = ref<Array<WarehouseItem>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const searchForm = reactive<{ wareId?: number; keyword?: number }>({
  wareId: undefined,
  keyword: undefined
});

const columns: TableColumns[] = [
  { label: "倉庫", prop: "wareId", minWidth: 100, formatter: row => `${warehouseName(row.wareId)}` },
  { label: "SKU ID", prop: "skuId", minWidth: 100 },
  { label: "圖片", width: 80, slot: "image" },
  { label: "SKU名稱", prop: "skuName", minWidth: 160, formatter: row => row.skuName ?? "—" },
  { label: "庫存", prop: "stock", minWidth: 100 },
  { label: "已鎖定", prop: "lockedStock", minWidth: 100 },
  { label: "可用", minWidth: 100, formatter: row => `${row.stock - row.lockedStock}` },
  { label: "操作", fixed: "right", width: 100, slot: "operation" }
];

function warehouseName(wareId: number) {
  return warehouses.value.find(w => w.id === wareId)?.name ?? wareId;
}

async function onSearch() {
  loading.value = true;
  const { items, total } = await getSkuStockList({
    wareId: searchForm.wareId,
    keyword: searchForm.keyword,
    page: pagination.currentPage,
    pageSize: pagination.pageSize
  });
  dataList.value = items;
  pagination.total = total;
  loading.value = false;
}

function onReset() {
  searchForm.wareId = undefined;
  searchForm.keyword = undefined;
  pagination.currentPage = 1;
  onSearch();
}

function onPageSizeChange(size: number) {
  pagination.pageSize = size;
  onSearch();
}

function onPageCurrentChange(page: number) {
  pagination.currentPage = page;
  onSearch();
}

function showError(error: any, fallback: string) {
  message(error?.response?.data?.message ?? fallback, { type: "error" });
}

const createFormRef = ref();

function openCreateDialog() {
  const formInline: { skuId: number | undefined; wareId: number | undefined; initialStock: number } = {
    skuId: undefined,
    wareId: undefined,
    initialStock: 0
  };

  addDialog({
    title: "新增庫存",
    width: "32%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(createForm, { ref: createFormRef, formInline, warehouses: warehouses.value }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = createFormRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        createStock({
          skuId: formInline.skuId!,
          wareId: formInline.wareId!,
          initialStock: formInline.initialStock
        })
          .then(() => {
            message("新增成功", { type: "success" });
            done();
            onSearch();
          })
          .catch(error => {
            showError(error, "新增失敗，可能這顆 SKU 在這個倉庫已經有庫存列了");
            closeLoading();
          });
      });
    }
  });
}

const adjustFormRef = ref();

function openAdjustDialog(row: SkuStockItem) {
  const formInline = { delta: 0, reason: "" };

  addDialog({
    title: `調整庫存（SKU ${row.skuId}）`,
    width: "32%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(adjustForm, {
        ref: adjustFormRef,
        warehouseName: `${warehouseName(row.wareId)}`,
        currentStock: row.stock,
        formInline
      }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = adjustFormRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        adjustStock(row.skuId, { wareId: row.wareId, ...formInline })
          .then(() => {
            message("調整成功", { type: "success" });
            done();
            onSearch();
          })
          .catch(error => {
            showError(error, "調整失敗，可能會讓庫存變成負數");
            closeLoading();
          });
      });
    }
  });
}

onMounted(async () => {
  const { items } = await getWarehouseList();
  warehouses.value = items;
  onSearch();
});
</script>

<template>
  <div class="main">
    <el-form :inline="true" :model="searchForm" class="mb-2">
      <el-form-item label="倉庫">
        <el-select v-model="searchForm.wareId" placeholder="不限" clearable style="width: 160px">
          <el-option v-for="w in warehouses" :key="w.id" :label="w.name" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="SKU ID">
        <el-input-number v-model="searchForm.keyword" :min="1" :controls="false" placeholder="不限" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">查詢</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <PureTableBar title="庫存列表" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openCreateDialog()">
          新增庫存
        </el-button>
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <pure-table
          border
          adaptive
          :size="size"
          :data="dataList"
          :columns="dynamicColumns"
          :loading="loading"
          :pagination="pagination"
          row-key="skuId"
          @page-size-change="onPageSizeChange"
          @page-current-change="onPageCurrentChange"
        >
          <template #image="{ row }">
            <el-image
              v-if="row.mainImage"
              :src="row.mainImage"
              style="width: 48px; height: 48px"
              fit="cover"
            />
            <span v-else class="text-gray-400">—</span>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openAdjustDialog(row)">
              調整
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
