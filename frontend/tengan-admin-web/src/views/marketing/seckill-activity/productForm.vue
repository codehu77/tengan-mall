<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import type { FormInstance } from "element-plus";
import { searchSpus, getSpuDetail } from "@/api/productSpu";
import {
  getSpuSkuSuggestions,
  type SpuSkuSuggestion,
  type SkuItemInput,
  type ActivitySpuSkus
} from "@/api/seckillActivity";

defineOptions({
  name: "SeckillActivityProductForm"
});

interface FormProps {
  mode: "create" | "edit";
  /** edit 模式帶入目前已存的設定，直接預填，不用重新選商品。 */
  existing?: ActivitySpuSkus;
}

const props = withDefaults(defineProps<FormProps>(), {
  mode: "create",
  existing: undefined
});

const ruleFormRef = ref<FormInstance>();

/** 選 SPU：依名稱搜尋（重用既有商品搜尋端點）或直接輸入 ID——商品名稱可能重複，
 * 名稱搜尋結果的選項會一併顯示 ID 方便分辨，也可以切到「直接輸入 ID」模式跳過搜尋直接指定。
 * edit 模式不用選（商品已經定案），直接顯示目前編輯中的商品。 */
const pickMode = ref<"search" | "id">("search");
const spuOptions = ref<Array<{ id: number; name: string; mainImage?: string }>>([]);
const spuLoading = ref(false);
const selectedSpuId = ref<number | null>(props.mode === "edit" ? (props.existing?.spuId ?? null) : null);

const manualSpuId = ref<number | null>(null);
const manualSpuName = ref("");
const manualSpuImage = ref("");
const manualLookupLoading = ref(false);
const manualLookupError = ref("");

const selectedSpuName = computed(() => {
  if (props.mode === "edit") return props.existing?.spuName ?? "";
  if (pickMode.value === "id") return manualSpuName.value;
  return spuOptions.value.find(s => s.id === selectedSpuId.value)?.name ?? "";
});

const selectedSpuImage = computed(() => {
  if (props.mode === "edit") return props.existing?.spuMainImage ?? "";
  if (pickMode.value === "id") return manualSpuImage.value;
  return spuOptions.value.find(s => s.id === selectedSpuId.value)?.mainImage ?? "";
});

function onPickModeChange() {
  selectedSpuId.value = null;
  manualSpuId.value = null;
  manualSpuName.value = "";
  manualSpuImage.value = "";
  manualLookupError.value = "";
  rows.value = [];
}

async function searchSpuRemote(query: string) {
  if (!query) {
    spuOptions.value = [];
    return;
  }
  spuLoading.value = true;
  try {
    const { items } = await searchSpus({ name: query, pageNum: 1, pageSize: 20 });
    spuOptions.value = items;
  } finally {
    spuLoading.value = false;
  }
}

async function lookupManualSpuId() {
  if (!manualSpuId.value) return;
  manualLookupLoading.value = true;
  manualLookupError.value = "";
  try {
    const spu = await getSpuDetail(manualSpuId.value);
    manualSpuName.value = spu.name;
    manualSpuImage.value = spu.mainImage ?? "";
    selectedSpuId.value = manualSpuId.value;
  } catch {
    manualSpuName.value = "";
    manualSpuImage.value = "";
    selectedSpuId.value = null;
    manualLookupError.value = "查無此商品，請確認 ID 是否正確";
  } finally {
    manualLookupLoading.value = false;
  }
}

/** 共用輸入：秒殺價/每人限購套用到每一列；總量只是給系統算建議配額的參考值，不是硬性上限的獨立配額池
 * （見「秒殺改成綁 SPU」規劃文件——每個規格底層還是各自獨立的配額鎖，總量純粹是輸入便利+驗證用途）。 */
const sharedPrice = ref(props.existing?.seckillPrice ?? 0);
const sharedLimitPerUser = ref(props.existing?.limitPerUser ?? 1);
const totalQuota = ref(0);

type Row = SpuSkuSuggestion & { seckillPrice: number; limitPerUser: number; seckillCount: number };
const rows = ref<Array<Row>>([]);
const loadingRows = ref(false);

onMounted(() => {
  if (props.mode === "edit" && props.existing) {
    rows.value = props.existing.items.map(item => ({
      ...item,
      seckillPrice: props.existing!.seckillPrice,
      limitPerUser: props.existing!.limitPerUser,
      seckillCount: item.suggestedQuota
    }));
    totalQuota.value = rows.value.reduce((sum, r) => sum + r.seckillCount, 0);
  }
});

async function loadSuggestions() {
  if (!selectedSpuId.value) return;
  loadingRows.value = true;
  try {
    const { items } = await getSpuSkuSuggestions(selectedSpuId.value, totalQuota.value);
    rows.value = items.map(item => ({
      ...item,
      seckillPrice: sharedPrice.value,
      limitPerUser: sharedLimitPerUser.value,
      seckillCount: item.suggestedQuota
    }));
  } finally {
    loadingRows.value = false;
  }
}

const quotaSum = computed(() => rows.value.reduce((sum, r) => sum + (r.seckillCount || 0), 0));
const overLimit = computed(() => quotaSum.value > totalQuota.value);

function getRef() {
  return ruleFormRef.value;
}

/** 目前這個商品全部的規格 skuId——存檔時用來界定「覆蓋範圍」，不管配額是不是 0 都要包含在內。 */
function getSkuIds(): Array<number> {
  return rows.value.map(r => r.skuId);
}

function getSkuItems(): Array<SkuItemInput> {
  return rows.value.map(r => ({
    skuId: r.skuId,
    seckillPrice: r.seckillPrice,
    seckillCount: r.seckillCount,
    limitPerUser: r.limitPerUser
  }));
}

function getSpuId(): number | null {
  return selectedSpuId.value;
}

function isValid(): boolean {
  return !!selectedSpuId.value && rows.value.length > 0 && !overLimit.value;
}

defineExpose({ getRef, getSkuIds, getSkuItems, getSpuId, isValid });
</script>

<template>
  <el-form ref="ruleFormRef" label-width="80px">
    <template v-if="mode === 'create'">
      <el-form-item label="選擇商品">
        <div style="width: 100%">
          <el-radio-group v-model="pickMode" size="small" class="mb-2" @change="onPickModeChange">
            <el-radio-button value="search">依名稱搜尋</el-radio-button>
            <el-radio-button value="id">直接輸入 ID</el-radio-button>
          </el-radio-group>

          <el-select
            v-if="pickMode === 'search'"
            v-model="selectedSpuId"
            filterable
            remote
            clearable
            :remote-method="searchSpuRemote"
            :loading="spuLoading"
            placeholder="輸入商品名稱搜尋（名稱可能重複，選項會附上 ID 方便分辨）"
            style="width: 100%"
          >
            <el-option v-for="spu in spuOptions" :key="spu.id" :label="`${spu.name}（#${spu.id}）`" :value="spu.id" />
          </el-select>

          <div v-else class="flex items-center gap-2">
            <el-input-number
              v-model="manualSpuId"
              :min="1"
              :controls="false"
              placeholder="輸入商品 ID"
              style="width: 160px"
            />
            <el-button :loading="manualLookupLoading" @click="lookupManualSpuId">確認</el-button>
            <span v-if="selectedSpuId && manualSpuName" class="text-sm text-green-600">已選取：{{ manualSpuName }}</span>
            <span v-if="manualLookupError" class="text-sm text-red-500">{{ manualLookupError }}</span>
          </div>
        </div>
      </el-form-item>
    </template>

    <div v-if="selectedSpuName" class="flex items-center gap-3 mb-4">
      <el-image
        v-if="selectedSpuImage"
        :src="selectedSpuImage"
        style="width: 56px; height: 56px; border-radius: 4px"
        fit="cover"
      />
      <span class="font-medium">{{ selectedSpuName }}（#{{ selectedSpuId }}）</span>
    </div>

    <el-row :gutter="8">
      <el-col :span="8">
        <el-form-item label="秒殺價">
          <el-input-number v-model="sharedPrice" :min="0" :precision="2" :controls="false" style="width: 100%" />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="每人限購">
          <el-input-number v-model="sharedLimitPerUser" :min="1" :controls="false" style="width: 100%" />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="總量">
          <el-input-number v-model="totalQuota" :min="0" :controls="false" style="width: 100%" />
        </el-form-item>
      </el-col>
    </el-row>
    <p class="hint-text">秒殺價/每人限購只是「帶出規格」時套用到每一列的預設值，帶出後每一列都可以再各自改。</p>

    <el-button
      type="primary"
      :disabled="!selectedSpuId"
      :loading="loadingRows"
      @click="loadSuggestions"
    >
      帶出規格／重新計算建議配額
    </el-button>

    <template v-if="rows.length > 0">
      <el-row :gutter="8" class="sku-row-header">
        <el-col :span="6">規格</el-col>
        <el-col :span="4">真實庫存</el-col>
        <el-col :span="5">秒殺價</el-col>
        <el-col :span="4">每人限購</el-col>
        <el-col :span="5">配額（可覆蓋，含填 0）</el-col>
      </el-row>
      <el-row v-for="row in rows" :key="row.skuId" :gutter="8" class="sku-row" align="middle">
        <el-col :span="6">{{ row.variantLabel }}</el-col>
        <el-col :span="4">{{ row.realStock }}</el-col>
        <el-col :span="5">
          <el-input-number v-model="row.seckillPrice" :min="0" :precision="2" :controls="false" style="width: 100%" />
        </el-col>
        <el-col :span="4">
          <el-input-number v-model="row.limitPerUser" :min="1" :controls="false" style="width: 100%" />
        </el-col>
        <el-col :span="5">
          <el-input-number
            v-model="row.seckillCount"
            :min="0"
            :max="row.realStock"
            :controls="false"
            style="width: 100%"
          />
        </el-col>
      </el-row>
      <p class="quota-sum" :class="{ over: overLimit }">
        配額加總：{{ quotaSum }} / 總量 {{ totalQuota }}{{ overLimit ? "（超過總量，請調整）" : "" }}
      </p>
    </template>
  </el-form>
</template>

<style scoped>
.hint-text {
  margin: 0 0 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.sku-row-header {
  margin-top: 12px;
  margin-bottom: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.sku-row {
  margin-bottom: 8px;
}
.quota-sum {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.quota-sum.over {
  color: var(--el-color-danger);
}
</style>
