<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { message } from "@/utils/message";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type PaymentRecord,
  type PaymentMethodConfig,
  getPaymentRecordList,
  getPaymentMethods,
  updatePaymentMethodStatus
} from "@/api/payment";

defineOptions({
  name: "PaymentList"
});

const loading = ref(true);
const dataList = ref<Array<PaymentRecord>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const searchForm = reactive<{ orderSn?: string; method?: string }>({
  orderSn: undefined,
  method: undefined
});

/** linepay/credit_card/cod 是全站唯一合法的付款方式命名（見 JWT設計.md 付款方式命名定案）。 */
const methodOptions = [
  { label: "LINE Pay", value: "linepay" },
  { label: "信用卡付款", value: "credit_card" },
  { label: "貨到付款", value: "cod" }
];

/** 1=PENDING 2=PAID（跟 tengan-payment PaymentStatus 對齊）。 */
const statusOptions = [
  { label: "待付款", value: 1 },
  { label: "已付款", value: 2 }
];

const statusTagType: Record<number, "warning" | "success"> = {
  1: "warning",
  2: "success"
};

const columns: TableColumns[] = [
  { label: "訂單編號", prop: "orderSn", minWidth: 170 },
  { label: "會員 ID", prop: "memberId", minWidth: 90 },
  { label: "金額", prop: "amount", minWidth: 100, formatter: row => `NT$ ${row.amount}` },
  { label: "付款方式", prop: "method", minWidth: 100, formatter: row => methodLabel(row.method) },
  { label: "狀態", prop: "status", minWidth: 90, slot: "status" },
  { label: "金流交易編號", prop: "gatewayTradeNo", minWidth: 160, formatter: row => row.gatewayTradeNo ?? "-" },
  { label: "付款時間", prop: "paidAt", minWidth: 170, formatter: row => (row.paidAt ? formatTime(row.paidAt) : "-") },
  { label: "建立時間", prop: "createdAt", minWidth: 170, formatter: row => formatTime(row.createdAt) }
];

function formatTime(iso: string) {
  return new Date(iso).toLocaleString("zh-TW", { hour12: false });
}

function methodLabel(method: string) {
  return methodOptions.find(m => m.value === method)?.label ?? method;
}

function statusLabel(status: number) {
  return statusOptions.find(s => s.value === status)?.label ?? status;
}

function showError(error: any, fallback: string) {
  message(error?.response?.data?.message ?? fallback, { type: "error" });
}

async function onSearch() {
  loading.value = true;
  const { items, total } = await getPaymentRecordList({
    orderSn: searchForm.orderSn,
    method: searchForm.method,
    page: pagination.currentPage,
    pageSize: pagination.pageSize
  });
  dataList.value = items;
  pagination.total = total;
  loading.value = false;
}

function onReset() {
  searchForm.orderSn = undefined;
  searchForm.method = undefined;
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

// 付款方式啟用/停用是真的有效果的功能——顧客前台結帳頁動態抓 GET /api/customer/payments/methods，
// 不是後台開關擺著好看的空功能。
const methodConfigs = ref<Array<PaymentMethodConfig>>([]);
const methodConfigsLoading = ref(true);

async function loadMethodConfigs() {
  methodConfigsLoading.value = true;
  methodConfigs.value = await getPaymentMethods();
  methodConfigsLoading.value = false;
}

function onToggleMethod(config: PaymentMethodConfig) {
  const next = !config.enabled;
  updatePaymentMethodStatus(config.method, next)
    .then(() => {
      config.enabled = next;
      message(`已${next ? "啟用" : "停用"}「${methodLabel(config.method)}」`, { type: "success" });
    })
    .catch(error => showError(error, "更新付款方式狀態失敗"));
}

onMounted(() => {
  onSearch();
  loadMethodConfigs();
});
</script>

<template>
  <div class="main">
    <el-card class="mb-4" shadow="never">
      <template #header>
        <span>付款方式啟用/停用</span>
      </template>
      <div v-loading="methodConfigsLoading" class="flex gap-8">
        <div v-for="config in methodConfigs" :key="config.method" class="flex items-center gap-2">
          <span>{{ methodLabel(config.method) }}</span>
          <el-switch :model-value="config.enabled" @change="onToggleMethod(config)" />
        </div>
      </div>
    </el-card>

    <el-form :inline="true" :model="searchForm" class="mb-2">
      <el-form-item label="訂單編號">
        <el-input v-model="searchForm.orderSn" placeholder="請輸入訂單編號" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="付款方式">
        <el-select v-model="searchForm.method" placeholder="不限" clearable style="width: 140px">
          <el-option v-for="m in methodOptions" :key="m.value" :label="m.label" :value="m.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">查詢</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <PureTableBar title="付款記錄" :columns="columns" @refresh="onSearch">
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
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
