<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import { getSessionList, type SessionItem } from "@/api/seckillSession";

defineOptions({
  name: "SeckillActivityForm"
});

interface FormItemProps {
  activityType: "FLASH_SALE" | "LAUNCH";
  sessionId: number | null;
  activityDate: string | null;
  startTime: string;
  endTime: string;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    activityType: "FLASH_SALE",
    sessionId: null,
    activityDate: null,
    startTime: "",
    endTime: ""
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const sessions = ref<Array<SessionItem>>([]);
onMounted(async () => {
  const { sessions: list } = await getSessionList();
  sessions.value = list;
});

/** el-date-picker 是受控元件，另開一組 Date[]/Date 給 picker 顯示、寫回 ISO 字串給後端，比照 coupon-template/form.vue 同樣的既有模式。 */
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

    <template v-if="newFormInline.activityType === 'FLASH_SALE'">
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
    </template>

    <el-form-item v-else label="活動時間">
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
