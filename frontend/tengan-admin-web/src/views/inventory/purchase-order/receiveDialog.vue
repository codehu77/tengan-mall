<script setup lang="ts">
defineOptions({
  name: "PurchaseOrderReceiveDialog"
});

interface ReceiveItemRow {
  itemId: number;
  skuId: number;
  orderedQty: number;
  receivedQty: number;
}

interface DialogProps {
  items: Array<ReceiveItemRow>;
  readonly?: boolean;
}

const props = withDefaults(defineProps<DialogProps>(), {
  items: () => [],
  readonly: false
});
</script>

<template>
  <el-table :data="props.items" border>
    <el-table-column prop="skuId" label="SKU ID" min-width="120" />
    <el-table-column prop="orderedQty" label="訂購數量" min-width="100" />
    <el-table-column label="收貨數量" min-width="140">
      <template #default="{ row }">
        <span v-if="props.readonly">{{ row.receivedQty }}</span>
        <el-input-number v-else v-model="row.receivedQty" :min="0" />
      </template>
    </el-table-column>
  </el-table>
</template>
