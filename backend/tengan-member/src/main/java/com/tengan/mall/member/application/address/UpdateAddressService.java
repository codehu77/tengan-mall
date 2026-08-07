package com.tengan.mall.member.application.address;

import com.tengan.mall.member.domain.exception.MemberAddressNotFoundException;
import com.tengan.mall.member.domain.model.MemberAddress;
import com.tengan.mall.member.domain.repository.MemberAddressRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateAddressService implements UpdateAddressUseCase {

    private final MemberAddressRepository memberAddressRepository;

    public UpdateAddressService(MemberAddressRepository memberAddressRepository) {
        this.memberAddressRepository = memberAddressRepository;
    }

    @Override
    public void update(UpdateAddressCommand command) {
        // 地址不屬於這個會員時當 404，不回傳 403——不洩漏「這個 id 存在，只是不是你的」。
        MemberAddress address = memberAddressRepository.findById(command.addressId())
                .filter(a -> a.belongsTo(command.memberId()))
                .orElseThrow(() -> new MemberAddressNotFoundException(command.addressId()));
        address.update(command.receiverName(), command.receiverPhone(), command.address());
        memberAddressRepository.save(address);
    }
}
