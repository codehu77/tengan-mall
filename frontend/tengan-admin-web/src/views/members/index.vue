<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type MemberItem,
  searchMembers,
  banMember,
  unbanMember
} from "@/api/member";

defineOptions({
  name: "MemberList"
});

const route = useRoute();

const loading = ref(true);
const dataList = ref<Array<MemberItem>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const searchForm = reactive<{
  keyword: string;
  dateRange: [string, string] | null;
}>({
  keyword: "",
  dateRange: null
});

/** 比照 system/log/index.vue 的既有模式：picker 顯示用 [Date, Date]，searchForm 維持 ISO 字串給後端用。 */
const dateRangePicker = ref<[Date, Date] | null>(null);

function onDateRangeChange(value: [Date, Date] | null) {
  searchForm.dateRange = value ? [value[0].toISOString(), value[1].toISOString()] : null;
}

const columns: TableColumns[] = [
  { label: "ID", prop: "id", minWidth: 80 },
  { label: "帳號", prop: "username", minWidth: 130 },
  { label: "暱稱", prop: "nickname", minWidth: 130 },
  { label: "手機", prop: "phone", minWidth: 130 },
  { label: "狀態", prop: "status", minWidth: 90, slot: "status" },
  { label: "操作", fixed: "right", width: 140, slot: "operation" }
];

async function onSearch() {
  loading.value = true;
  const { items, total } = await searchMembers({
    keyword: searchForm.keyword || undefined,
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
  searchForm.keyword = "";
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

function onToggleStatus(row: MemberItem) {
  const nextBan = row.status === 1;
  ElMessageBox.confirm(
    `確定要${nextBan ? "停權" : "復權"}會員「${row.username}」嗎？`,
    "提示",
    { type: "warning" }
  ).then(() => {
    const action = nextBan ? banMember(row.id) : unbanMember(row.id);
    action.then(() => {
      message("操作成功", { type: "success" });
      onSearch();
    });
  });
}

/** 支援從 dashboard「今日新會員」卡片點過來直接帶 ?from=...&to=... 篩選條件。 */
onMounted(() => {
  const { from, to } = route.query;
  if (typeof from === "string" && typeof to === "string") {
    searchForm.dateRange = [from, to];
    dateRangePicker.value = [new Date(from), new Date(to)];
  }
  onSearch();
});
</script>

<template>
  <div class="main">
    <el-form :inline="true" :model="searchForm" class="mb-2">
      <el-form-item label="關鍵字">
        <el-input
          v-model="searchForm.keyword"
          placeholder="帳號/暱稱/手機"
          clearable
        />
      </el-form-item>
      <el-form-item label="註冊時間">
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

    <PureTableBar title="會員列表" :columns="columns" @refresh="onSearch">
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
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="plain">
              {{ row.status === 1 ? "正常" : "停權" }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="onToggleStatus(row)"
            >
              {{ row.status === 1 ? "停權" : "復權" }}
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
