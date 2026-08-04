<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type BrandItem,
  getBrandList,
  createBrand,
  updateBrand,
  deleteBrand,
  showBrand,
  hideBrand
} from "@/api/productBrand";
import brandForm from "./form.vue";

defineOptions({
  name: "ProductBrand"
});

const loading = ref(true);
const dataList = ref<Array<BrandItem>>([]);

const columns: TableColumns[] = [
  { label: "品牌名稱", prop: "name", minWidth: 140 },
  { label: "Logo", prop: "logo", minWidth: 120, slot: "logo" },
  { label: "簡介", prop: "descript", minWidth: 160 },
  { label: "首字母", prop: "firstLetter", minWidth: 80 },
  { label: "排序", prop: "sort", minWidth: 80 },
  { label: "狀態", prop: "status", minWidth: 100, slot: "status" },
  { label: "操作", fixed: "right", width: 160, slot: "operation" }
];

async function onSearch() {
  loading.value = true;
  const { items } = await getBrandList();
  dataList.value = items;
  loading.value = false;
}

const formRef = ref();

function openBrandDialog(mode: "create" | "edit", row?: BrandItem) {
  const formInline = {
    name: row?.name ?? "",
    logo: row?.logo ?? "",
    descript: row?.descript ?? "",
    firstLetter: row?.firstLetter ?? "",
    sort: row?.sort ?? 0
  };

  addDialog({
    title: mode === "create" ? "新增品牌" : "編輯品牌",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(brandForm, { ref: formRef, mode, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = formRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const action =
          mode === "create"
            ? createBrand(formInline)
            : updateBrand(row!.id, formInline);
        action
          .then(() => {
            message(mode === "create" ? "新增成功" : "修改成功", {
              type: "success"
            });
            done();
            onSearch();
          })
          .catch(() => closeLoading());
      });
    }
  });
}

function onDelete(row: BrandItem) {
  ElMessageBox.confirm(`確定要刪除「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    deleteBrand(row.id)
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

function onToggleStatus(row: BrandItem) {
  const nextVisible = row.status !== 1;
  ElMessageBox.confirm(
    `確定要${nextVisible ? "顯示" : "隱藏"}品牌「${row.name}」嗎？`,
    "提示",
    { type: "warning" }
  ).then(() => {
    const action = nextVisible ? showBrand(row.id) : hideBrand(row.id);
    action.then(() => {
      message("操作成功", { type: "success" });
      onSearch();
    });
  });
}

onMounted(() => {
  onSearch();
});
</script>

<template>
  <div class="main">
    <PureTableBar title="品牌列表" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openBrandDialog('create')">
          新增品牌
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
        >
          <template #logo="{ row }">
            <el-image
              v-if="row.logo"
              :src="row.logo"
              style="width: 60px; height: 24px"
              fit="contain"
            />
            <span v-else class="text-gray-400">—</span>
          </template>
          <template #status="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="plain">
              {{ row.status === 1 ? "顯示" : "隱藏" }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openBrandDialog('edit', row)">
              編輯
            </el-button>
            <el-button
              link
              :type="row.status === 1 ? 'info' : 'success'"
              @click="onToggleStatus(row)"
            >
              {{ row.status === 1 ? "隱藏" : "顯示" }}
            </el-button>
            <el-button link type="danger" @click="onDelete(row)">
              刪除
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
