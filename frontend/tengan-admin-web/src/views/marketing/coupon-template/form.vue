<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "CouponTemplateForm"
});

interface FormItemProps {
  name: string;
  thresholdAmount: number;
  discountAmount: number;
  totalCount: number;
  effectiveStart: string;
  effectiveEnd: string;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    name: "",
    thresholdAmount: 0,
    discountAmount: 0,
    totalCount: 100,
    effectiveStart: "",
    effectiveEnd: ""
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

/** el-date-picker 是受控元件，另開一組 Date[] 給 picker 顯示、寫回 ISO 字串給後端，見 system/log 頁面同樣的既有模式。 */
const dateRange = ref<[Date, Date] | null>(
  newFormInline.value.effectiveStart && newFormInline.value.effectiveEnd
    ? [
        new Date(newFormInline.value.effectiveStart),
        new Date(newFormInline.value.effectiveEnd)
      ]
    : null
);

function onDateRangeChange(value: [Date, Date] | null) {
  if (value) {
    newFormInline.value.effectiveStart = value[0].toISOString();
    newFormInline.value.effectiveEnd = value[1].toISOString();
  } else {
    newFormInline.value.effectiveStart = "";
    newFormInline.value.effectiveEnd = "";
  }
}

const formRules = ref<FormRules>({
  name: [{ required: true, message: "請輸入模板名稱", trigger: "blur" }],
  totalCount: [{ required: true, message: "請輸入發放上限", trigger: "blur" }]
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
    <el-form-item label="模板名稱" prop="name">
      <el-input
        v-model="newFormInline.name"
        placeholder="例如：滿千折百"
        clearable
      />
    </el-form-item>
    <el-form-item label="門檻金額">
      <el-input-number v-model="newFormInline.thresholdAmount" :min="0" :precision="2" />
    </el-form-item>
    <el-form-item label="折抵金額">
      <el-input-number v-model="newFormInline.discountAmount" :min="0" :precision="2" />
    </el-form-item>
    <el-form-item label="發放上限" prop="totalCount">
      <el-input-number v-model="newFormInline.totalCount" :min="1" />
    </el-form-item>
    <el-form-item label="有效期間">
      <el-date-picker
        v-model="dateRange"
        type="datetimerange"
        start-placeholder="開始時間"
        end-placeholder="結束時間"
        @change="onDateRangeChange"
      />
    </el-form-item>
  </el-form>
</template>
