<script setup lang="ts">
import { ref, watch } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type { WarehouseItem } from "@/api/inventoryWarehouse";
import {
  type SpuSummaryItem,
  type SkuDetailItem,
  searchSpus,
  getSpuDetail
} from "@/api/productSpu";

defineOptions({
  name: "InventoryStockCreateForm"
});

interface FormItemProps {
  skuId: number | undefined;
  wareId: number | undefined;
  initialStock: number;
}

interface FormProps {
  formInline: FormItemProps;
  warehouses: Array<WarehouseItem>;
  /** 從商品頁面點進來時 skuId 已經知道，不用再搜尋一次商品——見 spu/stockDialog.vue。 */
  fixedSkuId?: number;
  fixedSkuLabel?: string;
}

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({ skuId: undefined, wareId: undefined, initialStock: 0 }),
  warehouses: () => [],
  fixedSkuId: undefined,
  fixedSkuLabel: ""
});

const ruleFormRef = ref<FormInstance>();
const newFormInline = ref(props.formInline);
if (props.fixedSkuId) {
  newFormInline.value.skuId = props.fixedSkuId;
}

/** 商品/規格用商品名稱搜尋選出來，不讓管理員自己輸入內部的數字 ID。 */
const spuOptions = ref<Array<SpuSummaryItem>>([]);
const spuLoading = ref(false);
const selectedSpuId = ref<number>();
const skuOptions = ref<Array<SkuDetailItem>>([]);
const skuLoading = ref(false);

async function onSearchSpu(keyword: string) {
  if (!keyword) {
    spuOptions.value = [];
    return;
  }
  spuLoading.value = true;
  const { items } = await searchSpus({ name: keyword, pageNum: 1, pageSize: 20 });
  spuOptions.value = items;
  spuLoading.value = false;
}

watch(selectedSpuId, async spuId => {
  newFormInline.value.skuId = undefined;
  skuOptions.value = [];
  if (!spuId) {
    return;
  }
  skuLoading.value = true;
  const detail = await getSpuDetail(spuId);
  skuOptions.value = detail.skus;
  skuLoading.value = false;
});

function skuLabel(sku: SkuDetailItem) {
  return `${sku.name}（NT$${sku.price}）`;
}

const formRules = ref<FormRules>({
  skuId: [{ required: true, message: "請選擇商品規格", trigger: "change" }],
  wareId: [{ required: true, message: "請選擇倉庫", trigger: "change" }]
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
    <el-form-item v-if="fixedSkuId" label="規格">
      <span>{{ fixedSkuLabel }}</span>
    </el-form-item>
    <template v-else>
      <el-form-item label="商品">
        <el-select
          v-model="selectedSpuId"
          filterable
          remote
          reserve-keyword
          placeholder="輸入商品名稱搜尋"
          :remote-method="onSearchSpu"
          :loading="spuLoading"
        >
          <el-option
            v-for="spu in spuOptions"
            :key="spu.id"
            :label="spu.name"
            :value="spu.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="規格" prop="skuId">
        <el-select
          v-model="newFormInline.skuId"
          placeholder="請先選擇商品"
          :loading="skuLoading"
          :disabled="!selectedSpuId"
        >
          <el-option
            v-for="sku in skuOptions"
            :key="sku.id"
            :label="skuLabel(sku)"
            :value="sku.id"
          />
        </el-select>
      </el-form-item>
    </template>
    <el-form-item label="倉庫" prop="wareId">
      <el-select v-model="newFormInline.wareId" placeholder="請選擇倉庫">
        <el-option
          v-for="w in warehouses"
          :key="w.id"
          :label="w.name"
          :value="w.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="初始庫存">
      <el-input-number v-model="newFormInline.initialStock" :min="0" />
    </el-form-item>
  </el-form>
</template>
