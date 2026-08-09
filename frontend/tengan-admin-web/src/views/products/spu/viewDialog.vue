<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { Picture } from "@element-plus/icons-vue";
import DOMPurify from "dompurify";
import { type SpuDetailItem, getSpuDetail } from "@/api/productSpu";

defineOptions({
  name: "ProductSpuViewDialog"
});

interface DialogProps {
  spuId: number;
  categoryName: string;
  brandName: string;
}

const props = defineProps<DialogProps>();

const loading = ref(true);
const detail = ref<SpuDetailItem>();

const statusOptions = [
  { label: "草稿", value: 0 },
  { label: "上架中", value: 1 },
  { label: "已下架", value: 2 },
  { label: "複製草稿", value: 3 }
];

function statusLabel(status: number) {
  return statusOptions.find(o => o.value === status)?.label ?? "未知";
}

function statusTagType(status: number): "info" | "success" | "warning" | "danger" {
  if (status === 1) return "success";
  if (status === 2) return "info";
  if (status === 3) return "danger";
  return "warning";
}

/** 沿用編輯精靈「描述預覽」對話框同一招——純展示不需要掛整顆 wangEditor 編輯器。 */
const sanitizedDescription = computed(() =>
  detail.value?.description ? DOMPurify.sanitize(detail.value.description) : ""
);

onMounted(async () => {
  detail.value = await getSpuDetail(props.spuId);
  loading.value = false;
});
</script>

<template>
  <div v-loading="loading" class="spu-view-dialog">
    <template v-if="detail">
      <el-card class="mb-3" shadow="never">
        <template #header>基本資訊</template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="分類">{{ categoryName }}</el-descriptions-item>
          <el-descriptions-item label="品牌">{{ brandName }}</el-descriptions-item>
          <el-descriptions-item label="名稱">{{ detail.name }}</el-descriptions-item>
          <el-descriptions-item label="狀態">
            <el-tag :type="statusTagType(detail.status)" effect="plain">
              {{ statusLabel(detail.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="mt-3">
          <div class="mb-1 text-gray-500" style="font-size: 12px">主圖</div>
          <el-image
            v-if="detail.mainImage"
            :src="detail.mainImage"
            :preview-src-list="[detail.mainImage]"
            preview-teleported
            class="img-thumb"
            fit="cover"
          >
            <template #error>
              <div class="img-thumb-error"><el-icon><Picture /></el-icon></div>
            </template>
          </el-image>
          <span v-else class="text-gray-400">—</span>
        </div>

        <div v-if="detail.images.length > 0" class="mt-3">
          <div class="mb-1 text-gray-500" style="font-size: 12px">共通圖片</div>
          <div class="flex flex-wrap gap-2">
            <el-image
              v-for="(img, idx) in detail.images"
              :key="idx"
              :src="img.imageUrl"
              :preview-src-list="detail.images.map(i => i.imageUrl)"
              preview-teleported
              class="img-thumb"
              fit="cover"
            />
          </div>
        </div>

        <div v-if="sanitizedDescription" class="mt-3">
          <div class="mb-1 text-gray-500" style="font-size: 12px">描述</div>
          <div class="spu-view-description" v-html="sanitizedDescription" />
        </div>
      </el-card>

      <el-card class="mb-3" shadow="never">
        <template #header>規格參數</template>
        <el-empty v-if="detail.attrValues.length === 0" description="無" :image-size="60" />
        <el-descriptions v-else :column="2" border>
          <el-descriptions-item
            v-for="attr in detail.attrValues"
            :key="attr.attrId"
            :label="attr.attrName"
          >
            {{ attr.attrValue }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="never">
        <template #header>銷售屬性與 SKU（共 {{ detail.skus.length }} 顆）</template>
        <el-empty v-if="detail.skus.length === 0" description="尚無 SKU" :image-size="60" />
        <el-card v-for="sku in detail.skus" :key="sku.id" class="mb-3" shadow="never">
          <template #header>{{ sku.name }}</template>
          <el-descriptions :column="2" border class="mb-3">
            <el-descriptions-item label="SKU ID">{{ sku.id }}</el-descriptions-item>
            <el-descriptions-item label="價格">NT$ {{ sku.price }}</el-descriptions-item>
            <el-descriptions-item label="銷量">{{ sku.saleCount }}</el-descriptions-item>
            <el-descriptions-item label="排序">{{ sku.sort }}</el-descriptions-item>
          </el-descriptions>

          <div v-if="sku.saleAttrValues.length > 0" class="mb-3">
            <el-tag
              v-for="v in sku.saleAttrValues"
              :key="v.attrId"
              class="mr-2"
              effect="plain"
            >
              {{ v.attrName }}：{{ v.attrValue }}
            </el-tag>
          </div>

          <div class="flex flex-wrap gap-2">
            <el-image
              v-if="sku.mainImage"
              :src="sku.mainImage"
              :preview-src-list="[sku.mainImage, ...sku.images.map(i => i.imageUrl)]"
              preview-teleported
              class="img-thumb"
              fit="cover"
            />
            <el-image
              v-for="(img, idx) in sku.images"
              :key="idx"
              :src="img.imageUrl"
              :preview-src-list="sku.images.map(i => i.imageUrl)"
              preview-teleported
              class="img-thumb"
              fit="cover"
            />
          </div>
        </el-card>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.img-thumb {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  cursor: pointer;
}
.img-thumb-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  background: #f5f7fa;
  font-size: 20px;
}
.spu-view-description :deep(img) {
  max-width: 100%;
  display: block;
}
</style>
