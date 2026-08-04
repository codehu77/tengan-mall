<script setup lang="ts">
import { hasAuth, getAuths } from "@/router/utils";

defineOptions({
  name: "PermissionButtonRouter"
});
</script>

<template>
  <div>
    <p class="mb-2!">當前擁有的code列表：{{ getAuths() }}</p>

    <el-card shadow="never" class="mb-2">
      <template #header>
        <div class="card-header">元件方式判斷許可權</div>
      </template>
      <el-space wrap>
        <Auth value="permission:btn:add">
          <el-button plain type="warning">
            擁有code：'permission:btn:add' 許可權可見
          </el-button>
        </Auth>
        <Auth :value="['permission:btn:edit']">
          <el-button plain type="primary">
            擁有code：['permission:btn:edit'] 許可權可見
          </el-button>
        </Auth>
        <Auth
          :value="[
            'permission:btn:add',
            'permission:btn:edit',
            'permission:btn:delete'
          ]"
        >
          <el-button plain type="danger">
            擁有code：['permission:btn:add', 'permission:btn:edit',
            'permission:btn:delete'] 許可權可見
          </el-button>
        </Auth>
      </el-space>
    </el-card>

    <el-card shadow="never" class="mb-2">
      <template #header>
        <div class="card-header">函式方式判斷許可權</div>
      </template>
      <el-space wrap>
        <el-button v-if="hasAuth('permission:btn:add')" plain type="warning">
          擁有code：'permission:btn:add' 許可權可見
        </el-button>
        <el-button v-if="hasAuth(['permission:btn:edit'])" plain type="primary">
          擁有code：['permission:btn:edit'] 許可權可見
        </el-button>
        <el-button
          v-if="
            hasAuth([
              'permission:btn:add',
              'permission:btn:edit',
              'permission:btn:delete'
            ])
          "
          plain
          type="danger"
        >
          擁有code：['permission:btn:add', 'permission:btn:edit',
          'permission:btn:delete'] 許可權可見
        </el-button>
      </el-space>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          指令方式判斷許可權（該方式不能動態修改許可權）
        </div>
      </template>
      <el-space wrap>
        <el-button v-auth="'permission:btn:add'" plain type="warning">
          擁有code：'permission:btn:add' 許可權可見
        </el-button>
        <el-button v-auth="['permission:btn:edit']" plain type="primary">
          擁有code：['permission:btn:edit'] 許可權可見
        </el-button>
        <el-button
          v-auth="[
            'permission:btn:add',
            'permission:btn:edit',
            'permission:btn:delete'
          ]"
          plain
          type="danger"
        >
          擁有code：['permission:btn:add', 'permission:btn:edit',
          'permission:btn:delete'] 許可權可見
        </el-button>
      </el-space>
    </el-card>
  </div>
</template>
