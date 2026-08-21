<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type ActivityItem,
  getActivityList,
  getActivity,
  createActivity,
  updateActivitySkus
} from "@/api/seckillActivity";
import activityForm from "./form.vue";
import skusForm from "./skusForm.vue";

defineOptions({
  name: "MarketingSeckillActivity"
});

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
  { label: "活動時間", minWidth: 220, formatter: row => `${formatTime(row.startTime)} ~ ${formatTime(row.endTime)}` },
  { label: "狀態", minWidth: 90, slot: "status" },
  { label: "操作", fixed: "right", width: 140, slot: "operation" }
];

function formatTime(iso: string) {
  return new Date(iso).toLocaleString("zh-TW", { hour12: false });
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

const skusFormRef = ref();

/** 目錄型節點 DRAFT 活動要先設定商品才會轉成 PUBLISHED（見 tengan-seckill UpdateActivitySkusService 說明）。 */
async function openSkusDialog(row: ActivityItem) {
  const detail = await getActivity(row.id);
  const initialSkus = detail.skus.map(s => ({
    skuId: s.skuId,
    seckillPrice: s.seckillPrice,
    seckillCount: s.seckillCount,
    limitPerUser: s.limitPerUser
  }));

  addDialog({
    title: `設定活動商品（${activityTypeLabel[row.activityType]}）`,
    width: "50%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(skusForm, { ref: skusFormRef, initialSkus }),
    beforeSure: (done, { closeLoading }) => {
      const items = skusFormRef.value.getSkuItems();
      if (items.length === 0) {
        message("至少要設定一項商品", { type: "warning" });
        closeLoading();
        return;
      }
      updateActivitySkus(row.id, items)
        .then(() => {
          message("商品設定成功", { type: "success" });
          done();
          onSearch();
        })
        .catch(() => closeLoading());
    }
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
            <el-button link type="primary" @click="openSkusDialog(row)">
              設定商品
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
