<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "SeckillSessionForm"
});

interface FormItemProps {
  name: string;
  timeOfDay: string;
  durationMinutes: number;
  sortOrder: number;
  enabled: boolean;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    name: "",
    timeOfDay: "",
    durationMinutes: 120,
    sortOrder: 0,
    enabled: true
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

/** el-time-picker 是受控元件，另開一個 Date 給 picker 顯示、寫回 "HH:mm:ss" 字串給後端。 */
const timeValue = ref<Date | null>(
  newFormInline.value.timeOfDay
    ? new Date(`1970-01-01T${newFormInline.value.timeOfDay}`)
    : null
);

function onTimeChange(value: Date | null) {
  if (value) {
    const hh = String(value.getHours()).padStart(2, "0");
    const mm = String(value.getMinutes()).padStart(2, "0");
    newFormInline.value.timeOfDay = `${hh}:${mm}:00`;
  } else {
    newFormInline.value.timeOfDay = "";
  }
}

const formRules = ref<FormRules>({
  name: [{ required: true, message: "請輸入場次名稱", trigger: "blur" }],
  timeOfDay: [{ required: true, message: "請選擇開賣時間", trigger: "change" }]
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
    <el-form-item label="場次名稱" prop="name">
      <el-input
        v-model="newFormInline.name"
        placeholder="例如「早場」"
        clearable
      />
    </el-form-item>
    <el-form-item label="開賣時間" prop="timeOfDay">
      <el-time-picker
        v-model="timeValue"
        format="HH:mm"
        placeholder="請選擇每日固定開賣時間"
        @change="onTimeChange"
      />
    </el-form-item>
    <el-form-item label="時長（分）" prop="durationMinutes">
      <el-input-number v-model="newFormInline.durationMinutes" :min="1" />
    </el-form-item>
    <el-form-item label="排序">
      <el-input-number v-model="newFormInline.sortOrder" :min="0" />
    </el-form-item>
    <el-form-item label="啟用">
      <el-switch v-model="newFormInline.enabled" />
    </el-form-item>
  </el-form>
</template>
