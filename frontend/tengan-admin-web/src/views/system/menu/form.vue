<script setup lang="ts">
import { ref, computed } from "vue";
import type { FormInstance, FormRules } from "element-plus";

defineOptions({
  name: "MenuForm"
});

interface FormItemProps {
  parentId: number | null;
  menuType: number; // 1=CATALOG 2=MENU 3=BUTTON
  title: string;
  path?: string;
  component?: string;
  routeName?: string;
  icon?: string;
  permissionCode?: string;
  sortOrder: number;
}

interface FormProps {
  mode: "create" | "edit";
  /** 建立子節點時顯示「上層：xxx」給操作者確認，不是表單欄位本身。 */
  parentTitle?: string;
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  mode: "create",
  parentTitle: "（無，頂層節點）",
  formInline: () => ({
    parentId: null,
    menuType: 1,
    title: "",
    path: "",
    component: "",
    routeName: "",
    icon: "",
    permissionCode: "",
    sortOrder: 0
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

/** BUTTON 不是路由，不需要 path/component/routeName/icon。 */
const isButton = computed(() => newFormInline.value.menuType === 3);
const isMenu = computed(() => newFormInline.value.menuType === 2);

/**
 * 目錄/選單都需要 path，選單還額外需要 component/routeName——這三個欄位空著存得進去，
 * 但 pure-admin 前端解析不出對應頁面元件時，會產生一個沒有 name、沒有 path 的殘缺路由，
 * Vue Router 註冊路由時直接丟例外，把整個後台的動態路由註冊流程打斷（不是只有這個節點壞掉，
 * 是全部選單都連帶進不去，實測踩過這個坑）。這裡的必填規則要跟後端 Menu.validateRoutable()
 * 保持一致，前端擋一次給即時錯誤訊息，後端擋一次是最後防線（例如直接呼叫 API 略過前端表單）。
 */
const formRules = computed<FormRules>(() => ({
  title: [{ required: true, message: "請輸入標題", trigger: "blur" }],
  path: isButton.value
    ? []
    : [{ required: true, message: "目錄/選單都需要路由路徑，不然前端會炸掉", trigger: "blur" }],
  component: isMenu.value
    ? [{ required: true, message: "選單需要元件路徑，不然前端會炸掉", trigger: "blur" }]
    : [],
  routeName: isMenu.value
    ? [{ required: true, message: "選單需要路由名稱，不然前端會炸掉", trigger: "blur" }]
    : [],
  permissionCode: [
    {
      pattern: /^[a-z][a-z0-9]*(:[a-z][a-z0-9]*)+$/,
      message: "colon 分隔的小寫片段，例如 system:user:write",
      trigger: "blur"
    }
  ]
}));

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
    <el-form-item v-if="mode === 'create'" label="上層節點">
      <span class="text-gray-500">{{ parentTitle }}</span>
    </el-form-item>
    <!-- 目錄底下只能放選單、選單底下只能放按鈕、頂層只能是目錄——類型由父層依情境算好，
         這裡固定鎖死不給改，不管新增還是編輯都一樣，不是只有編輯才鎖。 -->
    <el-form-item label="節點類型">
      <el-radio-group v-model="newFormInline.menuType" disabled>
        <el-radio :value="1">目錄</el-radio>
        <el-radio :value="2">選單</el-radio>
        <el-radio :value="3">按鈕</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="標題" prop="title">
      <el-input v-model="newFormInline.title" placeholder="顯示用標題" clearable />
    </el-form-item>
    <template v-if="!isButton">
      <el-form-item label="路由路徑" prop="path">
        <el-input v-model="newFormInline.path" placeholder="例如 /system/role" clearable />
      </el-form-item>
      <el-form-item label="元件路徑" prop="component">
        <el-input
          v-model="newFormInline.component"
          placeholder="例如 system/role/index"
          clearable
        />
      </el-form-item>
      <el-form-item label="路由名稱" prop="routeName">
        <el-input v-model="newFormInline.routeName" placeholder="例如 SystemRole" clearable />
      </el-form-item>
      <el-form-item label="圖示">
        <el-input v-model="newFormInline.icon" placeholder="Element Plus icon 名稱" clearable />
      </el-form-item>
    </template>
    <el-form-item label="權限碼" prop="permissionCode">
      <el-input
        v-model="newFormInline.permissionCode"
        placeholder="選填，例如 system:user:write"
        clearable
      />
    </el-form-item>
    <el-form-item label="排序">
      <el-input-number v-model="newFormInline.sortOrder" :min="0" />
    </el-form-item>
  </el-form>
</template>
