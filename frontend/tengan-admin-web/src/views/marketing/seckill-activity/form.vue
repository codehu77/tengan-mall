<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "SeckillActivityForm"
});

interface FormItemProps {
  activityType: "FLASH_SALE" | "LAUNCH";
  startTime: string;
  endTime: string;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    activityType: "FLASH_SALE",
    startTime: "",
    endTime: ""
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

/** el-date-picker 是受控元件，另開一組 Date[] 給 picker 顯示、寫回 ISO 字串給後端，比照 coupon-template/form.vue 同樣的既有模式。 */
const dateRange = ref<[Date, Date] | null>(
  newFormInline.value.startTime && newFormInline.value.endTime
    ? [new Date(newFormInline.value.startTime), new Date(newFormInline.value.endTime)]
    : null
);

function onDateRangeChange(value: [Date, Date] | null) {
  if (value) {
    newFormInline.value.startTime = value[0].toISOString();
    newFormInline.value.endTime = value[1].toISOString();
  } else {
    newFormInline.value.startTime = "";
    newFormInline.value.endTime = "";
  }
}

const formRules = ref<FormRules>({
  activityType: [{ required: true, message: "請選擇活動類型", trigger: "change" }]
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
    <el-form-item label="活動類型" prop="activityType">
      <el-select v-model="newFormInline.activityType" placeholder="請選擇">
        <el-option label="限時搶購（配額稀缺）" value="FLASH_SALE" />
        <el-option label="首發（流量閘門）" value="LAUNCH" />
      </el-select>
    </el-form-item>
    <el-form-item label="活動時間">
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
