<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "RoleForm"
});

interface FormItemProps {
  roleCode: string;
  roleName: string;
  active: boolean;
}

interface FormProps {
  mode: "create" | "edit";
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  mode: "create",
  formInline: () => ({
    roleCode: "",
    roleName: "",
    active: true
  })
});

const ruleFormRef = ref<FormInstance>();
// 直接沿用同一個物件參照，讓外層 addDialog 的 options.props.formInline 在 beforeSure 時
// 讀得到使用者輸入的最新值——這是 pure-admin 官方推薦的 addDialog + 獨立表單元件寫法。
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  roleCode: [
    { required: true, message: "請輸入角色代碼", trigger: "blur" },
    {
      pattern: /^[A-Z][A-Z0-9_]{1,49}$/,
      message: "只能是大寫英文字母開頭，後面接大寫英文/數字/底線",
      trigger: "blur"
    }
  ],
  roleName: [{ required: true, message: "請輸入角色名稱", trigger: "blur" }]
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
    <el-form-item label="角色代碼" prop="roleCode">
      <el-input
        v-model="newFormInline.roleCode"
        :disabled="mode === 'edit'"
        placeholder="例如 VIEWER，建立後不可修改"
        clearable
      />
    </el-form-item>
    <el-form-item label="角色名稱" prop="roleName">
      <el-input
        v-model="newFormInline.roleName"
        placeholder="請輸入角色名稱"
        clearable
      />
    </el-form-item>
    <el-form-item v-if="mode === 'edit'" label="狀態">
      <el-switch
        v-model="newFormInline.active"
        active-text="啟用"
        inactive-text="停用"
      />
    </el-form-item>
  </el-form>
</template>
