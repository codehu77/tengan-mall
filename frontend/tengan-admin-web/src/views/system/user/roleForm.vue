<script setup lang="ts">
import { ref } from "vue";
import type { RoleItem } from "@/api/role";

defineOptions({
  name: "AdminUserRoleForm"
});

interface FormProps {
  roles: Array<RoleItem>;
  checkedRoleIds: Array<number>;
}

const props = withDefaults(defineProps<FormProps>(), {
  roles: () => [],
  checkedRoleIds: () => []
});

const checked = ref<Array<number>>([...props.checkedRoleIds]);

function getCheckedRoleIds(): number[] {
  return checked.value;
}

defineExpose({ getCheckedRoleIds });
</script>

<template>
  <el-checkbox-group v-model="checked">
    <el-checkbox
      v-for="role in roles"
      :key="role.id"
      :value="role.id"
      :label="role.id"
      :disabled="role.status !== 1"
      border
      style="margin: 0 8px 8px 0"
    >
      {{ role.roleName }}
      <span v-if="role.status !== 1" class="text-gray-400">（已停用）</span>
    </el-checkbox>
  </el-checkbox-group>
  <el-empty v-if="roles.length === 0" description="目前沒有任何角色" />
</template>
