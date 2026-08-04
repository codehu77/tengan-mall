import forage from "localforage";
import type { LocalForage, ProxyStorage, ExpiresData } from "./types.d";

class StorageProxy implements ProxyStorage {
  protected storage: LocalForage;
  constructor(storageModel) {
    this.storage = storageModel;
    this.storage.config({
      // 首選IndexedDB作為第一驅動，不支援IndexedDB會自動降級到localStorage（WebSQL被棄用，詳情看https://developer.chrome.com/blog/deprecating-web-sql）
      driver: [this.storage.INDEXEDDB, this.storage.LOCALSTORAGE],
      name: "pure-admin"
    });
  }

  /**
   * @description 將對應鍵名的資料儲存到離線倉庫
   * @param k 鍵名
   * @param v 鍵值
   * @param m 快取時間（單位`分`，預設`0`分鐘，永久快取）
   */
  public async setItem<T>(k: string, v: T, m = 0): Promise<T> {
    return new Promise((resolve, reject) => {
      this.storage
        .setItem(k, {
          data: v,
          expires: m ? new Date().getTime() + m * 60 * 1000 : 0
        })
        .then(value => {
          resolve(value.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * @description 從離線倉庫中獲取對應鍵名的值
   * @param k 鍵名
   */
  public async getItem<T>(k: string): Promise<T> {
    return new Promise((resolve, reject) => {
      this.storage
        .getItem(k)
        .then((value: ExpiresData<T>) => {
          value && (value.expires > new Date().getTime() || value.expires === 0)
            ? resolve(value.data)
            : resolve(null);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * @description 從離線倉庫中刪除對應鍵名的值
   * @param k 鍵名
   */
  public async removeItem(k: string) {
    return new Promise<void>((resolve, reject) => {
      this.storage
        .removeItem(k)
        .then(() => {
          resolve();
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * @description 從離線倉庫中刪除所有的鍵名，重置資料庫
   */
  public async clear() {
    return new Promise<void>((resolve, reject) => {
      this.storage
        .clear()
        .then(() => {
          resolve();
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * @description 獲取資料倉儲中所有的key
   */
  public async keys() {
    return new Promise<string[]>((resolve, reject) => {
      this.storage
        .keys()
        .then(keys => {
          resolve(keys);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}

/**
 * 二次封裝 [localforage](https://localforage.docschina.org/) 支援設定過期時間，提供完整的型別提示
 */
export const localForage = () => new StorageProxy(forage);
