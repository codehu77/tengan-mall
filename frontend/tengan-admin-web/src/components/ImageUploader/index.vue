<script setup lang="ts">
import { ref } from "vue";
import { message } from "@/utils/message";
import { uploadFile } from "@/api/upload";
import type { UploadRequestOptions } from "element-plus";
import Plus from "~icons/ep/plus";
import Loading from "~icons/ep/loading";

defineOptions({
  name: "ImageUploader"
});

const props = withDefaults(
  defineProps<{
    modelValue?: string;
    /** 圓形頭像預覽 or 方形一般預覽 */
    shape?: "circle" | "square";
  }>(),
  {
    modelValue: "",
    shape: "circle"
  }
);

const emit = defineEmits<{
  (e: "update:modelValue", url: string): void;
}>();

const uploading = ref(false);

const ALLOWED_TYPES = ["image/jpeg", "image/png", "image/webp"];
const MAX_BYTES = 2 * 1024 * 1024;

function beforeUpload(file: File) {
  if (!ALLOWED_TYPES.includes(file.type)) {
    message("僅支援 JPG/PNG/WEBP 圖片格式", { type: "warning" });
    return false;
  }
  if (file.size > MAX_BYTES) {
    message("圖片大小不能超過 2MB", { type: "warning" });
    return false;
  }
  return true;
}

/**
 * 一定要用 :http-request 走專案既有的 http 工具（見 @/api/upload.ts），
 * 不能用 el-upload 內建的 action，那是原生 XHR，不會自動帶上登入用的 Authorization header。
 */
async function customUpload(options: UploadRequestOptions) {
  uploading.value = true;
  try {
    const { url } = await uploadFile(options.file as File);
    emit("update:modelValue", url);
  } catch {
    message("上傳失敗，請稍後再試", { type: "error" });
  } finally {
    uploading.value = false;
  }
}
</script>

<template>
  <el-upload
    class="image-uploader"
    :class="{ 'is-circle': shape === 'circle' }"
    :show-file-list="false"
    :before-upload="beforeUpload"
    :http-request="customUpload"
    accept="image/jpeg,image/png,image/webp"
  >
    <img v-if="modelValue" :src="modelValue" class="preview" />
    <div v-else class="placeholder">
      <IconifyIconOffline v-if="!uploading" :icon="Plus" />
      <IconifyIconOffline v-else class="is-loading" :icon="Loading" />
    </div>
    <div v-if="modelValue && uploading" class="uploading-mask">
      <IconifyIconOffline class="is-loading" :icon="Loading" />
    </div>
  </el-upload>
</template>

<style lang="scss" scoped>
.image-uploader {
  :deep(.el-upload) {
    position: relative;
    display: block;
    width: 96px;
    height: 96px;
    overflow: hidden;
    cursor: pointer;
    border: 1px dashed var(--el-border-color-darker);
    border-radius: 6px;
    transition: border-color 0.2s;

    &:hover {
      border-color: var(--el-color-primary);
    }
  }

  &.is-circle :deep(.el-upload) {
    border-radius: 50%;
  }

  .preview {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    font-size: 24px;
    color: var(--el-text-color-secondary);
  }

  .uploading-mask {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    color: #fff;
    background: rgb(0 0 0 / 40%);
  }
}
</style>
