<script setup lang="ts">
import { h, ref, reactive, onMounted } from "vue";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type AdminUserItem,
  getAdminUserList,
  getAdminUserDetail,
  createAdminUser,
  updateAdminUserStatus,
  assignAdminUserRoles
} from "@/api/adminUser";
import { type RoleItem, getRoleList } from "@/api/role";
import adminUserForm from "./form.vue";
import adminUserRoleForm from "./roleForm.vue";

defineOptions({
  name: "SystemUser"
});

const loading = ref(true);
const dataList = ref<Array<AdminUserItem>>([]);
const pagination = reactive({
  total: 0,
  pageSize: 10,
  currentPage: 1,
  background: true
});

const columns: TableColumns[] = [
  { label: "帳號", prop: "username", minWidth: 140 },
  { label: "真實姓名", prop: "realName", minWidth: 140 },
  { label: "狀態", prop: "status", minWidth: 100, slot: "status" },
  { label: "操作", fixed: "right", width: 220, slot: "operation" }
];

async function onSearch() {
  loading.value = true;
  const { items, total } = await getAdminUserList({
    pageNum: pagination.currentPage,
    pageSize: pagination.pageSize
  });
  dataList.value = items;
  pagination.total = total;
  loading.value = false;
}

function onPageSizeChange(size: number) {
  pagination.pageSize = size;
  onSearch();
}

function onPageCurrentChange(page: number) {
  pagination.currentPage = page;
  onSearch();
}

const formRef = ref();

function openCreateDialog() {
  const formInline = { username: "", password: "", realName: "" };

  addDialog({
    title: "新增管理員",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () => h(adminUserForm, { ref: formRef, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = formRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        createAdminUser(formInline)
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

function onToggleStatus(row: AdminUserItem) {
  const nextActive = row.status !== 1;
  updateAdminUserStatus(row.id, nextActive).then(() => {
    message("操作成功", { type: "success" });
    onSearch();
  });
}

/** 指派角色：先撈全部角色 + 這個管理員目前的 roleIds，開勾選對話框。 */
async function openRoleDialog(row: AdminUserItem) {
  const [{ items: roles }, detail] = await Promise.all([
    getRoleList(),
    getAdminUserDetail(row.id)
  ]);

  addDialog({
    title: `指派角色：${row.username}`,
    width: "30%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(adminUserRoleForm, {
        ref: formRef,
        roles: roles as Array<RoleItem>,
        checkedRoleIds: detail.roleIds
      }),
    beforeSure: (done, { closeLoading }) => {
      const roleIds = formRef.value.getCheckedRoleIds();
      assignAdminUserRoles(row.id, roleIds)
        .then(() => {
          message("角色指派已更新", { type: "success" });
          done();
        })
        .catch(() => closeLoading());
    }
  });
}

onMounted(() => {
  onSearch();
});
</script>

<template>
  <div class="main">
    <PureTableBar title="管理員列表" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openCreateDialog">
          新增管理員
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
          :pagination="pagination"
          row-key="id"
          @page-size-change="onPageSizeChange"
          @page-current-change="onPageCurrentChange"
        >
          <template #status="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="plain">
              {{ row.status === 1 ? "啟用" : "停用" }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openRoleDialog(row)">
              指派角色
            </el-button>
            <el-button
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="onToggleStatus(row)"
            >
              {{ row.status === 1 ? "停用" : "啟用" }}
            </el-button>
          </template>
        </pure-table>
      </template>
    </PureTableBar>
  </div>
</template>
