package com.tengan.mall.auth.application.password;

import com.tengan.mall.auth.application.port.OtpCodeStorePort;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 帳號存在+有密碼(password_hash非null)+有綁定手機才真的發 OTP；純 OAuth 帳號或無手機的帳號
 * 靜默跳過（見 login_register_redesign「純 OAuth 帳號沒有密碼可忘記」）。不丟例外，避免帳號列舉。
 */
@Service
public class ForgotPasswordService implements ForgotPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordService.class);
    static final String OTP_PURPOSE = "RESET_PASSWORD";

    private final AccountRepository accountRepository;
    private final OtpCodeStorePort otpCodeStorePort;

    public ForgotPasswordService(AccountRepository accountRepository, OtpCodeStorePort otpCodeStorePort) {
        this.accountRepository = accountRepository;
        this.otpCodeStorePort = otpCodeStorePort;
    }

    @Override
    public void forgot(ForgotPasswordCommand command) {
        Account account = accountRepository.findByIdentifier(command.identifier()).orElse(null);
        if (account == null || account.getPasswordHash() == null || account.getPhone() == null) {
            return;
        }

        String code = otpCodeStorePort.generateAndStore(account.getPhone().value(), OTP_PURPOSE);
        log.info("[展示模式] 忘記密碼驗證碼 phone={} code={}", account.getPhone().value(), code);
    }
}
