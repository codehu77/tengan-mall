<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type ActivityItem,
  getActivityList,
  createActivity,
  deleteActivity,
  triggerWarmUpNow
} from "@/api/seckillActivity";
import activityForm from "./form.vue";

defineOptions({
  name: "MarketingSeckillActivity"
});

const router = useRouter();

const loading = ref(true);
const dataList = ref<Array<ActivityItem>>([]);

const activityTypeLabel: Record<ActivityItem["activityType"], string> = {
  FLASH_SALE: "限時搶購",
  LAUNCH: "首發"
};

const statusLabel: Record<
  ActivityItem["status"],
  { text: string; type: "info" | "warning" | "success" | "primary" }
> = {
  DRAFT: { text: "草稿", type: "info" },
  PUBLISHED: { text: "已發布", type: "warning" },
  ACTIVE: { text: "進行中", type: "success" },
  SETTLED: { text: "已結算", type: "primary" }
};

const columns: TableColumns[] = [
  { label: "活動類型", minWidth: 100, formatter: row => activityTypeLabel[row.activityType] },
  { label: "活動時間", minWidth: 220, formatter: row => formatActivityTime(row) },
  { label: "狀態", minWidth: 90, slot: "status" },
  { label: "操作", fixed: "right", width: 200, slot: "operation" }
];

function formatTime(iso: string) {
  return new Date(iso).toLocaleString("zh-TW", { hour12: false });
}

/** FLASH_SALE 顯示場次名稱+日期比原始 startTime~endTime 更直觀；LAUNCH 維持原樣。 */
function formatActivityTime(row: ActivityItem) {
  if (row.activityType === "FLASH_SALE" && row.sessionName && row.activityDate) {
    return `${row.sessionName}（${row.activityDate}）`;
  }
  return `${formatTime(row.startTime)} ~ ${formatTime(row.endTime)}`;
}

async function onSearch() {
  loading.value = true;
  const { items } = await getActivityList();
  dataList.value = items;
  loading.value = false;
}

const formRef = ref();

function openCreateDialog() {
  const formInline = {
    activityType: "FLASH_SALE" as const,
    sessionId: null,
    activityDate: null,
    startTime: "",
    endTime: ""
  };

  addDialog({
    title: "新增秒殺活動",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(activityForm, { ref: formRef, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = formRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        createActivity(formInline)
          .then(() => {
            message("新增成功，接著請設定活動商品", { type: "success" });
            done();
            onSearch();
          })
          .catch(() => closeLoading());
      });
    }
  });
}

/** 目錄型節點 DRAFT 活動要先設定商品才會轉成 PUBLISHED（見 tengan-seckill UpdateActivitySkusService 說明）。
 * 「設定商品」改成獨立路由頁面（不是 dialog）——商品一多 dialog 塞不下，逐筆新增/編輯/刪除也需要
 * 完整版面，比照 products/spu 列表頁+獨立編輯頁的既有模式（見「設定活動商品改成列表頁」規劃）。 */
function openProducts(row: ActivityItem) {
  router.push(`/marketing/seckill-activity/${row.id}/products`);
}

// 不用等 WarmUpScheduler 固定的每日四個時間點，demo/測試新建的場次（例如剛設定好的「夜貓場」）
// 可以立刻從 PUBLISHED 轉 ACTIVE，不用乾等到下一個排程時間點。
const warmUpLoading = ref(false);

async function onWarmUpNow() {
  warmUpLoading.value = true;
  try {
    const { count } = await triggerWarmUpNow();
    await ElMessageBox.alert(`已預熱 ${count} 個活動`, "立即預熱結果", {
      confirmButtonText: "確定"
    });
    onSearch();
  } catch (error: any) {
    message(error?.response?.data?.message ?? "立即預熱失敗", {
      type: "error"
    });
  } finally {
    warmUpLoading.value = false;
  }
}

function onDelete(row: ActivityItem) {
  ElMessageBox.confirm(
    `確定要刪除這場「${activityTypeLabel[row.activityType]}」活動嗎？`,
    "提示",
    { type: "warning" }
  ).then(() => {
    deleteActivity(row.id)
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
    <PureTableBar title="秒殺活動" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openCreateDialog">
          新增活動
        </el-button>
        <el-button type="warning" :loading="warmUpLoading" @click="onWarmUpNow">
          立即預熱
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
          <template #status="{ row }">
            <el-tag :type="statusLabel[row.status].type" effect="plain">
              {{ statusLabel[row.status].text }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openProducts(row)">
              設定商品
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
