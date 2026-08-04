<script setup lang="ts">
import { h, ref, onMounted } from "vue";
import { ElMessageBox } from "element-plus";
import { message } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { PureTableBar } from "@/components/RePureTableBar";
import type { TableColumns } from "@pureadmin/table";
import {
  type RoleItem,
  getRoleList,
  getRoleDetail,
  createRole,
  updateRole,
  assignRoleMenus
} from "@/api/role";
import { type MenuTreeItem, getMenuList } from "@/api/menu";
import roleForm from "./form.vue";
import roleMenuForm from "./menuForm.vue";

defineOptions({
  name: "SystemRole"
});

const loading = ref(true);
const dataList = ref<Array<RoleItem>>([]);

const columns: TableColumns[] = [
  { label: "角色代碼", prop: "roleCode", minWidth: 140 },
  { label: "角色名稱", prop: "roleName", minWidth: 140 },
  { label: "狀態", prop: "status", minWidth: 100, slot: "status" },
  {
    label: "操作",
    fixed: "right",
    width: 260,
    slot: "operation"
  }
];

async function onSearch() {
  loading.value = true;
  const { items } = await getRoleList();
  dataList.value = items;
  loading.value = false;
}

const formRef = ref();

/** 新增/編輯角色（roleCode 建立後不可改，見 form.vue 的欄位停用邏輯） */
function openRoleDialog(mode: "create" | "edit", row?: RoleItem) {
  const formInline = {
    roleCode: row?.roleCode ?? "",
    roleName: row?.roleName ?? "",
    active: row ? row.status === 1 : true
  };

  addDialog({
    title: mode === "create" ? "新增角色" : "編輯角色",
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(roleForm, { ref: formRef, mode, formInline }),
    beforeSure: (done, { closeLoading }) => {
      const FormRef = formRef.value.getRef();
      FormRef.validate((valid: boolean) => {
        if (!valid) {
          closeLoading();
          return;
        }
        const action =
          mode === "create"
            ? createRole({
                roleCode: formInline.roleCode,
                roleName: formInline.roleName
              })
            : updateRole(row!.id, {
                roleName: formInline.roleName,
                active: formInline.active
              });
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

function onToggleStatus(row: RoleItem) {
  const nextActive = row.status !== 1;
  ElMessageBox.confirm(
    `確定要${nextActive ? "啟用" : "停用"}角色「${row.roleName}」嗎？`,
    "提示",
    { type: "warning" }
  ).then(() => {
    updateRole(row.id, { roleName: row.roleName, active: nextActive }).then(
      () => {
        message("操作成功", { type: "success" });
        onSearch();
      }
    );
  });
}

/** 選單授權：先撈完整選單樹 + 這個角色目前的 menuIds，開 el-tree 勾選對話框。 */
async function openMenuDialog(row: RoleItem) {
  const [{ items: menuTree }, detail] = await Promise.all([
    getMenuList(),
    getRoleDetail(row.id)
  ]);

  addDialog({
    title: `選單授權：${row.roleName}`,
    width: "36%",
    draggable: true,
    closeOnClickModal: false,
    contentRenderer: () =>
      h(roleMenuForm, {
        ref: formRef,
        menuTree: menuTree as Array<MenuTreeItem>,
        checkedKeys: detail.menuIds
      }),
    beforeSure: (done, { closeLoading }) => {
      const checkedKeys = formRef.value.getCheckedKeys();
      assignRoleMenus(row.id, checkedKeys)
        .then(() => {
          message("選單授權已更新", { type: "success" });
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
    <PureTableBar title="角色列表" :columns="columns" @refresh="onSearch">
      <template #buttons>
        <el-button type="primary" @click="openRoleDialog('create')">
          新增角色
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
              {{ row.status === 1 ? "啟用" : "停用" }}
            </el-tag>
          </template>
          <template #operation="{ row }">
            <el-button link type="primary" @click="openRoleDialog('edit', row)">
              編輯
            </el-button>
            <el-button link type="primary" @click="openMenuDialog(row)">
              選單授權
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
