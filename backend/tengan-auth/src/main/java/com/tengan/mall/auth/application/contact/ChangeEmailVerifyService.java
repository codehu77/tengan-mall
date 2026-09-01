package com.tengan.mall.auth.application.contact;

import com.tengan.mall.auth.application.port.AccountContactChangedEventPublisherPort;
import com.tengan.mall.auth.application.port.OtpCodeStorePort;
import com.tengan.mall.auth.domain.exception.AccountNotFoundException;
import com.tengan.mall.auth.domain.exception.IdentifierAlreadyExistsException;
import com.tengan.mall.auth.domain.exception.InvalidOtpCodeException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.model.Email;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeEmailVerifyService implements ChangeEmailVerifyUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChangeEmailVerifyService.class);
    private static final String OTP_PURPOSE = "CHANGE_EMAIL";

    private final AccountRepository accountRepository;
    private final OtpCodeStorePort otpCodeStorePort;
    private final AccountContactChangedEventPublisherPort accountContactChangedEventPublisherPort;

    public ChangeEmailVerifyService(AccountRepository accountRepository, OtpCodeStorePort otpCodeStorePort,
            AccountContactChangedEventPublisherPort accountContactChangedEventPublisherPort) {
        this.accountRepository = accountRepository;
        this.otpCodeStorePort = otpCodeStorePort;
        this.accountContactChangedEventPublisherPort = accountContactChangedEventPublisherPort;
    }

    @Override
    @Transactional
    public void verify(ChangeEmailVerifyCommand command) {
        String newEmail = new Email(command.newEmail()).value();
        if (!otpCodeStorePort.verifyAndConsume(newEmail, OTP_PURPOSE, command.code())) {
            throw new InvalidOtpCodeException();
        }

        accountRepository.findByIdentifier(newEmail)
                .filter(existing -> !existing.getId().value().equals(command.accountId()))
                .ifPresent(existing -> {
                    throw new IdentifierAlreadyExistsException(newEmail);
                });

        Account account = accountRepository.findById(new AccountId(command.accountId()))
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));
        String oldEmail = account.getEmail() != null ? account.getEmail().value() : null;

        account.changeEmail(new Email(newEmail));
        accountRepository.save(account);

        accountContactChangedEventPublisherPort.publish(command.accountId(),
                account.getPhone() != null ? account.getPhone().value() : null, newEmail);

        log.info("[安全提示] 帳號 {} 的Email已從 {} 改成 {}", command.accountId(), oldEmail, newEmail);
    }
}
