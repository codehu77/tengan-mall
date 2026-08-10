<script setup lang="ts">
import { h, ref, reactive, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type OrderSummary,
  getOrderList,
  getOrderDetail,
  shipOrder,
  cancelOrder
} from "@/api/order";
import detailDialog from "./detailDialog.vue";

defineOptions({
  name: "OrderList"
});

const loading = ref(true);
const dataList = ref<Array<OrderSummary>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const searchForm = reactive<{ status?: number }>({
  status: undefined
});

/** 1=PENDING_PAYMENT 2=PAID 3=SHIPPED 4=COMPLETED 5=CANCELLED（跟 tengan-order OrderStatus 對齊）。 */
const statusOptions = [
  { label: "待付款", value: 1 },
  { label: "已付款", value: 2 },
  { label: "已出貨", value: 3 },
  { label: "已完成", value: 4 },
  { label: "已取消", value: 5 }
];

const statusTagType: Record<number, "warning" | "primary" | "success" | "info" | "danger"> = {
  1: "warning",
  2: "primary",
  3: "success",
  4: "success",
  5: "info"
};

const columns: TableColumns[] = [
  { label: "訂單編號", prop: "orderSn", minWidth: 170 },
  { label: "會員 ID", prop: "memberId", minWidth: 90 },
  { label: "應付金額", prop: "payAmount", minWidth: 100, formatter: row => `NT$ ${row.payAmount}` },
  { label: "付款方式", prop: "paymentMethod", minWidth: 100 },
  { label: "狀態", prop: "status", minWidth: 90, slot: "status" },
  { label: "建立時間", prop: "createdAt", minWidth: 170, formatter: row => formatTime(row.createdAt) },
  { label: "操作", fixed: "right", width: 160, slot: "operation" }
];

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
  const { items, total } = await getOrderList({
    status: searchForm.status,
    page: pagination.currentPage,
    pageSize: pagination.pageSize
  });
  dataList.value = items;
  pagination.total = total;
  loading.value = false;
}

function onReset() {
  searchForm.status = undefined;
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

async function openDetailDialog(row: OrderSummary) {
  const detail = await getOrderDetail(row.orderSn);
  addDialog({
    title: `訂單詳情（${row.orderSn}）`,
    width: "60%",
    draggable: true,
    closeOnClickModal: false,
    hideFooter: true,
    contentRenderer: () => h(detailDialog, { detail })
  });
}

function onShip(row: OrderSummary) {
  ElMessageBox.confirm(`確定要將訂單「${row.orderSn}」標記為已出貨嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    shipOrder(row.orderSn)
      .then(() => {
        message("出貨成功", { type: "success" });
        onSearch();
      })
      .catch(error => showError(error, "出貨失敗"));
  });
}

function onCancel(row: OrderSummary) {
  ElMessageBox.prompt(`取消訂單「${row.orderSn}」，請填寫取消原因：`, "客服代為取消", {
    confirmButtonText: "確定",
    cancelButtonText: "放棄",
    inputPattern: /\S+/,
    inputErrorMessage: "取消原因不可為空"
  }).then(({ value }) => {
    cancelOrder(row.orderSn, value)
      .then(() => {
        message("取消成功", { type: "success" });
        onSearch();
      })
      .catch(error => showError(error, "取消失敗"));
  });
}

onMounted(() => {
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
      <el-form-item>
        <el-button type="primary" @click="onSearch">查詢</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <PureTableBar title="訂單列表" :columns="columns" @refresh="onSearch">
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
            <el-tag :type="statusTagType[row.status]" effect="plain">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openDetailDialog(row)">
              明細
            </el-button>
            <el-button v-if="row.status === 2" link type="primary" @click="onShip(row)">
              出貨
            </el-button>
            <el-button v-if="row.status === 1" link type="danger" @click="onCancel(row)">
              取消
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
