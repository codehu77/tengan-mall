package com.tengan.mall.auth.application.register;

import com.tengan.mall.auth.application.port.OtpCodeStorePort;
import com.tengan.mall.auth.application.port.VerificationTokenStorePort;
import com.tengan.mall.auth.domain.exception.InvalidOtpCodeException;
import java.time.Duration;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 三步驟註冊 Step 2：這裡才是真正消費驗證碼的時機。成功後發 registrationToken（TTL 10 分鐘），
 * 前端只負責原封不動把它帶到 Step 3，不能自帶 identifier 過去被竄改。
 */
@Service
public class RegisterVerifyService implements RegisterVerifyUseCase {

    private static final String OTP_PURPOSE = "REGISTER";
    private static final Duration TOKEN_TTL = Duration.ofMinutes(10);

    private final OtpCodeStorePort otpCodeStorePort;
    private final VerificationTokenStorePort verificationTokenStorePort;

    public RegisterVerifyService(OtpCodeStorePort otpCodeStorePort,
            VerificationTokenStorePort verificationTokenStorePort) {
        this.otpCodeStorePort = otpCodeStorePort;
        this.verificationTokenStorePort = verificationTokenStorePort;
    }

    @Override
    public RegisterVerifyResult verify(RegisterVerifyCommand command) {
        if (!otpCodeStorePort.verifyAndConsume(command.identifier(), OTP_PURPOSE, command.code())) {
            throw new InvalidOtpCodeException();
        }

        String registrationToken = verificationTokenStorePort.issue(
                Map.of("identifier", command.identifier()), TOKEN_TTL);
        return new RegisterVerifyResult(registrationToken);
    }
}
