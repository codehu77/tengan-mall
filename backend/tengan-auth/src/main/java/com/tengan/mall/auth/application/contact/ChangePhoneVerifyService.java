package com.tengan.mall.auth.application.contact;

import com.tengan.mall.auth.application.port.AccountContactChangedEventPublisherPort;
import com.tengan.mall.auth.application.port.OtpCodeStorePort;
import com.tengan.mall.auth.domain.exception.AccountNotFoundException;
import com.tengan.mall.auth.domain.exception.IdentifierAlreadyExistsException;
import com.tengan.mall.auth.domain.exception.InvalidOtpCodeException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.AccountId;
import com.tengan.mall.auth.domain.model.Phone;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangePhoneVerifyService implements ChangePhoneVerifyUseCase {

    private static final Logger log = LoggerFactory.getLogger(ChangePhoneVerifyService.class);
    private static final String OTP_PURPOSE = "CHANGE_PHONE";

    private final AccountRepository accountRepository;
    private final OtpCodeStorePort otpCodeStorePort;
    private final AccountContactChangedEventPublisherPort accountContactChangedEventPublisherPort;

    public ChangePhoneVerifyService(AccountRepository accountRepository, OtpCodeStorePort otpCodeStorePort,
            AccountContactChangedEventPublisherPort accountContactChangedEventPublisherPort) {
        this.accountRepository = accountRepository;
        this.otpCodeStorePort = otpCodeStorePort;
        this.accountContactChangedEventPublisherPort = accountContactChangedEventPublisherPort;
    }

    @Override
    @Transactional
    public void verify(ChangePhoneVerifyCommand command) {
        String newPhone = new Phone(command.newPhone()).value();
        if (!otpCodeStorePort.verifyAndConsume(newPhone, OTP_PURPOSE, command.code())) {
            throw new InvalidOtpCodeException();
        }

        // 防 start→verify 之間的競態：這段空窗期可能有別的帳號搶先用掉這個號碼。
        accountRepository.findByIdentifier(newPhone)
                .filter(existing -> !existing.getId().value().equals(command.accountId()))
                .ifPresent(existing -> {
                    throw new IdentifierAlreadyExistsException(newPhone);
                });

        Account account = accountRepository.findById(new AccountId(command.accountId()))
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));
        String oldPhone = account.getPhone() != null ? account.getPhone().value() : null;

        account.changePhone(new Phone(newPhone));
        accountRepository.save(account);

        accountContactChangedEventPublisherPort.publish(command.accountId(), newPhone,
                account.getEmail() != null ? account.getEmail().value() : null);

        // 比照 Phase 13 綁定成功通知信的既有模式，log-only（見 sms_scope_decision），不接真實簡訊商。
        log.info("[安全提示] 帳號 {} 的電話已從 {} 改成 {}", command.accountId(), oldPhone, newPhone);
    }
}
