<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import { message } from "@/utils/message";
import { type FreightRule, getFreightRule, updateFreightRule } from "@/api/order";

defineOptions({
  name: "OrderFreight"
});

const loading = ref(true);
const rule = reactive<FreightRule>({
  freeShippingThreshold: 600,
  shippingFee: 75
});

function showError(error: any, fallback: string) {
  message(error?.response?.data?.message ?? fallback, { type: "error" });
}

async function loadRule() {
  loading.value = true;
  try {
    Object.assign(rule, await getFreightRule());
  } catch (error) {
    showError(error, "查詢運費設定失敗");
  } finally {
    loading.value = false;
  }
}

const saving = ref(false);
async function onSave() {
  saving.value = true;
  try {
    await updateFreightRule(rule);
    message("運費設定已更新，立即生效", { type: "success" });
  } catch (error) {
    showError(error, "更新運費設定失敗");
  } finally {
    saving.value = false;
  }
}

onMounted(() => {
  loadRule();
});
</script>

<template>
  <div class="main">
    <el-card v-loading="loading" shadow="never">
      <template #header>
        <span>運費設定</span>
      </template>
      <el-form :model="rule" label-width="140px" style="max-width: 480px">
        <el-form-item label="免運門檻">
          <el-input-number v-model="rule.freeShippingThreshold" :min="0" :step="10" />
          <span class="ml-2 text-gray-400">商品原價小計達此金額免運</span>
        </el-form-item>
        <el-form-item label="運費金額">
          <el-input-number v-model="rule.shippingFee" :min="0" :step="5" />
          <span class="ml-2 text-gray-400">未達門檻時收取</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="onSave">儲存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
