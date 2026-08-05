<script setup lang="ts">
import { ref } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { reindexSearch } from "@/api/productSearch";

defineOptions({
  name: "ProductSearch"
});

const loading = ref(false);

async function onReindex() {
  try {
    await ElMessageBox.confirm(
      "會清空目前的搜尋索引並依 MySQL 現有的上架商品全部重新建立，資料量大時可能需要一點時間，確定要執行嗎？",
      "重建索引",
      { type: "warning" }
    );
  } catch {
    return;
  }

  loading.value = true;
  try {
    const { indexedCount } = await reindexSearch();
    message(`已重建索引，共 ${indexedCount} 筆`, { type: "success" });
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <span>搜尋索引</span>
    </template>
    <p class="mb-4 text-gray-500">
      前台搜尋（tengan-search）的資料是 Elasticsearch 索引，平常靠商品上架/修改/下架自動同步；
      如果索引跟資料庫對不上（例如剛部署、或搜尋功能改版），可以在這裡手動觸發一次全量重建——
      會先清空既有索引，再依 MySQL 目前所有上架中的商品重新建立。
    </p>
    <el-button type="primary" :loading="loading" @click="onReindex">
      重建索引
    </el-button>
  </el-card>
</template>
