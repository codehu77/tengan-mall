package com.tengan.mall.auth.application.register;

import com.tengan.mall.auth.application.port.MemberRegisteredEventPublisherPort;
import com.tengan.mall.auth.application.port.SmsCodeStorePort;
import com.tengan.mall.auth.domain.exception.DuplicatePhoneException;
import com.tengan.mall.auth.domain.exception.DuplicateUsernameException;
import com.tengan.mall.auth.domain.exception.InvalidSmsCodeException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.Phone;
import com.tengan.mall.auth.domain.model.Username;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService implements RegisterUseCase {

    private static final String SMS_PURPOSE = "REGISTER";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsCodeStorePort smsCodeStorePort;
    private final MemberRegisteredEventPublisherPort memberRegisteredEventPublisherPort;

    public RegisterService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
            SmsCodeStorePort smsCodeStorePort, MemberRegisteredEventPublisherPort memberRegisteredEventPublisherPort) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.smsCodeStorePort = smsCodeStorePort;
        this.memberRegisteredEventPublisherPort = memberRegisteredEventPublisherPort;
    }

    @Override
    @Transactional
    public RegisterResult register(RegisterCommand command) {
        if (!smsCodeStorePort.verifyAndConsume(command.phone(), SMS_PURPOSE, command.code())) {
            throw new InvalidSmsCodeException();
        }
        if (accountRepository.existsByUsername(command.username())) {
            throw new DuplicateUsernameException(command.username());
        }
        if (accountRepository.existsByPhone(command.phone())) {
            throw new DuplicatePhoneException(command.phone());
        }

        Account account = Account.create(
                new Username(command.username()),
                new Phone(command.phone()),
                passwordEncoder.encode(command.password()));
        Account saved = accountRepository.save(account);

        memberRegisteredEventPublisherPort.publish(saved.getId().value(), command.username(), command.phone());

        return new RegisterResult(saved.getId().value(), command.username());
    }
}
