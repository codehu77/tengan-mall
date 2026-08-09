<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "InventoryWarehouseForm"
});

interface FormItemProps {
  name: string;
  address: string;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    name: "",
    address: ""
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  name: [{ required: true, message: "請輸入倉庫名稱", trigger: "blur" }],
  address: [{ required: true, message: "請輸入倉庫地址", trigger: "blur" }]
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
    label-width="82px"
  >
    <el-form-item label="倉庫名稱" prop="name">
      <el-input
        v-model="newFormInline.name"
        placeholder="請輸入倉庫名稱"
        clearable
      />
    </el-form-item>
    <el-form-item label="倉庫地址" prop="address">
      <el-input
        v-model="newFormInline.address"
        placeholder="請輸入倉庫地址"
        clearable
      />
    </el-form-item>
  </el-form>
</template>
