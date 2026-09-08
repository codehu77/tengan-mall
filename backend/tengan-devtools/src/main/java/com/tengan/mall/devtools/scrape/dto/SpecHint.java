package com.tengan.mall.devtools.scrape.dto;

/** MOMO 商品規格表的一筆（例如 顏色=橘色、藍色、銀色）。只當提示顯示給使用者參考，
 * 不會自動對應到自家系統的 attrId——分類/規格屬性一律由使用者自己選。 */
public record SpecHint(String name, String value) {
}
