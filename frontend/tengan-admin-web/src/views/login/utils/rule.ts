import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 密碼正則（密碼格式應為8-18位數字、字母、符號的任意兩種組合） */
export const REGEXP_PWD =
  /^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?!([^(0-9a-zA-Z)]|[()])+$)(?!^.*[\u4E00-\u9FA5].*$)([^(0-9a-zA-Z)]|[()]|[a-z]|[A-Z]|[0-9]){8,18}$/;

/** 登入校驗 */
const loginRules = reactive<FormRules>({
  password: [
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("請輸入密碼"));
        } else if (!REGEXP_PWD.test(value)) {
          callback(
            new Error("密碼格式應為8-18位數字、字母、符號的任意兩種組合")
          );
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
});

export { loginRules };
