package com.tengan.mall.payment.infrastructure.ecpay;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * ECPay CheckMacValue 純函式（不依賴 Spring，方便單元測試不用起 context）。演算法（官方檢查碼機制文件
 * https://developers.ecpay.com.tw/29998/ + 官方 SDK 一致的作法）：
 * 1) 參數依 key 字母排序 2) 串成 key1=value1&key2=value2... 3) 前後包 HashKey=xxx&...&HashIV=xxx
 * 4) .NET 風格 URL encode（空白轉 + ，非英數字轉 %XX） 5) 轉小寫
 * 6) 把 %2d/%5f/%2e/%21/%2a/%28/%29 還原回 -_.!*()（.NET Uri.EscapeDataString 跟這支 API 的差異點）
 * 7) SHA256 雜湊 8) 轉大寫。
 *
 * <p>這個演算法已經用 Node crypto 實際跑過驗證（見對話紀錄 ecpay_checkmac_demo.js），{@link EcpayCheckMacValueCalculatorTest}
 * 直接拿那組輸入輸出當測試向量。同一支函式用在「組出送給 ECPay 的表單」跟「驗證 callback 簽章」兩處
 * ——驗證時把收到的參數（去掉 CheckMacValue 本身）傳進來重算一次，比對是否相等。</p>
 */
public final class EcpayCheckMacValueCalculator {

    private EcpayCheckMacValueCalculator() {
    }

    /** params 不可包含 CheckMacValue 本身。 */
    public static String calculate(Map<String, String> params, String hashKey, String hashIv) {
        SortedMap<String, String> sorted = new TreeMap<>(params);
        String kvString = sorted.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        String raw = "HashKey=" + hashKey + "&" + kvString + "&HashIV=" + hashIv;
        String encoded = dotNetUrlEncode(raw);
        String lower = encoded.toLowerCase();
        String reverted = revertReservedChars(lower);
        return sha256Hex(reverted).toUpperCase();
    }

    private static String dotNetUrlEncode(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-' || c == '_'
                    || c == '.') {
                sb.append(c);
            } else if (c == ' ') {
                sb.append('+');
            } else {
                for (byte b : String.valueOf(c).getBytes(StandardCharsets.UTF_8)) {
                    sb.append('%').append(String.format("%02X", b));
                }
            }
        }
        return sb.toString();
    }

    private static String revertReservedChars(String lower) {
        return lower.replace("%2d", "-").replace("%5f", "_").replace("%2e", ".").replace("%21", "!")
                .replace("%2a", "*").replace("%28", "(").replace("%29", ")");
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 演算法不存在", e);
        }
    }
}
