<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type WarehouseItem,
  getWarehouseList,
  createWarehouse
} from "@/api/inventoryWarehouse";
import warehouseForm from "./form.vue";

defineOptions({
  name: "InventoryWarehouse"
});

const loading = ref(true);
const dataList = ref<Array<WarehouseItem>>([]);

const columns: TableColumns[] = [
  { label: "倉庫名稱", prop: "name", minWidth: 140 },
  { label: "地址", prop: "address", minWidth: 200 }
];

async function onSearch() {
  loading.value = true;
  const { items } = await getWarehouseList();
  dataList.value = items;
  loading.value = false;
}

const formRef = ref();

function openWarehouseDialog() {
  const formInline = { name: "", address: "" };

  addDialog({
    title: "新增倉庫",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(warehouseForm, { ref: formRef, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = formRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        createWarehouse(formInline)
          .then(() => {
            message("新增成功", { type: "success" });
            done();
            onSearch();
          })
          .catch(() => closeLoading());
      });
    }
  });
}

onMounted(() => {
  onSearch();
});
</script>

<template>
  <div class="main">
    <PureTableBar title="倉庫列表" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openWarehouseDialog()">
          新增倉庫
        </el-button>
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <pure-table
          border
          adaptive
          :size="size"
          :data="dataList"
          :columns="dynamicColumns"
          :loading="loading"
          row-key="id"
        />
      </template>
    </PureTableBar>
  </div>
</template>
