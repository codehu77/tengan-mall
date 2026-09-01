package com.tengan.mall.auth.application.password;

import com.tengan.mall.auth.application.port.OtpCodeStorePort;
import com.tengan.mall.auth.application.port.VerificationTokenStorePort;
import com.tengan.mall.auth.domain.exception.InvalidOtpCodeException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import java.time.Duration;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 重新解析一次帳號資格（跟 ForgotPasswordService 同樣的「有密碼+有手機」判斷）——帳號不存在或
 * 不合資格時，丟出跟「驗證碼錯誤」完全相同的例外，讓失敗無法跟「帳號不存在」區分，防止列舉。
 */
@Service
public class ForgotPasswordVerifyService implements ForgotPasswordVerifyUseCase {

    private static final Duration TOKEN_TTL = Duration.ofMinutes(10);

    private final AccountRepository accountRepository;
    private final OtpCodeStorePort otpCodeStorePort;
    private final VerificationTokenStorePort verificationTokenStorePort;

    public ForgotPasswordVerifyService(AccountRepository accountRepository, OtpCodeStorePort otpCodeStorePort,
            VerificationTokenStorePort verificationTokenStorePort) {
        this.accountRepository = accountRepository;
        this.otpCodeStorePort = otpCodeStorePort;
        this.verificationTokenStorePort = verificationTokenStorePort;
    }

    @Override
    public ForgotPasswordVerifyResult verify(ForgotPasswordVerifyCommand command) {
        Account account = accountRepository.findByIdentifier(command.identifier()).orElse(null);
        if (account == null || account.getPasswordHash() == null || account.getPhone() == null) {
            throw new InvalidOtpCodeException();
        }

        boolean matched = otpCodeStorePort.verifyAndConsume(
                account.getPhone().value(), ForgotPasswordService.OTP_PURPOSE, command.code());
        if (!matched) {
            throw new InvalidOtpCodeException();
        }

        String resetToken = verificationTokenStorePort.issue(
                Map.of("accountId", account.getId().value().toString()), TOKEN_TTL);
        return new ForgotPasswordVerifyResult(resetToken);
    }
}
