package com.tengan.mall.member.application.address;

import java.util.List;

public interface ListAddressesUseCase {

    List<AddressSummary> list(Long memberId);
}
