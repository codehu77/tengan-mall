<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "AdminUserForm"
});

interface FormItemProps {
  username: string;
  password: string;
  realName: string;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    username: "",
    password: "",
    realName: ""
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  username: [{ required: true, message: "請輸入帳號", trigger: "blur" }],
  password: [
    { required: true, message: "請輸入密碼", trigger: "blur" },
    { min: 8, message: "密碼至少 8 個字元", trigger: "blur" }
  ]
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
    <el-form-item label="帳號" prop="username">
      <el-input v-model="newFormInline.username" placeholder="登入用帳號" clearable />
    </el-form-item>
    <el-form-item label="密碼" prop="password">
      <el-input
        v-model="newFormInline.password"
        type="password"
        show-password
        placeholder="請輸入密碼"
        clearable
      />
    </el-form-item>
    <el-form-item label="真實姓名" prop="realName">
      <el-input v-model="newFormInline.realName" placeholder="選填" clearable />
    </el-form-item>
  </el-form>
</template>
