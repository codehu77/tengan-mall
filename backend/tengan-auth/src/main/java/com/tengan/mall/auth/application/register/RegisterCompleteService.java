package com.tengan.mall.auth.application.register;

import com.tengan.mall.auth.application.port.AccessTokenIssuerPort;
import com.tengan.mall.auth.application.port.MemberRegisteredEventPublisherPort;
import com.tengan.mall.auth.application.port.RefreshTokenStorePort;
import com.tengan.mall.auth.application.port.VerificationTokenStorePort;
import com.tengan.mall.auth.domain.exception.IdentifierAlreadyExistsException;
import com.tengan.mall.auth.domain.exception.InvalidOrExpiredVerificationTokenException;
import com.tengan.mall.auth.domain.model.Account;
import com.tengan.mall.auth.domain.model.Email;
import com.tengan.mall.auth.domain.model.Phone;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 三步驟註冊 Step 3：消費 registrationToken、建帳號、發 member.registered 事件、
 * auto-login（視同 rememberMe=true，見 login_register_redesign「remember-me 語意擴大」）。
 */
@Service
public class RegisterCompleteService implements RegisterCompleteUseCase {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenStorePort verificationTokenStorePort;
    private final MemberRegisteredEventPublisherPort memberRegisteredEventPublisherPort;
    private final AccessTokenIssuerPort accessTokenIssuerPort;
    private final RefreshTokenStorePort refreshTokenStorePort;

    public RegisterCompleteService(AccountRepository accountRepository, PasswordEncoder passwordEncoder,
            VerificationTokenStorePort verificationTokenStorePort,
            MemberRegisteredEventPublisherPort memberRegisteredEventPublisherPort,
            AccessTokenIssuerPort accessTokenIssuerPort, RefreshTokenStorePort refreshTokenStorePort) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenStorePort = verificationTokenStorePort;
        this.memberRegisteredEventPublisherPort = memberRegisteredEventPublisherPort;
        this.accessTokenIssuerPort = accessTokenIssuerPort;
        this.refreshTokenStorePort = refreshTokenStorePort;
    }

    @Override
    @Transactional
    public RegisterCompleteResult complete(RegisterCompleteCommand command) {
        Map<String, String> payload = verificationTokenStorePort.consume(command.registrationToken())
                .orElseThrow(InvalidOrExpiredVerificationTokenException::new);
        String identifier = payload.get("identifier");

        if (accountRepository.existsByIdentifier(identifier)) {
            throw new IdentifierAlreadyExistsException(identifier);
        }

        boolean isEmail = identifier.contains("@");
        Phone phone = isEmail ? null : new Phone(identifier);
        Email email = isEmail ? new Email(identifier) : null;

        Account account = Account.create(phone, email, passwordEncoder.encode(command.password()));
        Account saved = accountRepository.save(account);
        Long accountId = saved.getId().value();

        memberRegisteredEventPublisherPort.publish(accountId, isEmail ? null : identifier,
                isEmail ? identifier : null);

        String accessToken = accessTokenIssuerPort.issue(saved.getId());
        String refreshToken = refreshTokenStorePort.issue(accountId, true);
        long ttlSeconds = refreshTokenStorePort.ttlSecondsFor(true);

        return new RegisterCompleteResult(accountId, accessToken, refreshToken, ttlSeconds);
    }
}
