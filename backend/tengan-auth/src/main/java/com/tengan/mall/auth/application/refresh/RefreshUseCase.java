package com.tengan.mall.auth.application.refresh;

public interface RefreshUseCase {

    RefreshResult refresh(RefreshCommand command);
}
