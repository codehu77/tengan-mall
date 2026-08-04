// 如果專案出現 `global is not defined` 報錯，可能是您引入某個庫的問題，比如 aws-sdk-js https://github.com/aws/aws-sdk-js
// 解決辦法就是將該檔案引入 src/main.ts 即可 import "@/utils/globalPolyfills";
if (typeof (window as any).global === "undefined") {
  (window as any).global = window;
}

export {};
