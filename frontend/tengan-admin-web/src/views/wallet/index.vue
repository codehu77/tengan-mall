<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import {
  type WalletRule,
  getWalletRule,
  updateWalletRule,
  adjustMemberPoints,
  updateMemberTier
} from "@/api/wallet";

defineOptions({
  name: "WalletRules"
});

const loading = ref(true);
const rule = reactive<WalletRule>({
  cashbackRatePro: 0,
  cashbackRateProPlus: 0,
  monthlyCapPro: undefined,
  monthlyCapProPlus: undefined,
  pointExpiryDays: 365,
  gracePeriodMinutes: 7 * 1440,
  pointValueRatio: 1
});

function showError(error: any, fallback: string) {
  message(error?.response?.data?.message ?? fallback, { type: "error" });
}

async function loadRule() {
  loading.value = true;
  try {
    Object.assign(rule, await getWalletRule());
  } catch (error) {
    showError(error, "查詢點數規則失敗");
  } finally {
    loading.value = false;
  }
}

const saving = ref(false);
async function onSave() {
  saving.value = true;
  try {
    await updateWalletRule(rule);
    message("點數規則已更新，立即生效", { type: "success" });
  } catch (error) {
    showError(error, "更新點數規則失敗");
  } finally {
    saving.value = false;
  }
}

/** demo 用：把鑑賞期/到期天數調成方便展示的短值，不需要重啟服務。 */
function onDemoShorten() {
  rule.gracePeriodMinutes = 2;
  rule.pointExpiryDays = 30;
  message("已調整為 demo 用短值，記得按「儲存」才會生效", { type: "info" });
}

async function onAdjustPoints() {
  try {
    const { value: memberId } = await ElMessageBox.prompt("請輸入會員 ID", "調整會員點數", {
      confirmButtonText: "下一步",
      cancelButtonText: "取消",
      inputPattern: /^\d+$/,
      inputErrorMessage: "請輸入數字"
    });
    const { value: points } = await ElMessageBox.prompt("請輸入調整點數（正數加發、負數扣回）", "調整會員點數", {
      confirmButtonText: "下一步",
      cancelButtonText: "取消",
      inputPattern: /^-?\d+$/,
      inputErrorMessage: "請輸入整數"
    });
    const { value: reason } = await ElMessageBox.prompt("請輸入調整原因", "調整會員點數", {
      confirmButtonText: "確認調整",
      cancelButtonText: "取消",
      inputPattern: /.+/,
      inputErrorMessage: "請輸入原因"
    });
    await adjustMemberPoints(Number(memberId), Number(points), reason);
    message("點數調整成功", { type: "success" });
  } catch (error) {
    if (error === "cancel") return;
    showError(error, "點數調整失敗");
  }
}

async function onUpdateTier() {
  try {
    const { value: memberId } = await ElMessageBox.prompt("請輸入會員 ID", "調整會員等級", {
      confirmButtonText: "下一步",
      cancelButtonText: "取消",
      inputPattern: /^\d+$/,
      inputErrorMessage: "請輸入數字"
    });
    const { value: tier } = await ElMessageBox.prompt(
      "請輸入目標等級（FREE / PRO / PRO_PLUS）",
      "調整會員等級",
      {
        confirmButtonText: "下一步",
        cancelButtonText: "取消",
        inputPattern: /^(FREE|PRO|PRO_PLUS)$/,
        inputErrorMessage: "請輸入 FREE、PRO 或 PRO_PLUS"
      }
    );
    const { value: reason } = await ElMessageBox.prompt("請輸入調整原因", "調整會員等級", {
      confirmButtonText: "確認調整",
      cancelButtonText: "取消",
      inputPattern: /.+/,
      inputErrorMessage: "請輸入原因"
    });
    await updateMemberTier(Number(memberId), tier, reason);
    message("會員等級調整成功", { type: "success" });
  } catch (error) {
    if (error === "cancel") return;
    showError(error, "會員等級調整失敗");
  }
}

onMounted(() => {
  loadRule();
});
</script>

<template>
  <div class="main">
    <el-card v-loading="loading" class="mb-4" shadow="never">
      <template #header>
        <span>點數規則設定</span>
      </template>
      <el-form :model="rule" label-width="140px" style="max-width: 480px">
        <el-form-item label="PRO 回饋比例">
          <el-input-number v-model="rule.cashbackRatePro" :min="0" :max="1" :step="0.01" />
        </el-form-item>
        <el-form-item label="PRO+ 回饋比例">
          <el-input-number v-model="rule.cashbackRateProPlus" :min="0" :max="1" :step="0.01" />
        </el-form-item>
        <el-form-item label="PRO 單月上限">
          <el-input-number v-model="rule.monthlyCapPro" :min="0" placeholder="留空=無上限" />
        </el-form-item>
        <el-form-item label="PRO+ 單月上限">
          <el-input-number v-model="rule.monthlyCapProPlus" :min="0" placeholder="留空=無上限" />
        </el-form-item>
        <el-form-item label="點數到期天數">
          <el-input-number v-model="rule.pointExpiryDays" :min="1" />
        </el-form-item>
        <el-form-item label="鑑賞期分鐘數">
          <el-input-number v-model="rule.gracePeriodMinutes" :min="0" />
          <span class="ml-2 text-gray-400">分鐘（1 天 = 1440 分鐘，demo 可調成 1、2 分鐘）</span>
        </el-form-item>
        <el-form-item label="點數兌換比例">
          <el-input-number v-model="rule.pointValueRatio" :min="0" :step="0.1" />
          <span class="ml-2 text-gray-400">1 點折抵 NT$</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="onSave">儲存</el-button>
          <el-button @click="onDemoShorten">Demo 用短值</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <span>會員點數/等級調整</span>
      </template>
      <div class="flex gap-4">
        <el-button @click="onAdjustPoints">調整會員點數</el-button>
        <el-button @click="onUpdateTier">調整會員等級</el-button>
      </div>
    </el-card>
  </div>
</template>
