<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "InventoryStockAdjustForm"
});

interface FormItemProps {
  delta: number;
  reason: string;
}

interface FormProps {
  warehouseName: string;
  currentStock: number;
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  warehouseName: "",
  currentStock: 0,
  formInline: () => ({ delta: 0, reason: "" })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  reason: [{ required: true, message: "請輸入調整原因", trigger: "blur" }]
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
    <el-form-item label="倉庫">
      <span>{{ warehouseName }}</span>
    </el-form-item>
    <el-form-item label="目前庫存">
      <span>{{ currentStock }}</span>
    </el-form-item>
    <el-form-item label="增減量" prop="delta">
      <el-input-number v-model="newFormInline.delta" />
      <div class="text-gray-400 text-xs mt-1">
        正數=補貨、負數=報損等扣減，不是覆蓋成這個數字
      </div>
    </el-form-item>
    <el-form-item label="原因" prop="reason">
      <el-input
        v-model="newFormInline.reason"
        placeholder="例如：進貨補庫存、盤點報損"
        clearable
      />
    </el-form-item>
  </el-form>
</template>
