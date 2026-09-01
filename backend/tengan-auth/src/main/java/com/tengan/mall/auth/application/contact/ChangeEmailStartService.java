package com.tengan.mall.auth.application.contact;

import com.tengan.mall.auth.application.port.OtpCodeStorePort;
import com.tengan.mall.auth.domain.exception.AccountNotFoundException;
import com.tengan.mall.auth.domain.exception.IdentifierAlreadyExistsException;
import com.tengan.mall.auth.domain.exception.InvalidCredentialsException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.model.Email;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** 邏輯跟 ChangePhoneStartService 完全對稱，見該類別註解說明重新驗證密碼的理由。 */
@Service
public class ChangeEmailStartService implements ChangeEmailStartUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChangeEmailStartService.class);
    private static final String OTP_PURPOSE = "CHANGE_EMAIL";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpCodeStorePort otpCodeStorePort;

    public ChangeEmailStartService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
            OtpCodeStorePort otpCodeStorePort) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpCodeStorePort = otpCodeStorePort;
    }

    @Override
    public ChangeEmailStartResult start(ChangeEmailStartCommand command) {
        Account account = accountRepository.findById(new AccountId(command.accountId()))
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        if (account.getPasswordHash() != null
                && !passwordEncoder.matches(command.currentPassword(), account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String newEmail = new Email(command.newEmail()).value();
        accountRepository.findByIdentifier(newEmail)
                .filter(existing -> !existing.getId().value().equals(command.accountId()))
                .ifPresent(existing -> {
                    throw new IdentifierAlreadyExistsException(newEmail);
                });

        String code = otpCodeStorePort.generateAndStore(newEmail, OTP_PURPOSE);
        log.info("[展示模式] 換Email驗證碼 accountId={} newEmail={} code={}", command.accountId(), newEmail, code);
        return new ChangeEmailStartResult(code);
    }
}
