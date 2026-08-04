import dayjs from "dayjs";
import { readdir, stat } from "node:fs";
import { fileURLToPath } from "node:url";
import { dirname, resolve } from "node:path";
import { sum, formatBytes } from "@pureadmin/utils";
import {
  name,
  version,
  engines,
  dependencies,
  devDependencies
} from "../package.json";

/** 啟動`node`程序時所在工作目錄的絕對路徑 */
const root: string = process.cwd();

/**
 * @description 根據可選的路徑片段生成一個新的絕對路徑
 * @param dir 路徑片段，預設`build`
 * @param metaUrl 模組的完整`url`，如果在`build`目錄外呼叫必傳`import.meta.url`
 */
const pathResolve = (dir = ".", metaUrl = import.meta.url) => {
  // 當前檔案目錄的絕對路徑
  const currentFileDir = dirname(fileURLToPath(metaUrl));
  // build 目錄的絕對路徑
  const buildDir = resolve(currentFileDir, "build");
  // 解析的絕對路徑
  const resolvedPath = resolve(currentFileDir, dir);
  // 檢查解析的絕對路徑是否在 build 目錄內
  if (resolvedPath.startsWith(buildDir)) {
    // 在 build 目錄內，返回當前檔案路徑
    return fileURLToPath(metaUrl);
  }
  // 不在 build 目錄內，返回解析後的絕對路徑
  return resolvedPath;
};

/** 設定別名 */
const alias: Record<string, string> = {
  "@": pathResolve("../src"),
  "@build": pathResolve()
};

/** 平臺的名稱、版本、執行所需的`node`和`pnpm`版本、依賴、最後構建時間的型別提示 */
const __APP_INFO__ = {
  pkg: { name, version, engines, dependencies, devDependencies },
  lastBuildTime: dayjs(new Date()).format("YYYY-MM-DD HH:mm:ss")
};

/** 處理環境變數 */
const wrapperEnv = (envConf: Recordable): ViteEnv => {
  // 預設值
  const ret: ViteEnv = {
    VITE_PORT: 8090,
    VITE_PUBLIC_PATH: "",
    VITE_ROUTER_HISTORY: "",
    VITE_CDN: false,
    VITE_HIDE_HOME: "false",
    VITE_COMPRESSION: "none"
  };

  for (const envName of Object.keys(envConf)) {
    let realName = envConf[envName].replace(/\\n/g, "\n");
    realName =
      realName === "true" ? true : realName === "false" ? false : realName;

    if (envName === "VITE_PORT") {
      realName = Number(realName);
    }
    ret[envName] = realName;
    if (typeof realName === "string") {
      process.env[envName] = realName;
    } else if (typeof realName === "object") {
      process.env[envName] = JSON.stringify(realName);
    }
  }
  return ret;
};

const fileListTotal: number[] = [];

/** 獲取指定資料夾中所有檔案的總大小 */
const getPackageSize = options => {
  const { folder = "dist", callback, format = true } = options;
  readdir(folder, (err, files: string[]) => {
    if (err) throw err;
    let count = 0;
    const checkEnd = () => {
      ++count == files.length &&
        callback(format ? formatBytes(sum(fileListTotal)) : sum(fileListTotal));
    };
    files.forEach((item: string) => {
      stat(`${folder}/${item}`, async (err, stats) => {
        if (err) throw err;
        if (stats.isFile()) {
          fileListTotal.push(stats.size);
          checkEnd();
        } else if (stats.isDirectory()) {
          getPackageSize({
            folder: `${folder}/${item}/`,
            callback: checkEnd
          });
        }
      });
    });
    files.length === 0 && callback(0);
  });
};

export { root, pathResolve, alias, __APP_INFO__, wrapperEnv, getPackageSize };
