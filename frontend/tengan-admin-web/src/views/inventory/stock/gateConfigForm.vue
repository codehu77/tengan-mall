<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import { getGate } from "@/api/inventoryGate";

defineOptions({
  name: "InventoryStockGateConfigForm"
});

interface FormItemProps {
  trafficGateEnabled: boolean;
  gateCloseTime?: string;
}

interface FormProps {
  skuId: number;
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  skuId: 0,
  formInline: () => ({ trafficGateEnabled: false, gateCloseTime: undefined })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);
const loading = ref(true);
const saleStartTime = ref<string | undefined>(undefined);
const gateWarmedAt = ref<string | undefined>(undefined);
const gateSettledAt = ref<string | undefined>(undefined);

const formRules = ref<FormRules>({
  gateCloseTime: [
    {
      validator: (_rule, value, callback) => {
        if (newFormInline.value.trafficGateEnabled && !value) {
          callback(new Error("請選擇閘門關閉時間"));
          return;
        }
        if (newFormInline.value.trafficGateEnabled && saleStartTime.value && value <= saleStartTime.value) {
          callback(new Error("閘門關閉時間必須晚於開賣時間"));
          return;
        }
        callback();
      },
      trigger: "change"
    }
  ]
});

function getRef() {
  return ruleFormRef.value;
}

onMounted(async () => {
  const detail = await getGate(props.skuId);
  saleStartTime.value = detail.saleStartTime;
  gateWarmedAt.value = detail.gateWarmedAt;
  gateSettledAt.value = detail.gateSettledAt;
  newFormInline.value.trafficGateEnabled = detail.trafficGateEnabled;
  newFormInline.value.gateCloseTime = detail.gateCloseTime;
  loading.value = false;
});

defineExpose({ getRef });
</script>

<template>
  <el-form
    v-loading="loading"
    ref="ruleFormRef"
    :model="newFormInline"
    :rules="formRules"
    label-width="120px"
  >
    <el-form-item label="SKU ID">
      <span>{{ skuId }}</span>
    </el-form-item>
    <el-form-item label="開賣時間">
      <span v-if="saleStartTime">{{ saleStartTime }}</span>
      <span v-else class="text-gray-400">尚未設定開賣時間，請先在商品編輯頁面設定</span>
    </el-form-item>
    <el-form-item label="啟用">
      <el-switch v-model="newFormInline.trafficGateEnabled" :disabled="!saleStartTime" />
      <div v-if="gateWarmedAt && !gateSettledAt" class="text-gray-400 text-xs mt-1">
        目前正在保護中，停用前請先等待閘門關閉、結算完成
      </div>
    </el-form-item>
    <el-form-item v-if="newFormInline.trafficGateEnabled" label="閘門關閉時間" prop="gateCloseTime">
      <el-date-picker
        v-model="newFormInline.gateCloseTime"
        type="datetime"
        placeholder="須晚於開賣時間"
        value-format="YYYY-MM-DDTHH:mm:ss"
        style="width: 260px"
      />
    </el-form-item>
  </el-form>
</template>
