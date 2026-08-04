package com.tengan.mall.auth.application.sms;

import com.tengan.mall.auth.application.port.SmsCodeStorePort;
import org.springframework.stereotype.Service;

@Service
public class VerifySmsCodeService implements VerifySmsCodeUseCase {

    private final SmsCodeStorePort smsCodeStorePort;

    public VerifySmsCodeService(SmsCodeStorePort smsCodeStorePort) {
        this.smsCodeStorePort = smsCodeStorePort;
    }

    @Override
    public boolean verify(VerifySmsCodeCommand command) {
        return smsCodeStorePort.peek(command.phone(), command.purpose(), command.code());
    }
}
