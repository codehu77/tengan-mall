<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "BaseAttrGroupForm"
});

interface FormItemProps {
  name: string;
  sort: number;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    name: "",
    sort: 0
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  name: [{ required: true, message: "請輸入屬性分組名稱", trigger: "blur" }]
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
    label-width="90px"
  >
    <el-form-item label="分組名稱" prop="name">
      <el-input
        v-model="newFormInline.name"
        placeholder="請輸入屬性分組名稱"
        clearable
      />
    </el-form-item>
    <el-form-item label="排序">
      <el-input-number v-model="newFormInline.sort" :min="0" />
    </el-form-item>
  </el-form>
</template>
