<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type BannerItem,
  getBannerList,
  createBanner,
  updateBanner,
  deleteBanner
} from "@/api/media";
import bannerForm from "./form.vue";

defineOptions({
  name: "MarketingBanner"
});

const loading = ref(true);
const dataList = ref<Array<BannerItem>>([]);

const columns: TableColumns[] = [
  { label: "圖片", minWidth: 140, slot: "image" },
  { label: "標題", prop: "title", minWidth: 120 },
  { label: "連結網址", prop: "linkUrl", minWidth: 160 },
  { label: "排序", prop: "sortOrder", minWidth: 80 },
  { label: "啟用", minWidth: 90, slot: "enabled" },
  { label: "操作", fixed: "right", width: 140, slot: "operation" }
];

async function onSearch() {
  loading.value = true;
  const { banners } = await getBannerList();
  dataList.value = banners;
  loading.value = false;
}

const formRef = ref();

function openBannerDialog(mode: "create" | "edit", row?: BannerItem) {
  const formInline = {
    imageUrl: row?.imageUrl ?? "",
    linkUrl: row?.linkUrl ?? "",
    title: row?.title ?? "",
    sortOrder: row?.sortOrder ?? 0,
    enabled: row?.enabled ?? true
  };

  addDialog({
    title: mode === "create" ? "新增輪播圖" : "編輯輪播圖",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(bannerForm, { ref: formRef, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = formRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const action =
          mode === "create"
            ? createBanner(formInline)
            : updateBanner(row!.id, formInline);
        action
          .then(() => {
            message(mode === "create" ? "新增成功" : "修改成功", {
              type: "success"
            });
            done();
            onSearch();
          })
          .catch((error: any) => {
            message(error?.response?.data?.message ?? "儲存失敗", {
              type: "error"
            });
            closeLoading();
          });
      });
    }
  });
}

function onDelete(row: BannerItem) {
  ElMessageBox.confirm(
    `確定要刪除輪播圖「${row.title || row.id}」嗎？`,
    "提示",
    { type: "warning" }
  ).then(() => {
    deleteBanner(row.id)
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

onMounted(() => {
  onSearch();
});
</script>

<template>
  <div class="main">
    <PureTableBar title="輪播圖管理" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openBannerDialog('create')">
          新增輪播圖
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
          <template #image="{ row }">
            <el-image
              :src="row.imageUrl"
              :preview-src-list="[row.imageUrl]"
              preview-teleported
              fit="cover"
              style="width: 120px; height: 68px; border-radius: 4px"
            />
          </template>
          <template #enabled="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" effect="plain">
              {{ row.enabled ? "啟用" : "停用" }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openBannerDialog('edit', row)">
              編輯
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
