<script setup lang="ts">
import { h, ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type ActivityItem,
  type ActivitySpuSkus,
  getActivity,
  getActivitySpuSkus,
  replaceProductSkus
} from "@/api/seckillActivity";
import productForm from "./productForm.vue";

defineOptions({
  name: "SeckillActivityProducts"
});

const route = useRoute();
const router = useRouter();
const activityId = Number(route.params.id);

const activityTypeLabel: Record<ActivityItem["activityType"], string> = {
  FLASH_SALE: "限時搶購",
  LAUNCH: "首發"
};

const activityCaption = ref("");
const loading = ref(true);
const products = ref<Array<ActivitySpuSkus>>([]);

function quotaSum(product: ActivitySpuSkus) {
  return product.items.reduce((sum, item) => sum + item.suggestedQuota, 0);
}

const columns: TableColumns[] = [
  { label: "圖片", width: 90, slot: "image" },
  { label: "商品", minWidth: 200, slot: "name" },
  { label: "規格數量", width: 100, formatter: row => row.items.length },
  { label: "秒殺價", width: 100, formatter: row => `NT$ ${row.seckillPrice}` },
  { label: "每人限購", width: 100, prop: "limitPerUser" },
  { label: "配額加總", width: 100, formatter: row => quotaSum(row) },
  { label: "操作", fixed: "right", width: 160, slot: "operation" }
];

async function onSearch() {
  loading.value = true;
  const { items } = await getActivitySpuSkus(activityId);
  products.value = items;
  loading.value = false;
}

async function loadActivityCaption() {
  const activity = await getActivity(activityId);
  const time =
    activity.activityType === "FLASH_SALE" && activity.sessionName && activity.activityDate
      ? `${activity.sessionName}（${activity.activityDate}）`
      : `${new Date(activity.startTime).toLocaleString("zh-TW", { hour12: false })} ~ ${new Date(activity.endTime).toLocaleString("zh-TW", { hour12: false })}`;
  activityCaption.value = `${activityTypeLabel[activity.activityType]} · ${time}`;
}

const formRef = ref();

function openCreateDialog() {
  addDialog({
    title: "新增商品",
    width: "55%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(productForm, { ref: formRef, mode: "create" }),
    beforeSure: (done, { closeLoading }) => {
      if (!formRef.value.isValid()) {
        message("請選擇商品、確認至少一項規格，且配額加總不超過總量", { type: "warning" });
        closeLoading();
        return;
      }
      const spuId = formRef.value.getSpuId();
      replaceProductSkus(activityId, spuId, formRef.value.getSkuIds(), formRef.value.getSkuItems())
        .then(() => {
          message("新增成功", { type: "success" });
          done();
          onSearch();
        })
        .catch((error: any) => {
          message(error?.response?.data?.message ?? "新增失敗", { type: "error" });
          closeLoading();
        });
    }
  });
}

function openEditDialog(row: ActivitySpuSkus) {
  addDialog({
    title: `編輯商品 - ${row.spuName}`,
    width: "55%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(productForm, { ref: formRef, mode: "edit", existing: row }),
    beforeSure: (done, { closeLoading }) => {
      if (!formRef.value.isValid()) {
        message("請確認至少一項規格，且配額加總不超過總量", { type: "warning" });
        closeLoading();
        return;
      }
      replaceProductSkus(activityId, row.spuId, formRef.value.getSkuIds(), formRef.value.getSkuItems())
        .then(() => {
          message("修改成功", { type: "success" });
          done();
          onSearch();
        })
        .catch((error: any) => {
          message(error?.response?.data?.message ?? "修改失敗", { type: "error" });
          closeLoading();
        });
    }
  });
}

function onDelete(row: ActivitySpuSkus) {
  ElMessageBox.confirm(`確定要把「${row.spuName}」從這場活動移除嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    const skuIds = row.items.map(item => item.skuId);
    replaceProductSkus(activityId, row.spuId, skuIds, [])
      .then(() => {
        message("移除成功", { type: "success" });
        onSearch();
      })
      .catch((error: any) => {
        message(error?.response?.data?.message ?? "移除失敗", { type: "error" });
      });
  });
}

function goBack() {
  router.push("/marketing/seckill-activity");
}

onMounted(() => {
  loadActivityCaption();
  onSearch();
});
</script>

<template>
  <div class="main">
    <div class="flex items-center gap-3 mb-3">
      <el-button link @click="goBack">← 返回秒殺活動列表</el-button>
      <span class="text-sm text-gray-500">{{ activityCaption }}</span>
    </div>

    <PureTableBar title="設定活動商品" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openCreateDialog">
          新增商品
        </el-button>
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <pure-table
          border
          adaptive
          :size="size"
          :data="products"
          :columns="dynamicColumns"
          :loading="loading"
          row-key="spuId"
        >
          <template #image="{ row }">
            <el-image
              v-if="row.spuMainImage"
              :src="row.spuMainImage"
              style="width: 56px; height: 56px; border-radius: 4px"
              fit="cover"
            />
            <span v-else class="text-gray-400">—</span>
          </template>
          <template #name="{ row }">
            {{ row.spuName }}（#{{ row.spuId }}）
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">
              編輯
            </el-button>
            <el-button link type="danger" @click="onDelete(row)">
              移除
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
