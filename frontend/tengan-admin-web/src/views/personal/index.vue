<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import { storageLocal } from "@pureadmin/utils";
import { message } from "@/utils/message";
import { userKey, type DataInfo } from "@/utils/auth";
import { useUserStoreHook } from "@/store/modules/user";
import { getMyProfile, updateMyProfile, changeMyPassword } from "@/api/user";
import ImageUploader from "@/components/ImageUploader/index.vue";

defineOptions({
  name: "Personal"
});

const profileFormRef = ref<FormInstance>();
const profileSaving = ref(false);
const profileForm = reactive({
  username: "",
  realName: "",
  avatarUrl: ""
});

const profileRules: FormRules = {
  realName: [{ required: true, message: "請輸入姓名", trigger: "blur" }]
};

async function loadProfile() {
  const result = await getMyProfile();
  profileForm.username = result.username;
  profileForm.realName = result.realName;
  profileForm.avatarUrl = result.avatarUrl ?? "";
}

function saveProfile() {
  profileFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return;
    profileSaving.value = true;
    try {
      await updateMyProfile({
        realName: profileForm.realName,
        avatarUrl: profileForm.avatarUrl
      });
      // 直接改 store + localStorage，不能借用 setToken——那支是為了整組登入資訊
      // （含 accessToken/expires）設計的，這裡只改頭像/暱稱會把 token 相關欄位洗成不合法的值。
      useUserStoreHook().SET_AVATAR(profileForm.avatarUrl);
      useUserStoreHook().SET_NICKNAME(profileForm.realName);
      const cached = storageLocal().getItem<DataInfo<number>>(userKey);
      if (cached) {
        storageLocal().setItem(userKey, {
          ...cached,
          avatar: profileForm.avatarUrl,
          nickname: profileForm.realName
        });
      }
      message("個人資訊已更新", { type: "success" });
    } finally {
      profileSaving.value = false;
    }
  });
}

const passwordFormRef = ref<FormInstance>();
const passwordSaving = ref(false);
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: ""
});

function validateConfirmPassword(_rule, value: string, callback) {
  if (value !== passwordForm.newPassword) {
    callback(new Error("兩次輸入的密碼不一致"));
  } else {
    callback();
  }
}

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: "請輸入目前密碼", trigger: "blur" }],
  newPassword: [
    { required: true, message: "請輸入新密碼", trigger: "blur" },
    { min: 8, message: "新密碼至少 8 碼", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, message: "請再次輸入新密碼", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" }
  ]
};

function savePassword() {
  passwordFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return;
    passwordSaving.value = true;
    try {
      await changeMyPassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      });
      message("密碼已修改", { type: "success" });
      passwordForm.oldPassword = "";
      passwordForm.newPassword = "";
      passwordForm.confirmPassword = "";
      passwordFormRef.value.clearValidate();
    } finally {
      passwordSaving.value = false;
    }
  });
}

onMounted(() => {
  loadProfile();
});
</script>

<template>
  <div class="main">
    <el-card class="mb-4" header="個人資訊">
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
        style="max-width: 480px"
      >
        <el-form-item label="頭像">
          <ImageUploader v-model="profileForm.avatarUrl" />
        </el-form-item>
        <el-form-item label="帳號">
          <el-input :model-value="profileForm.username" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="profileForm.realName" placeholder="請輸入姓名" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="profileSaving" @click="saveProfile">
            儲存
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card header="修改密碼">
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
        style="max-width: 480px"
      >
        <el-form-item label="目前密碼" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="請輸入目前密碼"
          />
        </el-form-item>
        <el-form-item label="新密碼" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="至少 8 碼"
          />
        </el-form-item>
        <el-form-item label="確認新密碼" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="請再次輸入新密碼"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="passwordSaving" @click="savePassword">
            修改密碼
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
