<script setup lang="ts">
import { ref } from "vue";
import type { UploadRawFile } from "element-plus";
import { message } from "@/utils/message";
import { uploadImage } from "@/api/media";

defineOptions({
  name: "ImageUrlInput"
});

interface Props {
  modelValue: string;
  category: "banner" | "product";
  placeholder?: string;
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: "圖片網址"
});

const emit = defineEmits<{
  (e: "update:modelValue", value: string): void;
}>();

const uploading = ref(false);

const ALLOWED_TYPES = ["image/jpeg", "image/png", "image/webp"];
const MAX_BYTES = 2 * 1024 * 1024;

function beforeUpload(file: UploadRawFile) {
  if (!ALLOWED_TYPES.includes(file.type)) {
    message("只支援 JPEG/PNG/WebP 格式", { type: "warning" });
    return false;
  }
  if (file.size > MAX_BYTES) {
    message("檔案大小不可超過 2MB", { type: "warning" });
    return false;
  }
  return true;
}

async function customUpload(options: { file: File }) {
  uploading.value = true;
  try {
    const { url } = await uploadImage(options.file, props.category);
    emit("update:modelValue", url);
    message("上傳成功", { type: "success" });
  } catch (error: any) {
    message(error?.response?.data?.message ?? "上傳失敗", { type: "error" });
  } finally {
    uploading.value = false;
  }
}
</script>

<template>
  <div class="flex items-center gap-2">
    <el-input
      :model-value="modelValue"
      :placeholder="placeholder"
      style="width: 320px"
      @update:model-value="value => emit('update:modelValue', value)"
    />
    <el-upload
      :show-file-list="false"
      :before-upload="beforeUpload"
      :http-request="customUpload"
      accept="image/jpeg,image/png,image/webp"
    >
      <el-button :loading="uploading">
        {{ uploading ? "上傳中" : "上傳" }}
      </el-button>
    </el-upload>
  </div>
</template>
