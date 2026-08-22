<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type SessionItem,
  getSessionList,
  createSession,
  updateSession,
  deleteSession
} from "@/api/seckillSession";
import sessionForm from "./form.vue";

defineOptions({
  name: "MarketingSeckillSession"
});

const loading = ref(true);
const dataList = ref<Array<SessionItem>>([]);

const columns: TableColumns[] = [
  { label: "場次名稱", prop: "name", minWidth: 120 },
  { label: "開賣時間", prop: "timeOfDay", minWidth: 100, formatter: row => row.timeOfDay.slice(0, 5) },
  { label: "時長（分）", prop: "durationMinutes", minWidth: 100 },
  { label: "排序", prop: "sortOrder", minWidth: 80 },
  { label: "啟用", minWidth: 90, slot: "enabled" },
  { label: "操作", fixed: "right", width: 140, slot: "operation" }
];

async function onSearch() {
  loading.value = true;
  const { sessions } = await getSessionList();
  dataList.value = sessions;
  loading.value = false;
}

const formRef = ref();

function openSessionDialog(mode: "create" | "edit", row?: SessionItem) {
  const formInline = {
    name: row?.name ?? "",
    timeOfDay: row?.timeOfDay ?? "",
    durationMinutes: row?.durationMinutes ?? 120,
    sortOrder: row?.sortOrder ?? 0,
    enabled: row?.enabled ?? true
  };

  addDialog({
    title: mode === "create" ? "新增場次" : "編輯場次",
    width: "32%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(sessionForm, { ref: formRef, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = formRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const action =
          mode === "create"
            ? createSession(formInline)
            : updateSession(row!.id, formInline);
        action
          .then(() => {
            message(mode === "create" ? "新增成功" : "修改成功", {
              type: "success"
            });
            done();
            onSearch();
          })
          .catch(() => closeLoading());
      });
    }
  });
}

function onDelete(row: SessionItem) {
  ElMessageBox.confirm(`確定要刪除場次「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    deleteSession(row.id)
      .then(() => {
        message("刪除成功", { type: "success" });
        onSearch();
      })
      .catch((error: any) => {
        message(error?.response?.data?.message ?? "刪除失敗", {
          type: "error"
        });
      });
  });
}

onMounted(() => {
  onSearch();
});
</script>

<template>
  <div class="main">
    <PureTableBar title="秒殺場次" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openSessionDialog('create')">
          新增場次
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
          row-key="id"
        >
          <template #enabled="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" effect="plain">
              {{ row.enabled ? "啟用" : "停用" }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openSessionDialog('edit', row)">
              編輯
            </el-button>
            <el-button link type="danger" @click="onDelete(row)">
              刪除
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
