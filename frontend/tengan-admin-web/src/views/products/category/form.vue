<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "ProductCategoryForm"
});

interface FormItemProps {
  parentId: number | null;
  name: string;
  icon?: string;
  sort: number;
}

interface FormProps {
  mode: "create" | "edit";
  /** 建立子分類時顯示「上層：xxx」給操作者確認，不是表單欄位本身。 */
  parentTitle?: string;
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  mode: "create",
  parentTitle: "（無，頂層分類）",
  formInline: () => ({
    parentId: null,
    name: "",
    icon: "",
    sort: 0
  })
});

const ruleFormRef = ref<FormInstance>();
// 直接沿用同一個物件參照，讓外層 addDialog 的 formInline 在 beforeSure 時讀得到最新值
// （見 system/role/form.vue 同樣的寫法）。
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  name: [{ required: true, message: "請輸入分類名稱", trigger: "blur" }]
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
    <el-form-item v-if="mode === 'create'" label="上層分類">
      <span class="text-gray-500">{{ parentTitle }}</span>
    </el-form-item>
    <el-form-item label="分類名稱" prop="name">
      <el-input
        v-model="newFormInline.name"
        placeholder="請輸入分類名稱"
        clearable
      />
    </el-form-item>
    <el-form-item label="圖示">
      <el-input
        v-model="newFormInline.icon"
        placeholder="Element Plus icon 名稱"
        clearable
      />
    </el-form-item>
    <el-form-item label="排序">
      <el-input-number v-model="newFormInline.sort" :min="0" />
    </el-form-item>
  </el-form>
</template>
