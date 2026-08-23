<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import ImageUrlInput from "@/components/ReUpload/ImageUrlInput.vue";

defineOptions({
  name: "BannerForm"
});

interface FormItemProps {
  imageUrl: string;
  linkUrl: string;
  title: string;
  sortOrder: number;
  enabled: boolean;
}

interface FormProps {
  formInline: FormItemProps;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    imageUrl: "",
    linkUrl: "",
    title: "",
    sortOrder: 0,
    enabled: true
  })
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);

const formRules = ref<FormRules>({
  imageUrl: [{ required: true, message: "請貼上圖片網址或上傳圖片", trigger: "blur" }]
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
    <el-form-item label="圖片" prop="imageUrl">
      <ImageUrlInput v-model="newFormInline.imageUrl" category="banner" />
    </el-form-item>
    <el-form-item v-if="newFormInline.imageUrl" label=" ">
      <el-image
        :src="newFormInline.imageUrl"
        :preview-src-list="[newFormInline.imageUrl]"
        preview-teleported
        fit="cover"
        style="width: 160px; height: 90px; border-radius: 4px"
      />
    </el-form-item>
    <el-form-item label="標題" prop="title">
      <el-input
        v-model="newFormInline.title"
        placeholder="純後台識別用，選填"
        clearable
      />
    </el-form-item>
    <el-form-item label="連結網址" prop="linkUrl">
      <el-input
        v-model="newFormInline.linkUrl"
        placeholder="點擊輪播圖後導向的網址，選填"
        clearable
      />
    </el-form-item>
    <el-form-item label="排序">
      <el-input-number v-model="newFormInline.sortOrder" :min="0" />
    </el-form-item>
    <el-form-item label="啟用">
      <el-switch v-model="newFormInline.enabled" />
    </el-form-item>
  </el-form>
</template>
