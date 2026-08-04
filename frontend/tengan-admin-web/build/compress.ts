import type { Plugin } from "vite";
import { isArray } from "@pureadmin/utils";
import compressPlugin from "vite-plugin-compression";

export const configCompressPlugin = (
  compress: ViteCompression
): Plugin | Plugin[] => {
  if (compress === "none") return null;

  const gz = {
    // 生成的壓縮包字尾
    ext: ".gz",
    // 體積大於threshold才會被壓縮
    threshold: 0,
    // 預設壓縮.js|mjs|json|css|html字尾檔案，設定成true，壓縮全部檔案
    filter: () => true,
    // 壓縮後是否刪除原始檔案
    deleteOriginFile: false
  };
  const br = {
    ext: ".br",
    algorithm: "brotliCompress",
    threshold: 0,
    filter: () => true,
    deleteOriginFile: false
  };

  const codeList = [
    { k: "gzip", v: gz },
    { k: "brotli", v: br },
    { k: "both", v: [gz, br] }
  ];

  const plugins: Plugin[] = [];

  codeList.forEach(item => {
    if (compress.includes(item.k)) {
      if (compress.includes("clear")) {
        if (isArray(item.v)) {
          item.v.forEach(vItem => {
            plugins.push(
              compressPlugin(Object.assign(vItem, { deleteOriginFile: true }))
            );
          });
        } else {
          plugins.push(
            compressPlugin(Object.assign(item.v, { deleteOriginFile: true }))
          );
        }
      } else {
        if (isArray(item.v)) {
          item.v.forEach(vItem => {
            plugins.push(compressPlugin(vItem));
          });
        } else {
          plugins.push(compressPlugin(item.v));
        }
      }
    }
  });

  return plugins;
};
