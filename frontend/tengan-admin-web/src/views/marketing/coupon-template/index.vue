<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type TemplateItem,
  getTemplateList,
  createTemplate,
  updateTemplate,
  delistTemplate,
  grantCoupons
} from "@/api/couponTemplate";
import templateForm from "./form.vue";
import grantForm from "./grantForm.vue";

defineOptions({
  name: "MarketingCouponTemplate"
});

const loading = ref(true);
const dataList = ref<Array<TemplateItem>>([]);

const columns: TableColumns[] = [
  { label: "模板名稱", prop: "name", minWidth: 140 },
  { label: "門檻金額", prop: "thresholdAmount", minWidth: 100 },
  { label: "折抵金額", prop: "discountAmount", minWidth: 100 },
  { label: "已核發/上限", minWidth: 120, formatter: row => `${row.issuedCount}/${row.totalCount}` },
  { label: "有效期間", minWidth: 220, formatter: row => `${formatTime(row.effectiveStart)} ~ ${formatTime(row.effectiveEnd)}` },
  { label: "狀態", prop: "status", minWidth: 90, slot: "status" },
  { label: "操作", fixed: "right", width: 220, slot: "operation" }
];

function formatTime(iso: string) {
  return new Date(iso).toLocaleString("zh-TW", { hour12: false });
}

async function onSearch() {
  loading.value = true;
  const { items } = await getTemplateList();
  dataList.value = items;
  loading.value = false;
}

const formRef = ref();

function openTemplateDialog(mode: "create" | "edit", row?: TemplateItem) {
  const formInline = {
    name: row?.name ?? "",
    thresholdAmount: row?.thresholdAmount ?? 0,
    discountAmount: row?.discountAmount ?? 0,
    totalCount: row?.totalCount ?? 100,
    effectiveStart: row?.effectiveStart ?? "",
    effectiveEnd: row?.effectiveEnd ?? ""
  };

  addDialog({
    title: mode === "create" ? "新增優惠券模板" : "編輯優惠券模板",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(templateForm, { ref: formRef, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = formRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const action =
          mode === "create"
            ? createTemplate(formInline)
            : updateTemplate(row!.id, formInline);
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

function onDelist(row: TemplateItem) {
  ElMessageBox.confirm(`確定要下架「${row.name}」嗎？`, "提示", {
    type: "warning"
  }).then(() => {
    delistTemplate(row.id).then(() => {
      message("下架成功", { type: "success" });
      onSearch();
    });
  });
}

const grantFormRef = ref();

function openGrantDialog(row: TemplateItem) {
  addDialog({
    title: "核發優惠券",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(grantForm, { ref: grantFormRef, templateName: row.name }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = grantFormRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const userIds = grantFormRef.value.getUserIds();
        grantCoupons({ templateId: row.id, userIds })
          .then(({ succeededUserIds, skippedUserIds }) => {
            message(
              `核發成功 ${succeededUserIds.length} 位${skippedUserIds.length ? `，額度已滿跳過 ${skippedUserIds.length} 位` : ""}`,
              { type: "success" }
            );
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
    <PureTableBar title="優惠券模板" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openTemplateDialog('create')">
          新增模板
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
          <template #status="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="plain">
              {{ row.status === 1 ? "上架中" : "已下架" }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openTemplateDialog('edit', row)">
              編輯
            </el-button>
            <el-button link type="success" @click="openGrantDialog(row)">
              核發
            </el-button>
            <el-button
              v-if="row.status === 1"
              link
              type="danger"
              @click="onDelist(row)"
            >
              下架
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
