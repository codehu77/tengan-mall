<script setup lang="ts">
import { ref, computed } from "vue";
import type { FormInstance } from "element-plus";
import type { SkuItemInput } from "@/api/seckillActivity";

defineOptions({
  name: "SeckillActivitySkusForm"
});

interface FormProps {
  initialSkus: Array<SkuItemInput>;
}

const props = withDefaults(defineProps<FormProps>(), {
  initialSkus: () => []
});

const ruleFormRef = ref<FormInstance>();
const rows = ref<Array<SkuItemInput>>(
  props.initialSkus.length > 0
    ? props.initialSkus.map(item => ({ ...item }))
    : [{ skuId: 0, seckillPrice: 0, seckillCount: 1, limitPerUser: 1 }]
);

/** el-form 需要一個 :model，這裡沒有欄位層級驗證規則，純粹讓 ref="ruleFormRef" 有東西可綁。 */
const formModel = computed(() => ({ rows: rows.value }));

function addRow() {
  rows.value.push({ skuId: 0, seckillPrice: 0, seckillCount: 1, limitPerUser: 1 });
}

function removeRow(index: number) {
  rows.value.splice(index, 1);
}

function getRef() {
  return ruleFormRef.value;
}

/** 送出前過濾掉還沒填 SKU ID 的空白列。 */
function getSkuItems(): Array<SkuItemInput> {
  return rows.value.filter(row => row.skuId > 0);
}

defineExpose({ getRef, getSkuItems });
</script>

<template>
  <el-form ref="ruleFormRef" :model="formModel">
    <el-row :gutter="8" class="sku-row-header">
      <el-col :span="6">SKU ID</el-col>
      <el-col :span="6">秒殺價</el-col>
      <el-col :span="5">配額</el-col>
      <el-col :span="5">每人限購</el-col>
    </el-row>
    <el-row
      v-for="(row, index) in rows"
      :key="index"
      :gutter="8"
      class="sku-row"
    >
      <el-col :span="6">
        <el-input-number
          v-model="row.skuId"
          :min="1"
          :controls="false"
          style="width: 100%"
        />
      </el-col>
      <el-col :span="6">
        <el-input-number
          v-model="row.seckillPrice"
          :min="0"
          :precision="2"
          :controls="false"
          style="width: 100%"
        />
      </el-col>
      <el-col :span="5">
        <el-input-number
          v-model="row.seckillCount"
          :min="1"
          :controls="false"
          style="width: 100%"
        />
      </el-col>
      <el-col :span="5">
        <el-input-number
          v-model="row.limitPerUser"
          :min="1"
          :controls="false"
          style="width: 100%"
        />
      </el-col>
      <el-col :span="2">
        <el-button link type="danger" @click="removeRow(index)">移除</el-button>
      </el-col>
    </el-row>
    <el-button type="primary" link @click="addRow">+ 新增商品</el-button>
  </el-form>
</template>

<style scoped>
.sku-row-header {
  margin-bottom: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.sku-row {
  margin-bottom: 8px;
}
</style>
