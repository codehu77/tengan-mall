<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import { type CategoryTreeItem, getCategoryTree } from "@/api/productCategory";
import { type BrandItem, getBrandList } from "@/api/productBrand";
import {
  type SpuSummaryItem,
  searchSpus,
  publishSpu,
  unlistSpu,
  deleteSpu
} from "@/api/productSpu";

defineOptions({
  name: "ProductSpu"
});

const router = useRouter();

const loading = ref(true);
const dataList = ref<Array<SpuSummaryItem>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const categoryTree = ref<Array<CategoryTreeItem>>([]);
const brandList = ref<Array<BrandItem>>([]);

const categoryNameMap = computed(() => {
  const map = new Map<number, string>();
  const walk = (nodes: Array<CategoryTreeItem>) => {
    for (const node of nodes) {
      map.set(node.id, node.name);
      if (node.children?.length) walk(node.children);
    }
  };
  walk(categoryTree.value);
  return map;
});

const brandNameMap = computed(() => {
  const map = new Map<number, string>();
  for (const brand of brandList.value) map.set(brand.id, brand.name);
  return map;
});

const statusOptions = [
  { label: "草稿", value: 0 },
  { label: "上架中", value: 1 },
  { label: "已下架", value: 2 }
];

function statusLabel(status: number) {
  return statusOptions.find(o => o.value === status)?.label ?? "未知";
}

function statusTagType(status: number): "info" | "success" | "warning" {
  if (status === 1) return "success";
  if (status === 2) return "info";
  return "warning";
}

const searchForm = reactive<{
  categoryId?: number;
  brandId?: number;
  name: string;
  status?: number;
}>({
  categoryId: undefined,
  brandId: undefined,
  name: "",
  status: undefined
});

const columns: TableColumns[] = [
  { label: "主圖", prop: "mainImage", width: 90, slot: "mainImage" },
  { label: "名稱", prop: "name", minWidth: 180 },
  { label: "分類", prop: "categoryId", minWidth: 140, slot: "category" },
  { label: "品牌", prop: "brandId", minWidth: 120, slot: "brand" },
  { label: "SKU數量", prop: "skuCount", width: 100 },
  { label: "狀態", prop: "status", width: 100, slot: "status" },
  { label: "操作", fixed: "right", width: 220, slot: "operation" }
];

async function loadFilters() {
  const [{ items: categories }, { items: brands }] = await Promise.all([
    getCategoryTree(),
    getBrandList()
  ]);
  categoryTree.value = categories;
  brandList.value = brands;
}

async function onSearch() {
  loading.value = true;
  const { items, total } = await searchSpus({
    categoryId: searchForm.categoryId,
    brandId: searchForm.brandId,
    name: searchForm.name || undefined,
    status: searchForm.status,
    pageNum: pagination.currentPage,
    pageSize: pagination.pageSize
  });
  dataList.value = items;
  pagination.total = total;
  loading.value = false;
}

function onReset() {
  searchForm.categoryId = undefined;
  searchForm.brandId = undefined;
  searchForm.name = "";
  searchForm.status = undefined;
  pagination.currentPage = 1;
  onSearch();
}

function onPageSizeChange(size: number) {
  pagination.pageSize = size;
  onSearch();
}

function onPageCurrentChange(page: number) {
  pagination.currentPage = page;
  onSearch();
}

function onCreate() {
  router.push("/products/spu/create");
}

function onEdit(row: SpuSummaryItem) {
  router.push(`/products/spu/edit/${row.id}`);
}

function onPublish(row: SpuSummaryItem) {
  ElMessageBox.confirm(`確定要上架「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    publishSpu(row.id)
      .then(() => {
        message("上架成功", { type: "success" });
        onSearch();
      })
      .catch((error: any) => {
        message(error?.response?.data?.message ?? "上架失敗", {
          type: "error"
        });
      });
  });
}

function onUnlist(row: SpuSummaryItem) {
  ElMessageBox.confirm(`確定要下架「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    unlistSpu(row.id).then(() => {
      message("下架成功", { type: "success" });
      onSearch();
    });
  });
}

function onDelete(row: SpuSummaryItem) {
  if (row.status === 1) return;
  ElMessageBox.confirm(`確定要刪除「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    deleteSpu(row.id)
      .then(() => {
        message("刪除成功", { type: "success" });
        onSearch();
      })
      .catch((error: any) => {
        message(error?.response?.data?.message ?? "刪除失敗", {
          type: "error"
        });
      });
  });
}

onMounted(async () => {
  await loadFilters();
  onSearch();
});
</script>

<template>
  <div class="main">
    <el-form :inline="true" :model="searchForm" class="mb-2">
      <el-form-item label="分類">
        <el-cascader
          v-model="searchForm.categoryId"
          :options="categoryTree"
          :props="{
            value: 'id',
            label: 'name',
            children: 'children',
            checkStrictly: false,
            emitPath: false
          }"
          placeholder="不限"
          clearable
          filterable
        />
      </el-form-item>
      <el-form-item label="品牌">
        <el-select
          v-model="searchForm.brandId"
          placeholder="不限"
          filterable
          clearable
          style="width: 160px"
        >
          <el-option
            v-for="brand in brandList"
            :key="brand.id"
            :label="brand.name"
            :value="brand.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="名稱">
        <el-input v-model="searchForm.name" placeholder="商品名稱關鍵字" clearable />
      </el-form-item>
      <el-form-item label="狀態">
        <el-select
          v-model="searchForm.status"
          placeholder="不限"
          clearable
          style="width: 120px"
        >
          <el-option
            v-for="opt in statusOptions"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">查詢</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <PureTableBar title="SPU商品列表" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="onCreate">新增SPU</el-button>
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <pure-table
          border
          adaptive
          :size="size"
          :data="dataList"
          :columns="dynamicColumns"
          :loading="loading"
          :pagination="pagination"
          row-key="id"
          @page-size-change="onPageSizeChange"
          @page-current-change="onPageCurrentChange"
        >
          <template #mainImage="{ row }">
            <el-image
              v-if="row.mainImage"
              :src="row.mainImage"
              style="width: 60px; height: 60px"
              fit="cover"
            />
            <span v-else class="text-gray-400">—</span>
          </template>
          <template #category="{ row }">
            {{ categoryNameMap.get(row.categoryId) ?? "—" }}
          </template>
          <template #brand="{ row }">
            {{ brandNameMap.get(row.brandId) ?? "—" }}
          </template>
          <template #status="{ row }">
            <el-tag :type="statusTagType(row.status)" effect="plain">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="onEdit(row)">編輯</el-button>
            <el-button
              v-if="row.status !== 1"
              link
              type="success"
              @click="onPublish(row)"
            >
              上架
            </el-button>
            <el-button v-else link type="warning" @click="onUnlist(row)">
              下架
            </el-button>
            <el-tooltip
              v-if="row.status === 1"
              content="上架中的商品需先下架"
              placement="top"
            >
              <el-button link type="danger" disabled>刪除</el-button>
            </el-tooltip>
            <el-button v-else link type="danger" @click="onDelete(row)">
              刪除
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
