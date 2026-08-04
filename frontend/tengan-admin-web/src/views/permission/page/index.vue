<script setup lang="ts">
import { initRouter } from "@/router/utils";
import { storageLocal } from "@pureadmin/utils";
import { type CSSProperties, ref, computed } from "vue";
import { useUserStoreHook } from "@/store/modules/user";
import { usePermissionStoreHook } from "@/store/modules/permission";

defineOptions({
  name: "PermissionPage"
});

const elStyle = computed((): CSSProperties => {
  return {
    width: "85vw",
    justifyContent: "start"
  };
});

const username = ref(useUserStoreHook()?.username);

const options = [
  {
    value: "admin",
    label: "管理員角色"
  },
  {
    value: "common",
    label: "普通角色"
  }
];

// 這是 pure-admin 範本內建的角色切換 demo，帳密是寫死的示範資料，不是真的串接後端帳號系統，
// 跟 tengan-admin 的 RBAC 骨架無關（真正的登入走 src/views/login/index.vue）。
function onChange() {
  useUserStoreHook()
    .loginByUsername({ username: username.value, password: "admin123" })
    .then(() => {
      storageLocal().removeItem("async-routes");
      usePermissionStoreHook().clearAllCachePage();
      initRouter();
    });
}
</script>

<template>
  <div>
    <p class="mb-2!">
      模擬後臺根據不同角色返回對應路由，觀察左側選單變化（管理員角色可檢視系統管理選單、普通角色不可檢視系統管理選單）
    </p>
    <el-card shadow="never" :style="elStyle">
      <template #header>
        <div class="card-header">
          <span>當前角色：{{ username }}</span>
        </div>
      </template>
      <el-select v-model="username" class="w-[160px]!" @change="onChange">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-card>
  </div>
</template>
