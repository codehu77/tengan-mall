package com.tengan.mall.admin.application.adminuser;

public interface GetAdminUserDetailUseCase {

    GetAdminUserDetailResult getDetail(GetAdminUserDetailQuery query);
}
