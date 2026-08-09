<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import { type TaskItem, getTaskList } from "@/api/inventoryTask";

defineOptions({
  name: "InventoryTask"
});

const loading = ref(true);
const dataList = ref<Array<TaskItem>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const searchForm = reactive<{
  status?: number;
  dateRange: [string, string] | null;
}>({
  status: undefined,
  dateRange: null
});

/** el-date-picker 是受控元件，另開一個 ref 給 picker 顯示用，見 system/log 頁面同樣的既有模式。 */
const dateRangePicker = ref<[Date, Date] | null>(null);

const statusOptions = [
  { label: "已鎖定", value: 1 },
  { label: "已釋放", value: 2 },
  { label: "已扣減", value: 3 }
];

const columns: TableColumns[] = [
  { label: "訂單編號", prop: "orderSn", minWidth: 180 },
  { label: "狀態", prop: "status", minWidth: 100, slot: "status" },
  { label: "建立時間", prop: "createdAt", minWidth: 170, formatter: row => formatTime(row.createdAt) }
];

function formatTime(iso: string) {
  return new Date(iso).toLocaleString("zh-TW", { hour12: false });
}

function statusLabel(status: number) {
  return statusOptions.find(s => s.value === status)?.label ?? status;
}

async function onSearch() {
  loading.value = true;
  const { items, total } = await getTaskList({
    status: searchForm.status,
    from: searchForm.dateRange?.[0],
    to: searchForm.dateRange?.[1],
    page: pagination.currentPage,
    pageSize: pagination.pageSize
  });
  dataList.value = items;
  pagination.total = total;
  loading.value = false;
}

function onReset() {
  searchForm.status = undefined;
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
      <el-form-item label="狀態">
        <el-select v-model="searchForm.status" placeholder="不限" clearable style="width: 140px">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
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

    <PureTableBar title="庫存工作單" :columns="columns" @refresh="onSearch">
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
            <el-tag
              :type="row.status === 1 ? 'warning' : row.status === 3 ? 'success' : 'info'"
              effect="plain"
            >
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
