<script setup lang="ts">
import { h, ref, reactive, onMounted } from "vue";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type PurchaseOrderSummary,
  getPurchaseOrderList,
  getPurchaseOrderDetail,
  createPurchaseOrder,
  receivePurchaseOrder
} from "@/api/purchaseOrder";
import { type WarehouseItem, getWarehouseList } from "@/api/inventoryWarehouse";
import createForm from "./createForm.vue";
import receiveDialog from "./receiveDialog.vue";

defineOptions({
  name: "InventoryPurchaseOrder"
});

const loading = ref(true);
const dataList = ref<Array<PurchaseOrderSummary>>([]);
const warehouses = ref<Array<WarehouseItem>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const searchForm = reactive<{ status?: number; wareId?: number }>({
  status: undefined,
  wareId: undefined
});

const statusOptions = [
  { label: "待收貨", value: 1 },
  { label: "已收貨", value: 2 }
];

const columns: TableColumns[] = [
  { label: "單號", prop: "poNumber", minWidth: 180 },
  { label: "倉庫", prop: "wareId", minWidth: 120, formatter: row => `${warehouseName(row.wareId)}` },
  { label: "供應商", prop: "supplierName", minWidth: 120, formatter: row => row.supplierName || "-" },
  { label: "狀態", prop: "status", minWidth: 90, slot: "status" },
  { label: "建立時間", prop: "createdAt", minWidth: 170, formatter: row => formatTime(row.createdAt) },
  { label: "操作", fixed: "right", width: 100, slot: "operation" }
];

function warehouseName(wareId: number) {
  return warehouses.value.find(w => w.id === wareId)?.name ?? wareId;
}

function formatTime(iso: string) {
  return new Date(iso).toLocaleString("zh-TW", { hour12: false });
}

function statusLabel(status: number) {
  return statusOptions.find(s => s.value === status)?.label ?? status;
}

function showError(error: any, fallback: string) {
  message(error?.response?.data?.message ?? fallback, { type: "error" });
}

async function onSearch() {
  loading.value = true;
  const { items, total } = await getPurchaseOrderList({
    status: searchForm.status,
    wareId: searchForm.wareId,
    page: pagination.currentPage,
    pageSize: pagination.pageSize
  });
  dataList.value = items;
  pagination.total = total;
  loading.value = false;
}

function onReset() {
  searchForm.status = undefined;
  searchForm.wareId = undefined;
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

const createFormRef = ref();

function openCreateDialog() {
  const formInline = { wareId: undefined as number | undefined, supplierName: "" };

  addDialog({
    title: "新增採購單",
    width: "50%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(createForm, { ref: createFormRef, formInline, warehouses: warehouses.value }),
    beforeSure: (done, { closeLoading }) => {
      if (!formInline.wareId) {
        message("請選擇倉庫", { type: "warning" });
        closeLoading();
        return;
      }
      const items = createFormRef.value.getItems();
      if (items.length === 0) {
        message("請至少新增一項採購項目", { type: "warning" });
        closeLoading();
        return;
      }
      createPurchaseOrder({
        wareId: formInline.wareId,
        supplierName: formInline.supplierName,
        items
      })
        .then(() => {
          message("建立成功", { type: "success" });
          done();
          onSearch();
        })
        .catch(error => {
          showError(error, "建立失敗");
          closeLoading();
        });
    }
  });
}

async function openReceiveDialog(row: PurchaseOrderSummary) {
  const detail = await getPurchaseOrderDetail(row.id);
  const readonly = row.status !== 1;
  const items = detail.items.map(i => ({
    itemId: i.id,
    skuId: i.skuId,
    orderedQty: i.orderedQty,
    receivedQty: i.receivedQty ?? i.orderedQty
  }));

  addDialog({
    title: readonly ? `採購單明細（${row.poNumber}）` : `收貨（${row.poNumber}）`,
    width: "50%",
    draggable: true,
    closeOnClickModal: false,
    hideFooter: readonly,
    contentRenderer: () => h(receiveDialog, { items, readonly }),
    beforeSure: (done, { closeLoading }) => {
      receivePurchaseOrder(row.id, {
        items: items.map(i => ({ itemId: i.itemId, receivedQty: i.receivedQty }))
      })
        .then(() => {
          message("收貨成功", { type: "success" });
          done();
          onSearch();
        })
        .catch(error => {
          showError(error, "收貨失敗，可能此單已經被收過貨");
          closeLoading();
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
      <el-form-item label="狀態">
        <el-select v-model="searchForm.status" placeholder="不限" clearable style="width: 140px">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="倉庫">
        <el-select v-model="searchForm.wareId" placeholder="不限" clearable style="width: 160px">
          <el-option v-for="w in warehouses" :key="w.id" :label="w.name" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">查詢</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <PureTableBar title="採購單" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openCreateDialog()">
          新增採購單
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
          row-key="id"
          @page-size-change="onPageSizeChange"
          @page-current-change="onPageCurrentChange"
        >
          <template #status="{ row }">
            <el-tag :type="row.status === 1 ? 'warning' : 'success'" effect="plain">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openReceiveDialog(row)">
              {{ row.status === 1 ? "收貨" : "明細" }}
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
