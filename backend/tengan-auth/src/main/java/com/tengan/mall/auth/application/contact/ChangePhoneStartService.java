package com.tengan.mall.auth.application.contact;

import com.tengan.mall.auth.application.port.OtpCodeStorePort;
import com.tengan.mall.auth.domain.exception.AccountNotFoundException;
import com.tengan.mall.auth.domain.exception.IdentifierAlreadyExistsException;
import com.tengan.mall.auth.domain.exception.InvalidCredentialsException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.model.Phone;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 換掉登入身份本身，風險等級跟「新增登入方式」（Phase 13 連結 Google）不同——一旦 session 被盜用，
 * 攻擊者能先改電話、再用忘記密碼流程換掉密碼，等於把真正的使用者鎖在外面。比照 Google/Facebook/
 * Firebase requires-recent-login 跟 OWASP 帳號安全指引，要求重新輸入目前密碼；純 OAuth 帳號
 * （沒有密碼）跳過這關，比照忘記密碼流程既有的判斷邏輯。
 */
@Service
public class ChangePhoneStartService implements ChangePhoneStartUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChangePhoneStartService.class);
    private static final String OTP_PURPOSE = "CHANGE_PHONE";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpCodeStorePort otpCodeStorePort;

    public ChangePhoneStartService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
            OtpCodeStorePort otpCodeStorePort) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpCodeStorePort = otpCodeStorePort;
    }

    @Override
    public ChangePhoneStartResult start(ChangePhoneStartCommand command) {
        Account account = accountRepository.findById(new AccountId(command.accountId()))
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        if (account.getPasswordHash() != null
                && !passwordEncoder.matches(command.currentPassword(), account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String newPhone = new Phone(command.newPhone()).value();
        accountRepository.findByIdentifier(newPhone)
                .filter(existing -> !existing.getId().value().equals(command.accountId()))
                .ifPresent(existing -> {
                    throw new IdentifierAlreadyExistsException(newPhone);
                });

        String code = otpCodeStorePort.generateAndStore(newPhone, OTP_PURPOSE);
        log.info("[展示模式] 換電話驗證碼 accountId={} newPhone={} code={}", command.accountId(), newPhone, code);
        return new ChangePhoneStartResult(code);
    }
}
