<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import { type OperLogItem, searchOperLogs } from "@/api/operLog";

defineOptions({
  name: "SystemLog"
});

const loading = ref(true);
const dataList = ref<Array<OperLogItem>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const searchForm = reactive<{
  adminId?: number;
  module: string;
  dateRange: [string, string] | null;
}>({
  adminId: undefined,
  module: "",
  dateRange: null
});

/**
 * el-date-picker 是受控元件，輸入框顯示的內容是從 v-model 反推出來的，不是選完自己記住——
 * 原本只監聽 @change 沒有綁 v-model，查詢功能有作用（change 有拿到值），但選完不會顯示在
 * 框框裡。這裡另外開一個 ref 專門給 picker 顯示用（[Date, Date] 原生型別），
 * searchForm.dateRange 維持 ISO 字串給後端用，兩者用 onDateRangeChange 轉換。
 */
const dateRangePicker = ref<[Date, Date] | null>(null);

const columns: TableColumns[] = [
  { label: "時間", prop: "createdAt", minWidth: 170, formatter: row => formatTime(row.createdAt) },
  { label: "管理員", prop: "username", minWidth: 110 },
  { label: "模組", prop: "module", minWidth: 100 },
  { label: "動作", prop: "action", minWidth: 100 },
  { label: "說明", prop: "targetDesc", minWidth: 200 },
  { label: "結果", prop: "resultStatus", minWidth: 90, slot: "resultStatus" }
];

function formatTime(iso: string) {
  return new Date(iso).toLocaleString("zh-TW", { hour12: false });
}

async function onSearch() {
  loading.value = true;
  const { items, total } = await searchOperLogs({
    adminId: searchForm.adminId || undefined,
    module: searchForm.module || undefined,
    from: searchForm.dateRange?.[0],
    to: searchForm.dateRange?.[1],
    pageNum: pagination.currentPage,
    pageSize: pagination.pageSize
  });
  dataList.value = items;
  pagination.total = total;
  loading.value = false;
}

function onReset() {
  searchForm.adminId = undefined;
  searchForm.module = "";
  searchForm.dateRange = null;
  dateRangePicker.value = null;
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

/** el-date-picker 給的是本地時間字串，轉成帶時區的 ISO 字串給後端的 Instant 參數解析。 */
function onDateRangeChange(value: [Date, Date] | null) {
  searchForm.dateRange = value
    ? [value[0].toISOString(), value[1].toISOString()]
    : null;
}

onMounted(() => {
  onSearch();
});
</script>

<template>
  <div class="main">
    <el-form :inline="true" :model="searchForm" class="mb-2">
      <el-form-item label="管理員 ID">
        <el-input-number
          v-model="searchForm.adminId"
          :min="1"
          :controls="false"
          placeholder="不限"
          clearable
        />
      </el-form-item>
      <el-form-item label="模組">
        <el-input v-model="searchForm.module" placeholder="例如 role" clearable />
      </el-form-item>
      <el-form-item label="時間區間">
        <el-date-picker
          v-model="dateRangePicker"
          type="datetimerange"
          start-placeholder="開始時間"
          end-placeholder="結束時間"
          @change="onDateRangeChange"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">查詢</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <PureTableBar title="操作日誌" :columns="columns" @refresh="onSearch">
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
          <template #resultStatus="{ row }">
            <el-tag :type="row.resultStatus === 1 ? 'success' : 'danger'" effect="plain">
              {{ row.resultStatus === 1 ? "成功" : "拒絕/失敗" }}
            </el-tag>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
