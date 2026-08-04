package com.tengan.mall.auth.application.sms;

import com.tengan.mall.auth.application.port.SmsCodeStorePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendSmsCodeService implements SendSmsCodeUseCase {

    private static final Logger log = LoggerFactory.getLogger(SendSmsCodeService.class);

    private final SmsCodeStorePort smsCodeStorePort;

    public SendSmsCodeService(SmsCodeStorePort smsCodeStorePort) {
        this.smsCodeStorePort = smsCodeStorePort;
    }

    @Override
    public String sendCode(SendSmsCodeCommand command) {
        String code = smsCodeStorePort.generateAndStore(command.phone(), command.purpose());
        log.info("[展示模式] 簡訊驗證碼 phone={} purpose={} code={}", command.phone(), command.purpose(), code);
        return code;
    }
}
