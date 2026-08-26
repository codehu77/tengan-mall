<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import { type GateStatusItem, getGateStatusList, triggerGateWarmUpNow } from "@/api/inventoryGate";

defineOptions({
  name: "InventoryGate"
});

const loading = ref(true);
const dataList = ref<Array<GateStatusItem>>([]);
const warmUpLoading = ref(false);

type StatusTag = { text: string; type: "info" | "warning" | "danger" | "success" };

/**
 * 純前端依現有欄位推導，後端沒有額外存一個 status 欄位（見 tengan-inventory 的 SkuLaunchConfig 設計）。
 * 未預熱的情況拆成三種，因為「還沒到開賣時間」跟「已經超過開賣時間卻還沒預熱」意義完全不同——後者代表
 * 這段期間商品其實是走一般 MySQL 路徑在賣、完全沒被 Redis 保護到，需要提醒管理員點「立即預熱」補救；
 * 如果連閘門關閉時間都過了還沒預熱，代表整個保護窗口已經結束，補預熱也沒意義了，純粹顯示「已逾期」。
 */
function statusOf(row: GateStatusItem): StatusTag {
  if (row.gateSettledAt) {
    return { text: "已結算", type: "info" };
  }
  if (!row.gateWarmedAt) {
    if (row.gateCloseTime && new Date(row.gateCloseTime).getTime() <= Date.now()) {
      return { text: "已逾期", type: "info" };
    }
    if (row.saleStartTime && new Date(row.saleStartTime).getTime() <= Date.now()) {
      return { text: "逾期未預熱", type: "danger" };
    }
    return { text: "未預熱", type: "info" };
  }
  if (row.gateCloseTime && new Date(row.gateCloseTime).getTime() <= Date.now()) {
    return { text: "待結算", type: "warning" };
  }
  return { text: "保護中", type: "success" };
}

const columns: TableColumns[] = [
  { label: "圖片", width: 80, slot: "image" },
  { label: "SKU", prop: "skuId", minWidth: 100 },
  { label: "商品名稱", prop: "skuName", minWidth: 160, formatter: row => row.skuName ?? "—" },
  { label: "開賣時間", prop: "saleStartTime", minWidth: 160, formatter: row => row.saleStartTime ?? "—" },
  { label: "閘門關閉時間", prop: "gateCloseTime", minWidth: 160, formatter: row => row.gateCloseTime ?? "—" },
  { label: "限購", prop: "purchaseLimitPerUser", minWidth: 80, formatter: row => row.purchaseLimitPerUser ?? "不限" },
  { label: "保護庫存", prop: "gateProtectedStock", minWidth: 90, formatter: row => row.gateProtectedStock ?? "—" },
  { label: "目前剩餘(Redis)", prop: "currentAvailablePermits", minWidth: 110, formatter: row => row.currentAvailablePermits ?? "—" },
  { label: "已購買人數", prop: "buyersCount", minWidth: 90, formatter: row => row.buyersCount ?? "—" },
  { label: "狀態", width: 90, slot: "status" },
  { label: "預熱時間", prop: "gateWarmedAt", minWidth: 160, formatter: row => row.gateWarmedAt ?? "—" },
  { label: "結算時間", prop: "gateSettledAt", minWidth: 160, formatter: row => row.gateSettledAt ?? "—" }
];

async function onSearch() {
  loading.value = true;
  const { items } = await getGateStatusList();
  dataList.value = items;
  loading.value = false;
}

// 不用等 GateWarmUpScheduler 固定的每日四個時間點，demo/測試新設定的商品（例如剛設定開賣時間的
// 首發商品）可以立刻從「未預熱」轉「保護中」，不用乾等到下一個排程時間點才會啟動保護。
async function onWarmUpNow() {
  warmUpLoading.value = true;
  try {
    const { count } = await triggerGateWarmUpNow();
    await ElMessageBox.alert(`已預熱 ${count} 個 SKU`, "立即預熱結果", {
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

onMounted(() => {
  onSearch();
});
</script>

<template>
  <div class="main">
    <PureTableBar title="庫存流量閘門" :columns="columns" @refresh="onSearch">
      <template #buttons>
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
          row-key="skuId"
        >
          <template #image="{ row }">
            <el-image
              v-if="row.mainImage"
              :src="row.mainImage"
              style="width: 48px; height: 48px"
              fit="cover"
            />
            <span v-else class="text-gray-400">—</span>
          </template>
          <template #status="{ row }">
            <el-tag :type="statusOf(row).type" effect="plain">
              {{ statusOf(row).text }}
            </el-tag>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
