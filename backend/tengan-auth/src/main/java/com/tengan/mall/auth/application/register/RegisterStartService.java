package com.tengan.mall.auth.application.register;

import com.tengan.mall.auth.application.port.OtpCodeStorePort;
import com.tengan.mall.auth.domain.exception.IdentifierAlreadyExistsException;
import com.tengan.mall.auth.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 三步驟註冊 Step 1：identifier 已存在就擋（不分帳密帳號或純 OAuth 帳號，一律同樣對待——
 * 見 login_register_redesign 設計「不會幫既有帳號補密碼」），否則發 OTP。
 */
@Service
public class RegisterStartService implements RegisterStartUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterStartService.class);
    private static final String OTP_PURPOSE = "REGISTER";

    private final AccountRepository accountRepository;
    private final OtpCodeStorePort otpCodeStorePort;

    public RegisterStartService(AccountRepository accountRepository, OtpCodeStorePort otpCodeStorePort) {
        this.accountRepository = accountRepository;
        this.otpCodeStorePort = otpCodeStorePort;
    }

    @Override
    public RegisterStartResult start(RegisterStartCommand command) {
        String identifier = command.identifier();
        if (accountRepository.existsByIdentifier(identifier)) {
            throw new IdentifierAlreadyExistsException(identifier);
        }

        String code = otpCodeStorePort.generateAndStore(identifier, OTP_PURPOSE);
        log.info("[展示模式] 註冊驗證碼 identifier={} code={}", identifier, code);
        return new RegisterStartResult(code);
    }
}
