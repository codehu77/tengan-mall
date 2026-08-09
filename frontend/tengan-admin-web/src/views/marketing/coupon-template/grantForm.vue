<script setup lang="ts">
import { ref, computed } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "CouponGrantForm"
});

interface FormProps {
  templateName: string;
}

withDefaults(defineProps<FormProps>(), {
  templateName: ""
});

const ruleFormRef = ref<FormInstance>();
const userIdsText = ref("");

const formModel = computed(() => ({ userIdsText: userIdsText.value }));

const formRules = ref<FormRules>({
  userIdsText: [
    { required: true, message: "請輸入至少一個會員 ID", trigger: "blur" }
  ]
});

/** 輸入框用逗號/換行分隔的會員 ID 字串，送出前轉成數字陣列，過濾掉空白跟非數字輸入。 */
function getUserIds(): Array<number> {
  return userIdsText.value
    .split(/[,\n，]/)
    .map(s => s.trim())
    .filter(s => s.length > 0)
    .map(Number)
    .filter(n => !Number.isNaN(n));
}

function getRef() {
  return ruleFormRef.value;
}

defineExpose({ getRef, getUserIds });
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="formModel"
    :rules="formRules"
    label-width="82px"
  >
    <el-form-item label="優惠券">
      <span>{{ templateName }}</span>
    </el-form-item>
    <el-form-item label="會員 ID" prop="userIdsText">
      <el-input
        v-model="userIdsText"
        type="textarea"
        :rows="4"
        placeholder="輸入會員 ID，用逗號或換行分隔，例如：1,2,3"
      />
    </el-form-item>
  </el-form>
</template>
