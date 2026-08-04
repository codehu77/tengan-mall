package com.tengan.mall.admin.domain.exception;

/**
 * 帳密正確但帳號被停用——跟 {@link InvalidAdminCredentialsException} 分開，因為這不是
 * 猜密碼場景，明確告知「已停用」不會洩漏任何攻擊者能利用的資訊。
 */
public class AdminAccountDisabledException extends RuntimeException {

    public AdminAccountDisabledException() {
        super("帳號已停用");
    }
}
