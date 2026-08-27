<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import { getSessionList, type SessionItem } from "@/api/seckillSession";

defineOptions({
  name: "SeckillActivityForm"
});

interface FormItemProps {
  activityType: "FLASH_SALE";
  sessionId: number | null;
  activityDate: string | null;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    activityType: "FLASH_SALE",
    sessionId: null,
    activityDate: null
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const sessions = ref<Array<SessionItem>>([]);
onMounted(async () => {
  const { sessions: list } = await getSessionList();
  sessions.value = list;
});

const activityDateValue = ref<Date | null>(
  newFormInline.value.activityDate ? new Date(newFormInline.value.activityDate) : null
);

function onActivityDateChange(value: Date | null) {
  if (value) {
    const yyyy = value.getFullYear();
    const mm = String(value.getMonth() + 1).padStart(2, "0");
    const dd = String(value.getDate()).padStart(2, "0");
    newFormInline.value.activityDate = `${yyyy}-${mm}-${dd}`;
  } else {
    newFormInline.value.activityDate = null;
  }
}

const formRules = ref<FormRules>({
  sessionId: [{ required: true, message: "請選擇場次", trigger: "change" }],
  activityDate: [{ required: true, message: "請選擇日期", trigger: "change" }]
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
    <el-form-item label="場次" prop="sessionId">
      <el-select v-model="newFormInline.sessionId" placeholder="請選擇場次">
        <el-option
          v-for="session in sessions"
          :key="session.id"
          :label="`${session.name}（${session.timeOfDay.slice(0, 5)}，${session.durationMinutes}分鐘）`"
          :value="session.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="日期" prop="activityDate">
      <el-date-picker
        v-model="activityDateValue"
        type="date"
        placeholder="請選擇場次生效日期"
        @change="onActivityDateChange"
      />
    </el-form-item>
  </el-form>
</template>
