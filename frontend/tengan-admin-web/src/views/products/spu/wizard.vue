<script setup lang="ts">
import { ref, reactive, computed, shallowRef, onMounted, onBeforeUnmount } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { Picture } from "@element-plus/icons-vue";
import DOMPurify from "dompurify";
import "@wangeditor/editor/dist/css/style.css";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import type { IDomEditor } from "@wangeditor/editor";
import { applyWangEditorTraditionalChinese } from "@/utils/wangEditorZhTw";
import { message } from "@/utils/message";

applyWangEditorTraditionalChinese();
import { type CategoryTreeItem, getCategoryTree } from "@/api/productCategory";
import { type BrandItem, getBrandList } from "@/api/productBrand";
import {
  type BaseAttrGroupItem,
  type BaseAttrItem,
  type SaleAttrItem,
  getBaseAttrGroups,
  getBaseAttrs,
  getSaleAttrs
} from "@/api/productAttr";
import {
  type SkuFormData,
  getSpuDetail,
  createSpu,
  updateSpu
} from "@/api/productSpu";

defineOptions({
  name: "ProductSpuWizard"
});

const route = useRoute();
const router = useRouter();
const mode = computed(() => (route.name === "ProductSpuEdit" ? "edit" : "create"));
const spuId = computed(() => Number(route.params.id));

const currentStep = ref(0);
const loading = ref(false);
/** 複製出來、還沒被人工編輯確認過的草稿——後端在 Step1「保存」成功後會轉成 NEW，這裡存的是進來時的初始狀態。 */
const isDuplicateDraft = ref(false);

/** 貫穿三步的單一份表單物件，Step 只是這份資料的不同視圖（v-show 切換），不是三個獨立表單。 */
const form = reactive<{
  categoryId: number | null;
  brandId: number | null;
  name: string;
  description: string;
  mainImage: string;
}>({
  categoryId: null,
  brandId: null,
  name: "",
  description: "",
  mainImage: ""
});

type SkuDraft = {
  _key: string;
  name: string;
  price: number | null;
  mainImage: string;
  sort: number;
  images: Array<{ imageUrl: string; sort: number }>;
  saleAttrValues: Array<{ attrId: number | null; attrValue: string }>;
};

const skus = ref<Array<SkuDraft>>([]);

/** Spu 層級共通圖片——所有底下的 Sku 共用（例如整體外觀/包裝/尺寸表），跟各自 Sku 專屬的 images 分開存。 */
const spuImages = ref<Array<{ imageUrl: string; sort: number }>>([]);

function newKey() {
  return Math.random().toString(36).slice(2);
}

function addSpuImage() {
  spuImages.value.push({ imageUrl: "", sort: spuImages.value.length });
}
function removeSpuImage(index: number) {
  spuImages.value.splice(index, 1);
}

const categoryTree = ref<Array<CategoryTreeItem>>([]);
const brandList = ref<Array<BrandItem>>([]);

const baseAttrGroups = ref<Array<BaseAttrGroupItem>>([]);
const baseAttrs = ref<Array<BaseAttrItem>>([]);
const saleAttrs = ref<Array<SaleAttrItem>>([]);
const baseAttrValueMap = reactive<Record<number, string>>({});
const orphanAttrValues = ref<Array<{ attrId: number; attrName: string; attrValue: string }>>([]);

const sortedBaseAttrGroups = computed(() =>
  [...baseAttrGroups.value].sort((a, b) => a.sort - b.sort)
);
function attrsInGroup(groupId: number) {
  return baseAttrs.value
    .filter(a => a.attrGroupId === groupId)
    .sort((a, b) => a.sort - b.sort);
}

let previousCategoryId: number | null = null;

async function loadCategoryTemplates(categoryId: number) {
  const [{ items: groups }, { items: attrs }, { items: sales }] = await Promise.all([
    getBaseAttrGroups(categoryId),
    getBaseAttrs(categoryId),
    getSaleAttrs(categoryId)
  ]);
  baseAttrGroups.value = groups;
  baseAttrs.value = attrs;
  saleAttrs.value = sales;
}

/** Step2/3 只要有任何一邊填過資料，換分類就要二次確認、確認後才清空重拉樣板。 */
const step2Touched = computed(
  () =>
    Object.values(baseAttrValueMap).some(v => !!v) ||
    orphanAttrValues.value.length > 0 ||
    skus.value.length > 0
);

async function onCategoryChange(newId: number | number[]) {
  const id = Array.isArray(newId) ? newId[newId.length - 1] : newId;
  if (previousCategoryId != null && previousCategoryId !== id && step2Touched.value) {
    try {
      await ElMessageBox.confirm(
        "更換分類會清空已填的規格參數與 SKU 列表，確定要繼續嗎？",
        "提示",
        { type: "warning" }
      );
    } catch {
      form.categoryId = previousCategoryId;
      return;
    }
    Object.keys(baseAttrValueMap).forEach(key => delete baseAttrValueMap[Number(key)]);
    orphanAttrValues.value = [];
    skus.value = [];
  }
  previousCategoryId = id;
  await loadCategoryTemplates(id);
}

const step1FormRef = ref();
const step1Rules = {
  categoryId: [{ required: true, message: "請選擇分類", trigger: "change" }],
  brandId: [{ required: true, message: "請選擇品牌", trigger: "change" }],
  name: [{ required: true, message: "請輸入商品名稱", trigger: "blur" }]
};

// --- wangEditor ---
const editorRef = shallowRef<IDomEditor>();
const toolbarConfig = {
  excludeKeys: ["uploadImage", "uploadVideo", "insertVideo"]
};
const editorConfig = { placeholder: "請輸入商品描述..." };
function handleEditorCreated(editor: IDomEditor) {
  editorRef.value = editor;
}
onBeforeUnmount(() => {
  editorRef.value?.destroy();
});

const previewVisible = ref(false);
const sanitizedDescription = computed(() => DOMPurify.sanitize(form.description));

// --- Step3: SKU 手動新增/刪除 ---
function addSku() {
  skus.value.push({
    _key: newKey(),
    name: "",
    price: null,
    mainImage: "",
    sort: skus.value.length,
    images: [],
    saleAttrValues: []
  });
}
function removeSku(index: number) {
  skus.value.splice(index, 1);
}
function addSkuImage(sku: SkuDraft) {
  sku.images.push({ imageUrl: "", sort: sku.images.length });
}
function removeSkuImage(sku: SkuDraft, index: number) {
  sku.images.splice(index, 1);
}
function addSkuSaleAttrValue(sku: SkuDraft) {
  sku.saleAttrValues.push({ attrId: null, attrValue: "" });
}
function removeSkuSaleAttrValue(sku: SkuDraft, index: number) {
  sku.saleAttrValues.splice(index, 1);
}
function availableSaleAttrs(sku: SkuDraft, currentAttrId: number | null) {
  const used = new Set(
    sku.saleAttrValues.map(v => v.attrId).filter(id => id != null && id !== currentAttrId)
  );
  return saleAttrs.value.filter(a => !used.has(a.id));
}

// --- Step3: 快速產生組合 ---
const generateDialogVisible = ref(false);
const candidateValues = reactive<Record<number, Array<string>>>({});
/** 每個屬性目前正在輸入、還沒按 Enter 確認的暫存文字，跟已確認的 candidateValues 分開存。 */
const candidateInputs = reactive<Record<number, string>>({});

function openGenerateDialog() {
  for (const attr of saleAttrs.value) {
    if (!candidateValues[attr.id]) candidateValues[attr.id] = [];
    candidateInputs[attr.id] = "";
  }
  generateDialogVisible.value = true;
}

function onAddCandidate(attrId: number) {
  const value = candidateInputs[attrId]?.trim();
  candidateInputs[attrId] = "";
  if (!value) return;
  if (candidateValues[attrId].includes(value)) return;
  candidateValues[attrId].push(value);
}

function removeCandidate(attrId: number, index: number) {
  candidateValues[attrId].splice(index, 1);
}

function skuComboKey(values: Array<{ attrId: number | null; attrValue: string }>) {
  return values
    .filter(v => v.attrId != null && v.attrValue)
    .map(v => `${v.attrId}:${v.attrValue}`)
    .sort()
    .join("|");
}

function onGenerateCombinations() {
  const activeAttrs = saleAttrs.value.filter(a => (candidateValues[a.id]?.length ?? 0) > 0);
  if (activeAttrs.length === 0) {
    message("請至少為一個銷售屬性輸入候選值", { type: "warning" });
    return;
  }

  let combos: Array<Array<{ attrId: number; attrName: string; attrValue: string }>> = [[]];
  for (const attr of activeAttrs) {
    const next: typeof combos = [];
    for (const combo of combos) {
      for (const value of candidateValues[attr.id]) {
        next.push([...combo, { attrId: attr.id, attrName: attr.name, attrValue: value }]);
      }
    }
    combos = next;
  }

  const existingKeys = new Set(skus.value.map(s => skuComboKey(s.saleAttrValues)));
  let added = 0;
  let skipped = 0;
  for (const combo of combos) {
    const key = skuComboKey(combo);
    if (existingKeys.has(key)) {
      skipped++;
      continue;
    }
    existingKeys.add(key);
    skus.value.push({
      _key: newKey(),
      name: `${form.name} ${combo.map(c => c.attrValue).join("/")}`.trim(),
      price: null,
      mainImage: "",
      sort: skus.value.length,
      images: [],
      saleAttrValues: combo.map(c => ({ attrId: c.attrId, attrValue: c.attrValue }))
    });
    added++;
  }

  if (added > 0 && skipped === 0) {
    message(`已產生 ${added} 筆 SKU 草稿`, { type: "success" });
  } else if (added > 0 && skipped > 0) {
    message(
      `已產生 ${added} 筆 SKU 草稿，${skipped} 筆組合與現有 SKU 重複，已自動略過`,
      { type: "warning" }
    );
  } else {
    message("所選組合皆已存在於目前的 SKU 列表中，未新增任何項目", { type: "warning" });
  }
  generateDialogVisible.value = false;
}

// --- 導覽 ---
async function onNext() {
  if (currentStep.value === 0) {
    const valid = await step1FormRef.value
      .validate()
      .then(() => true)
      .catch(() => false);
    if (!valid) return;
  }
  currentStep.value = Math.min(currentStep.value + 1, 2);
}
function onPrev() {
  currentStep.value = Math.max(currentStep.value - 1, 0);
}
function jumpTo(step: number) {
  if (mode.value === "edit") currentStep.value = step;
}

/**
 * 建立/更新共用的儲存邏輯，回傳是否成功——「完成」（建立模式）跟「保存」（編輯模式每一步都有）
 * 呼叫同一份邏輯，差別只在儲存後要不要導回列表頁，由呼叫端各自決定。
 */
async function saveSpu(): Promise<boolean> {
  const valid = await step1FormRef.value
    .validate()
    .then(() => true)
    .catch(() => false);
  if (!valid) {
    currentStep.value = 0;
    return false;
  }

  const attrValues = [
    ...Object.entries(baseAttrValueMap)
      .filter(([, v]) => !!v && v.trim() !== "")
      .map(([attrId, attrValue]) => ({ attrId: Number(attrId), attrValue })),
    ...orphanAttrValues.value.map(o => ({ attrId: o.attrId, attrValue: o.attrValue }))
  ];

  const skuPayloads: Array<SkuFormData> = skus.value.map(s => ({
    name: s.name,
    price: s.price ?? 0,
    mainImage: s.mainImage || undefined,
    sort: s.sort,
    images: s.images,
    saleAttrValues: s.saleAttrValues
      .filter(v => v.attrId != null)
      .map(v => ({ attrId: v.attrId as number, attrValue: v.attrValue }))
  }));

  const payload = {
    categoryId: form.categoryId as number,
    brandId: form.brandId as number,
    name: form.name,
    description: form.description || undefined,
    mainImage: form.mainImage || undefined,
    attrValues,
    images: spuImages.value,
    skus: skuPayloads
  };

  loading.value = true;
  try {
    if (mode.value === "create") {
      await createSpu(payload);
      message("新增成功", { type: "success" });
    } else {
      await updateSpu(spuId.value, payload);
      message("修改成功", { type: "success" });
      isDuplicateDraft.value = false;
    }
    return true;
  } catch (error: any) {
    message(error?.response?.data?.message ?? "儲存失敗", { type: "error" });
    return false;
  } finally {
    loading.value = false;
  }
}

/** 建立模式的「完成」——儲存成功後離開精靈，回到列表頁。 */
async function onFinish() {
  const ok = await saveSpu();
  if (ok) router.push("/products/spu");
}

/** 編輯模式三步都有的「保存」——儲存成功後留在原頁，讓使用者可以接著編輯其他步驟，不強迫跳到最後一步。 */
async function onSave() {
  await saveSpu();
}

function onCancel() {
  router.push("/products/spu");
}

onMounted(async () => {
  loading.value = true;
  const [{ items: categories }, { items: brands }] = await Promise.all([
    getCategoryTree(),
    getBrandList()
  ]);
  categoryTree.value = categories;
  brandList.value = brands;

  if (mode.value === "edit") {
    const detail = await getSpuDetail(spuId.value);
    isDuplicateDraft.value = detail.status === 3;
    form.categoryId = detail.categoryId;
    form.brandId = detail.brandId;
    form.name = detail.name;
    form.description = detail.description ?? "";
    form.mainImage = detail.mainImage ?? "";
    spuImages.value = detail.images.map(i => ({ ...i }));
    previousCategoryId = detail.categoryId;
    await loadCategoryTemplates(detail.categoryId);

    for (const v of detail.attrValues) {
      if (baseAttrs.value.some(a => a.id === v.attrId)) {
        baseAttrValueMap[v.attrId] = v.attrValue;
      } else {
        orphanAttrValues.value.push({ attrId: v.attrId, attrName: v.attrName, attrValue: v.attrValue });
      }
    }

    skus.value = detail.skus.map(s => ({
      _key: newKey(),
      name: s.name,
      price: s.price,
      mainImage: s.mainImage ?? "",
      sort: s.sort,
      images: s.images.map(i => ({ ...i })),
      saleAttrValues: s.saleAttrValues.map(v => ({ attrId: v.attrId, attrValue: v.attrValue }))
    }));
  }
  loading.value = false;
});
</script>

<template>
  <div class="main" v-loading="loading">
    <el-steps :active="currentStep" finish-status="success" align-center class="mb-4">
      <el-step title="基本資訊" />
      <el-step title="規格參數" />
      <el-step title="銷售屬性與 SKU" />
    </el-steps>

    <el-alert
      v-if="isDuplicateDraft"
      title="這是複製出來的草稿，尚未被編輯確認，無法直接上架"
      description="請檢查各步驟內容（尤其名稱與價格），確認無誤後按「保存」，狀態會轉為草稿，才能上架。"
      type="warning"
      show-icon
      :closable="false"
      class="mb-4"
    />

    <div v-if="mode === 'edit'" class="mb-4 flex justify-center gap-2">
      <el-button link type="primary" @click="jumpTo(0)">跳至基本資訊</el-button>
      <el-button link type="primary" @click="jumpTo(1)">跳至規格參數</el-button>
      <el-button link type="primary" @click="jumpTo(2)">跳至銷售屬性與 SKU</el-button>
    </div>

    <!-- Step1 基本資訊 -->
    <el-form
      v-show="currentStep === 0"
      ref="step1FormRef"
      :model="form"
      :rules="step1Rules"
      label-width="90px"
    >
      <el-form-item label="分類" prop="categoryId">
        <el-cascader
          v-model="form.categoryId"
          :options="categoryTree"
          :props="{
            value: 'id',
            label: 'name',
            children: 'children',
            checkStrictly: false,
            emitPath: false
          }"
          placeholder="請選擇到第三層分類"
          filterable
          style="width: 320px"
          @change="onCategoryChange"
        />
      </el-form-item>
      <el-form-item label="品牌" prop="brandId">
        <el-select v-model="form.brandId" placeholder="請選擇品牌" filterable style="width: 320px">
          <el-option v-for="brand in brandList" :key="brand.id" :label="brand.name" :value="brand.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="名稱" prop="name">
        <el-input v-model="form.name" placeholder="請輸入商品名稱" style="width: 480px" />
      </el-form-item>
      <el-form-item label="主圖">
        <div class="flex items-center gap-2">
          <el-input v-model="form.mainImage" placeholder="圖片網址" style="width: 480px" />
          <el-image
            v-if="form.mainImage"
            :src="form.mainImage"
            :preview-src-list="[form.mainImage]"
            preview-teleported
            class="img-thumb"
            fit="cover"
          >
            <template #error>
              <div class="img-thumb-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
      </el-form-item>
      <el-form-item label="共通圖片">
        <div class="w-full">
          <div
            v-for="(img, imgIdx) in spuImages"
            :key="imgIdx"
            class="mb-2 flex items-center gap-2"
          >
            <el-input v-model="img.imageUrl" placeholder="圖片網址" style="width: 320px" />
            <el-input-number v-model="img.sort" :min="0" style="width: 110px" />
            <el-image
              v-if="img.imageUrl"
              :src="img.imageUrl"
              :preview-src-list="[img.imageUrl]"
              preview-teleported
              class="img-thumb"
              fit="cover"
            >
              <template #error>
                <div class="img-thumb-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <el-button link type="danger" @click="removeSpuImage(imgIdx)">移除</el-button>
          </div>
          <el-button link type="primary" @click="addSpuImage">新增圖片</el-button>
          <div class="text-gray-400" style="font-size: 12px">
            所有 SKU 共用的圖片（例如整體外觀/包裝/尺寸表），跟各 SKU 自己的圖片分開存。
          </div>
        </div>
      </el-form-item>
      <el-form-item label="描述">
        <div style="width: 100%; border: 1px solid #dcdfe6">
          <Toolbar
            :editor="editorRef"
            :defaultConfig="toolbarConfig"
            mode="default"
            style="border-bottom: 1px solid #dcdfe6"
          />
          <Editor
            v-model="form.description"
            :defaultConfig="editorConfig"
            mode="default"
            style="height: 600px; overflow-y: hidden"
            @onCreated="handleEditorCreated"
          />
        </div>
        <el-button style="margin-top: 12px" @click="previewVisible = true">預覽</el-button>
      </el-form-item>
    </el-form>

    <!-- Step2 規格參數 -->
    <div v-show="currentStep === 1">
      <el-empty v-if="sortedBaseAttrGroups.length === 0" description="此分類尚未設定規格參數樣板，可跳過此步驟" />
      <el-card v-for="group in sortedBaseAttrGroups" :key="group.id" class="mb-3">
        <template #header>{{ group.name }}</template>
        <el-form label-width="120px">
          <el-form-item v-for="attr in attrsInGroup(group.id)" :key="attr.id" :label="attr.name">
            <el-input v-model="baseAttrValueMap[attr.id]" placeholder="選填" style="width: 320px" />
          </el-form-item>
          <el-empty v-if="attrsInGroup(group.id).length === 0" description="此分組尚無規格參數" :image-size="60" />
        </el-form>
      </el-card>

      <el-card v-if="orphanAttrValues.length > 0" class="mb-3">
        <template #header>
          <el-tag type="warning" effect="plain">已停用的規格</el-tag>
        </template>
        <div v-for="(orphan, idx) in orphanAttrValues" :key="orphan.attrId" class="mb-2 flex items-center gap-2">
          <span class="w-32">{{ orphan.attrName }}</span>
          <span>{{ orphan.attrValue }}</span>
          <el-button link type="danger" @click="orphanAttrValues.splice(idx, 1)">刪除</el-button>
        </div>
      </el-card>
    </div>

    <!-- Step3 銷售屬性與 SKU -->
    <div v-show="currentStep === 2">
      <div class="mb-3">
        <el-button type="primary" @click="addSku">新增 SKU</el-button>
        <el-button @click="openGenerateDialog">快速產生組合</el-button>
        <el-tag v-if="skus.length === 0" type="warning" effect="plain" class="ml-2">
          目前尚無 SKU，儲存後商品會停在草稿狀態，無法上架
        </el-tag>
      </div>

      <el-card v-for="(sku, index) in skus" :key="sku._key" class="mb-3">
        <template #header>
          <div class="flex justify-between items-center">
            <span>SKU #{{ index + 1 }}</span>
            <el-button link type="danger" @click="removeSku(index)">刪除此 SKU</el-button>
          </div>
        </template>
        <el-form label-width="90px">
          <el-form-item label="名稱">
            <el-input v-model="sku.name" placeholder="SKU 名稱" style="width: 320px" />
          </el-form-item>
          <el-form-item label="價格">
            <el-input-number v-model="sku.price" :min="0" :precision="2" style="width: 200px" />
          </el-form-item>
          <el-form-item label="主圖">
            <div class="flex items-center gap-2">
              <el-input v-model="sku.mainImage" placeholder="圖片網址" style="width: 320px" />
              <el-image
                v-if="sku.mainImage"
                :src="sku.mainImage"
                :preview-src-list="[sku.mainImage]"
                preview-teleported
                class="img-thumb"
                fit="cover"
              >
                <template #error>
                  <div class="img-thumb-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="sku.sort" :min="0" style="width: 140px" />
          </el-form-item>

          <el-form-item label="圖片列表">
            <div class="w-full">
              <div v-for="(img, imgIdx) in sku.images" :key="imgIdx" class="mb-2 flex items-center gap-2">
                <el-input v-model="img.imageUrl" placeholder="圖片網址" style="width: 280px" />
                <el-input-number v-model="img.sort" :min="0" style="width: 110px" />
                <el-image
                  v-if="img.imageUrl"
                  :src="img.imageUrl"
                  :preview-src-list="[img.imageUrl]"
                  preview-teleported
                  class="img-thumb"
                  fit="cover"
                >
                  <template #error>
                    <div class="img-thumb-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <el-button link type="danger" @click="removeSkuImage(sku, imgIdx)">移除</el-button>
              </div>
              <el-button link type="primary" @click="addSkuImage(sku)">新增圖片</el-button>
            </div>
          </el-form-item>

          <el-form-item label="銷售屬性">
            <div class="w-full">
              <div
                v-for="(value, valIdx) in sku.saleAttrValues"
                :key="valIdx"
                class="mb-2 flex items-center gap-2"
              >
                <el-select v-model="value.attrId" placeholder="屬性" style="width: 160px">
                  <el-option
                    v-for="attr in availableSaleAttrs(sku, value.attrId)"
                    :key="attr.id"
                    :label="attr.name"
                    :value="attr.id"
                  />
                </el-select>
                <el-input v-model="value.attrValue" placeholder="屬性值" style="width: 200px" />
                <el-button link type="danger" @click="removeSkuSaleAttrValue(sku, valIdx)">移除</el-button>
              </div>
              <el-button link type="primary" @click="addSkuSaleAttrValue(sku)">新增銷售屬性</el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <div class="mt-4 flex justify-center gap-2">
      <el-button @click="onCancel">取消</el-button>
      <el-button v-if="currentStep > 0" @click="onPrev">上一步</el-button>
      <el-button v-if="currentStep < 2" type="primary" @click="onNext">下一步</el-button>
      <el-button v-if="mode === 'edit'" type="success" @click="onSave">保存</el-button>
      <el-button v-else-if="currentStep === 2" type="primary" @click="onFinish">完成</el-button>
    </div>

    <el-dialog v-model="generateDialogVisible" title="快速產生 SKU 組合" width="600px">
      <p class="text-gray-500" style="margin-bottom: 20px">為要參與組合的銷售屬性輸入候選值（輸入後按 Enter），沒填的屬性不參與組合。</p>
      <div v-for="attr in saleAttrs" :key="attr.id" class="mb-4 flex items-center gap-3">
        <div style="width: 80px; font-weight: 700; flex-shrink: 0">{{ attr.name }}</div>
        <div class="flex flex-wrap items-center gap-2">
          <el-tag
            v-for="(value, idx) in candidateValues[attr.id]"
            :key="value"
            closable
            @close="removeCandidate(attr.id, idx)"
          >
            {{ value }}
          </el-tag>
          <el-input
            v-model="candidateInputs[attr.id]"
            placeholder="輸入候選值後按 Enter"
            style="width: 160px"
            @keyup.enter="onAddCandidate(attr.id)"
          />
        </div>
      </div>
      <el-empty v-if="saleAttrs.length === 0" description="此分類尚未設定銷售屬性樣板" :image-size="60" />
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onGenerateCombinations">產生</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="previewVisible" title="描述預覽" width="800px">
      <div
        class="spu-description-content"
        style="max-width: 750px; margin: 0 auto"
        v-html="sanitizedDescription"
      />
    </el-dialog>
  </div>
</template>

<style>
/*
 * wangEditor 內建樣式表對每個 <p> 都固定 margin:15px 0（見 dist/css/style.css），這跟
 * 「行高」設定無關——就算把行高調到最小的 1，段落與段落之間的間距還是這個寫死的 margin 造成的。
 * 組圖情境（連續好幾張撐滿寬度的圖片，中間不夾文字）每張圖都各自獨立一個 <p>，這個固定 margin
 * 就會在圖與圖中間留出明顯空隙。這裡只把「整個段落只有一張圖」的情況 margin 歸零，一般文字段落
 * 之間的間距不受影響。同一份規則套用在編輯器本身跟預覽 dialog 兩處，讓兩邊視覺一致。
 */
.w-e-text-container [data-slate-editor] p:has(.w-e-image-container) {
  margin: 0;
}
.w-e-text-container [data-slate-editor] .w-e-image-container {
  vertical-align: top;
}
.spu-description-content p:has(img) {
  margin: 0;
}
.spu-description-content img {
  display: block;
  max-width: 100%;
}

/*
 * wangEditor 全螢幕模式（.w-e-full-screen-container）只給 position:fixed，沒有設定
 * z-index（見 dist/css/style.css），而 pure-admin 版面的側邊欄/固定區塊有些疊到 z-index
 * 2000+（見 src/style/sidebar.scss）。兩者同層競爭時，版面元件（例如這個精靈上方的
 * el-steps）會蓋在全螢幕編輯器上面——不只是視覺蓋住，工具列上「退出全螢幕」按鈕本身也被蓋住
 * 點不到，這才是「切不回去」真正的原因，不是全螢幕功能本身壞掉。給一個夠高的 z-index 蓋過
 * 版面元件即可，不需要動 pure-admin 的版面樣式。
 */
.w-e-full-screen-container {
  z-index: 3000 !important;
}

/*
 * 專案的全域 reset（Tailwind preflight）把 button 的 padding/line-height 都重置掉，
 * wangEditor 自己的 .w-e-modal button 雖然有 text-align:center，但插入圖片這類彈窗
 * 的「確定」按鈕文字實際上還是沒有置中——用 flex 置中取代單純的 text-align，不管是什麼
 * 造成文字偏移，flex 置中都能蓋過去。
 */
.w-e-modal button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/*
 * 「插入圖片」彈窗固定給 3 個欄位：圖片網址(src)/圖片描述(alt)/圖片連結(href，選填，填了會把
 * 圖片包成超連結，跟 src 顯示哪張圖無關)——商品描述用不到「點圖片會跳轉」這個功能，這裡直接把
 * 第三個輸入框藏起來。wangEditor 沒有提供設定關掉單一欄位的官方 API（MENU_CONF 只能掛
 * callback，不能砍欄位），只能用 CSS 隱藏；欄位還在 DOM 裡但永遠是空字串，送出時 href 為空、
 * 圖片就不會被包成連結，效果等同拿掉。用 nth-of-type(3) 選第三個 div（跟 class 選擇器等效，
 * 因為插入圖片彈窗裡的欄位容器全部是 div），只有插入圖片這個彈窗有 3 個欄位，不會誤傷其他
 * 彈窗（插入連結只有 2 個欄位、表格/顏色/表情選單走完全不同的版面，不受影響）。
 */
.w-e-modal .babel-container:nth-of-type(3) {
  display: none !important;
}

.img-thumb {
  width: 60px;
  height: 60px;
  flex-shrink: 0;
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
</style>
