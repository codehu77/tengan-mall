<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "SaleAttrForm"
});

interface FormItemProps {
  name: string;
  searchable: boolean;
  sort: number;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    name: "",
    searchable: false,
    sort: 0
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  name: [{ required: true, message: "請輸入銷售屬性名稱", trigger: "blur" }]
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
    <el-form-item label="屬性名稱" prop="name">
      <el-input
        v-model="newFormInline.name"
        placeholder="請輸入銷售屬性名稱"
        clearable
      />
    </el-form-item>
    <el-form-item label="可搜尋">
      <el-switch v-model="newFormInline.searchable" />
    </el-form-item>
    <el-form-item label="排序">
      <el-input-number v-model="newFormInline.sort" :min="0" />
    </el-form-item>
  </el-form>
</template>
