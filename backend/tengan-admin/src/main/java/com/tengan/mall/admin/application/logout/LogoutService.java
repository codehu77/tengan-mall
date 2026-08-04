package com.tengan.mall.admin.application.logout;

import com.tengan.mall.admin.application.port.AdminRefreshTokenStorePort;
import org.springframework.stereotype.Service;

/** 只刪除這一顆 refresh token（單一裝置登出），不動同帳號其他裝置的 session。 */
@Service
public class LogoutService implements LogoutUseCase {

    private final AdminRefreshTokenStorePort refreshTokenStorePort;

    public LogoutService(AdminRefreshTokenStorePort refreshTokenStorePort) {
        this.refreshTokenStorePort = refreshTokenStorePort;
    }

    @Override
    public void logout(LogoutCommand command) {
        refreshTokenStorePort.delete(command.refreshToken());
    }
}
