<script setup lang="ts">
import { h, ref, watch, onMounted } from "vue";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { getSpuDetail, type SkuDetailItem } from "@/api/productSpu";
import { getWarehouseList, type WarehouseItem } from "@/api/inventoryWarehouse";
import {
  type SkuStockItem,
  getSkuStockList,
  createStock,
  adjustStock
} from "@/api/inventoryStock";
import adjustForm from "@/views/inventory/stock/adjustForm.vue";
import createForm from "@/views/inventory/stock/createForm.vue";

/**
 * 從商品管理頁面點進來，spuId 是已知的、規格用下拉選（不是搜尋），完全不用管理員記/輸入任何內部
 * ID——這是回應「新增庫存要打商品名稱搜尋還是很奇怪」這個問題的最終解法：庫存管理的入口本來就該
 * 從管理員已經在看的商品長出來，不是另外開一個要憑空想起商品名稱的獨立畫面。
 */
defineOptions({
  name: "SpuStockDialog"
});

const props = defineProps<{ spuId: number }>();

const loading = ref(true);
const skus = ref<Array<SkuDetailItem>>([]);
const warehouses = ref<Array<WarehouseItem>>([]);
const selectedSkuId = ref<number>();
const stockRows = ref<Array<SkuStockItem>>([]);
const stockLoading = ref(false);

function skuLabel(sku: SkuDetailItem) {
  return `${sku.name}（NT$${sku.price}）`;
}

function warehouseName(wareId: number) {
  return warehouses.value.find(w => w.id === wareId)?.name ?? wareId;
}

async function loadStock() {
  if (!selectedSkuId.value) {
    stockRows.value = [];
    return;
  }
  stockLoading.value = true;
  const { items } = await getSkuStockList({
    keyword: selectedSkuId.value,
    page: 1,
    pageSize: 50
  });
  // keyword 在後端是前綴比對(不是精準比對)，這裡再過濾一次避免抓到 5/50/51 這種誤中的列。
  stockRows.value = items.filter(i => i.skuId === selectedSkuId.value);
  stockLoading.value = false;
}

watch(selectedSkuId, loadStock);

const createFormRef = ref();

function openCreateDialog() {
  const sku = skus.value.find(s => s.id === selectedSkuId.value);
  if (!sku) return;
  const formInline: { skuId: number | undefined; wareId: number | undefined; initialStock: number } = {
    skuId: sku.id,
    wareId: undefined,
    initialStock: 0
  };

  addDialog({
    title: "新增庫存",
    width: "32%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(createForm, {
        ref: createFormRef,
        formInline,
        warehouses: warehouses.value,
        fixedSkuId: sku.id,
        fixedSkuLabel: skuLabel(sku)
      }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = createFormRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        createStock({
          skuId: sku.id,
          wareId: formInline.wareId!,
          initialStock: formInline.initialStock
        })
          .then(() => {
            message("新增成功", { type: "success" });
            done();
            loadStock();
          })
          .catch(error => {
            message(error?.response?.data?.message ?? "新增失敗，可能這個倉庫已經有庫存列了", {
              type: "error"
            });
            closeLoading();
          });
      });
    }
  });
}

const adjustFormRef = ref();

function openAdjustDialog(row: SkuStockItem) {
  const formInline = { delta: 0, reason: "" };

  addDialog({
    title: `調整庫存（SKU ${row.skuId}）`,
    width: "32%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(adjustForm, {
        ref: adjustFormRef,
        warehouseName: `${warehouseName(row.wareId)}`,
        currentStock: row.stock,
        formInline
      }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = adjustFormRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        adjustStock(row.skuId, { wareId: row.wareId, ...formInline })
          .then(() => {
            message("調整成功", { type: "success" });
            done();
            loadStock();
          })
          .catch(error => {
            message(error?.response?.data?.message ?? "調整失敗，可能會讓庫存變成負數", {
              type: "error"
            });
            closeLoading();
          });
      });
    }
  });
}

onMounted(async () => {
  loading.value = true;
  const [detail, { items: wares }] = await Promise.all([
    getSpuDetail(props.spuId),
    getWarehouseList()
  ]);
  skus.value = detail.skus;
  warehouses.value = wares;
  if (skus.value.length > 0) {
    selectedSkuId.value = skus.value[0].id;
  }
  loading.value = false;
});
</script>

<template>
  <div v-loading="loading">
    <el-empty v-if="!loading && skus.length === 0" description="此商品尚無 SKU，請先到編輯頁新增規格" />
    <template v-else>
      <el-form label-width="60px" class="mb-2">
        <el-form-item label="規格">
          <el-select v-model="selectedSkuId" style="width: 100%">
            <el-option v-for="sku in skus" :key="sku.id" :label="skuLabel(sku)" :value="sku.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table v-loading="stockLoading" :data="stockRows" border size="small">
        <el-table-column label="倉庫" :formatter="row => `${warehouseName(row.wareId)}`" />
        <el-table-column label="庫存" prop="stock" />
        <el-table-column label="已鎖定" prop="lockedStock" />
        <el-table-column label="可用" :formatter="row => `${row.stock - row.lockedStock}`" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="primary" @click="openAdjustDialog(row)">調整</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!stockLoading && stockRows.length === 0" description="這個規格還沒有任何庫存" :image-size="60" />

      <div class="mt-3">
        <el-button type="primary" :disabled="!selectedSkuId" @click="openCreateDialog()">
          新增到其他倉庫
        </el-button>
      </div>
    </template>
  </div>
</template>
