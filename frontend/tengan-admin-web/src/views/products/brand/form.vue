<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "ProductBrandForm"
});

interface FormItemProps {
  name: string;
  logo?: string;
  descript?: string;
  firstLetter?: string;
  sort: number;
}

interface FormProps {
  mode: "create" | "edit";
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  mode: "create",
  formInline: () => ({
    name: "",
    logo: "",
    descript: "",
    firstLetter: "",
    sort: 0
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  name: [{ required: true, message: "請輸入品牌名稱", trigger: "blur" }],
  firstLetter: [
    { pattern: /^[A-Za-z]?$/, message: "只能是單一英文字母", trigger: "blur" }
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
    <el-form-item label="品牌名稱" prop="name">
      <el-input
        v-model="newFormInline.name"
        placeholder="請輸入品牌名稱"
        clearable
      />
    </el-form-item>
    <el-form-item label="Logo 網址">
      <el-input v-model="newFormInline.logo" placeholder="圖片網址" clearable />
    </el-form-item>
    <el-form-item label="簡介">
      <el-input
        v-model="newFormInline.descript"
        type="textarea"
        :rows="2"
        placeholder="品牌簡介"
      />
    </el-form-item>
    <el-form-item label="首字母" prop="firstLetter">
      <el-input
        v-model="newFormInline.firstLetter"
        placeholder="例如 A"
        maxlength="1"
        clearable
      />
    </el-form-item>
    <el-form-item label="排序">
      <el-input-number v-model="newFormInline.sort" :min="0" />
    </el-form-item>
  </el-form>
</template>
