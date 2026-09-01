<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { message } from "@/utils/message";
import { ElMessageBox } from "element-plus";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type SubscriptionRecord,
  type SubscriptionPayment,
  getSubscriptionList,
  getSubscriptionPayments
} from "@/api/subscription";
import { triggerReconcileNow } from "@/api/payment";

defineOptions({
  name: "SubscriptionList"
});

const loading = ref(true);
const dataList = ref<Array<SubscriptionRecord>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const searchForm = reactive<{ memberId?: number; status?: number }>({
  memberId: undefined,
  status: undefined
});

const tierOptions = [
  { label: "PRO", value: "PRO" },
  { label: "PRO+", value: "PRO_PLUS" }
];

/** 0=PENDING 1=ACTIVE 2=CANCELLED，跟 tengan-payment SubscriptionStatus 對齊。 */
const statusOptions = [
  { label: "付款確認中", value: 0 },
  { label: "訂閱中", value: 1 },
  { label: "已取消", value: 2 }
];

const statusTagType: Record<number, "warning" | "success" | "info"> = {
  0: "warning",
  1: "success",
  2: "info"
};

/** 即時向 tengan-member 批次組裝，查不到（時序邊角案例）時顯示原始 memberId 別讓欄位空白。 */
function memberLabel(row: SubscriptionRecord) {
  if (!row.memberAccount) {
    return `#${row.memberId}`;
  }
  return row.memberNickname
    ? `${row.memberAccount}（${row.memberNickname}）`
    : row.memberAccount;
}

function tierLabel(tier: string) {
  return tierOptions.find(t => t.value === tier)?.label ?? tier;
}

function statusLabel(status: number) {
  return statusOptions.find(s => s.value === status)?.label ?? status;
}

const columns: TableColumns[] = [
  { label: "訂閱編號", prop: "id", minWidth: 90 },
  { label: "會員 ID", prop: "memberId", minWidth: 90 },
  {
    label: "會員帳號",
    prop: "memberAccount",
    minWidth: 140,
    formatter: row => memberLabel(row)
  },
  {
    label: "方案",
    prop: "targetTier",
    minWidth: 90,
    formatter: row => tierLabel(row.targetTier)
  },
  { label: "狀態", prop: "status", minWidth: 100, slot: "status" },
  {
    label: "每期金額",
    prop: "periodAmount",
    minWidth: 100,
    formatter: row => `NT$ ${row.periodAmount}`
  },
  { label: "連續失敗次數", prop: "consecutiveFailures", minWidth: 110 },
  {
    label: "下次扣款日/到期日",
    prop: "paidUntil",
    minWidth: 170,
    formatter: row => formatTime(row.paidUntil)
  },
  { label: "ECPay 交易編號", prop: "ecpayMerchantTradeNo", minWidth: 190 },
  {
    label: "建立時間",
    prop: "createdAt",
    minWidth: 170,
    formatter: row => formatTime(row.createdAt)
  },
  {
    label: "取消時間",
    prop: "cancelledAt",
    minWidth: 170,
    formatter: row => (row.cancelledAt ? formatTime(row.cancelledAt) : "-")
  },
  { label: "操作", fixed: "right", width: 110, slot: "operation" }
];

function formatTime(iso: string) {
  return new Date(iso).toLocaleString("zh-TW", { hour12: false });
}

function showError(error: any, fallback: string) {
  message(error?.response?.data?.message ?? fallback, { type: "error" });
}

async function onSearch() {
  loading.value = true;
  try {
    const { items, total } = await getSubscriptionList({
      memberId: searchForm.memberId,
      status: searchForm.status,
      page: pagination.currentPage,
      pageSize: pagination.pageSize
    });
    dataList.value = items;
    pagination.total = total;
  } finally {
    loading.value = false;
  }
}

function onReset() {
  searchForm.memberId = undefined;
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

// 扣款紀錄同頁彈窗：subscription_payment 逐期通知歷史，不用另外切頁。
const paymentsDialogVisible = ref(false);
const paymentsLoading = ref(false);
const paymentsList = ref<Array<SubscriptionPayment>>([]);
const activeSubscriptionId = ref<number | null>(null);

async function onShowPayments(row: SubscriptionRecord) {
  activeSubscriptionId.value = row.id;
  paymentsDialogVisible.value = true;
  paymentsLoading.value = true;
  try {
    paymentsList.value = await getSubscriptionPayments(row.id);
  } catch (error) {
    showError(error, "查詢扣款紀錄失敗");
  } finally {
    paymentsLoading.value = false;
  }
}

// Phase 8.6 排程式查帳收尾：手動立即查帳（不限卡多久，一次查完目前所有 PENDING），從付款記錄頁搬過來——
// 使用者主要是為了確認訂閱扣款有沒有真的成功才會用到這顆按鈕，同一顆按鈕仍會一併查一般訂單/訂閱首期/
// 訂閱續期三種（見 tengan-admin PaymentController.reconcileNow），只是操作入口改放這裡。
const reconcileLoading = ref(false);

async function onReconcileNow() {
  reconcileLoading.value = true;
  try {
    const result = await triggerReconcileNow();
    await ElMessageBox.alert(
      `一般訂單：查了 ${result.paymentChecked} 筆，${result.paymentConverged} 筆確認已付款、${result.paymentFailed} 筆確認未付款<br/>` +
        `訂閱首期：查了 ${result.subscriptionChecked} 筆，${result.subscriptionConverged} 筆確認成功、${result.subscriptionFailed} 筆確認失敗<br/>` +
        `訂閱續期：查了 ${result.renewalChecked} 筆，${result.renewalRecovered} 筆確認續訂成功`,
      "立即查帳結果",
      { confirmButtonText: "確定", dangerouslyUseHTMLString: true }
    );
    onSearch();
  } catch (error) {
    showError(error, "立即查帳失敗");
  } finally {
    reconcileLoading.value = false;
  }
}

onMounted(() => {
  onSearch();
});
</script>

<template>
  <div class="main">
    <el-form :inline="true" :model="searchForm" class="mb-2">
      <el-form-item label="會員 ID">
        <el-input-number
          v-model="searchForm.memberId"
          :controls="false"
          placeholder="請輸入會員 ID"
          clearable
          style="width: 140px"
        />
      </el-form-item>
      <el-form-item label="狀態">
        <el-select
          v-model="searchForm.status"
          placeholder="不限"
          clearable
          style="width: 140px"
        >
          <el-option
            v-for="s in statusOptions"
            :key="s.value"
            :label="s.label"
            :value="s.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">查詢</el-button>
        <el-button @click="onReset">重置</el-button>
        <el-button
          type="warning"
          :loading="reconcileLoading"
          @click="onReconcileNow"
          >立即查帳</el-button
        >
      </el-form-item>
    </el-form>

    <PureTableBar title="訂閱紀錄" :columns="columns" @refresh="onSearch">
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
            <el-button link type="primary" @click="onShowPayments(row)"
              >扣款紀錄</el-button
            >
          </template>
        </pure-table>
      </template>
    </PureTableBar>

    <el-dialog
      v-model="paymentsDialogVisible"
      :title="`訂閱 #${activeSubscriptionId} 扣款紀錄`"
      width="1000px"
    >
      <el-table v-loading="paymentsLoading" :data="paymentsList" border>
        <el-table-column label="第幾期" prop="totalSuccessTimes" width="90" />
        <el-table-column label="結果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.success ? 'success' : 'danger'" effect="plain">
              {{ row.success ? "成功" : "失敗" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金額" width="120">
          <template #default="{ row }">NT$ {{ row.amount }}</template>
        </el-table-column>
        <el-table-column label="ECPay 授權單號" prop="gwsr" min-width="180" />
        <el-table-column label="執行時間" min-width="190">
          <template #default="{ row }">{{
            formatTime(row.processDate)
          }}</template>
        </el-table-column>
        <el-table-column label="收到通知時間" min-width="190">
          <template #default="{ row }">{{
            formatTime(row.createdAt)
          }}</template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="paymentsDialogVisible = false">關閉</el-button>
      </template>
    </el-dialog>
  </div>
</template>
