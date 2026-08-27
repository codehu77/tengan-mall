<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "ProductSpuGateForm"
});

interface FormItemProps {
  gateCloseTime?: string;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({ gateCloseTime: undefined })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  gateCloseTime: [
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback(new Error("請選擇保護結束時間"));
          return;
        }
        if (value <= new Date().toISOString().slice(0, 19)) {
          callback(new Error("保護結束時間必須晚於現在"));
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

defineExpose({ getRef });
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="newFormInline"
    :rules="formRules"
    label-width="96px"
  >
    <el-form-item label="保護結束時間" prop="gateCloseTime">
      <el-date-picker
        v-model="newFormInline.gateCloseTime"
        type="datetime"
        placeholder="須晚於現在"
        value-format="YYYY-MM-DDTHH:mm:ss"
        style="width: 260px"
      />
      <div class="text-gray-400 text-xs mt-1" style="width: 100%">
        送出後立即套用到底下所有 SKU，開始保護目前庫存
      </div>
    </el-form-item>
  </el-form>
</template>
