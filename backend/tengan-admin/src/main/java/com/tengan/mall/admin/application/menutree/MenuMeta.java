package com.tengan.mall.admin.application.menutree;

import java.util.List;

/** 對齊 pure-admin 動態路由期待的 meta 形狀（title/icon/rank/auths）。 */
public record MenuMeta(String title, String icon, int rank, List<String> auths) {
}
