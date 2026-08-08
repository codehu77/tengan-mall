package com.tengan.mall.member.application.address;

import com.tengan.mall.member.domain.model.MemberAddress;
import com.tengan.mall.member.domain.repository.MemberAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateAddressService implements CreateAddressUseCase {

    private final MemberAddressRepository memberAddressRepository;

    public CreateAddressService(MemberAddressRepository memberAddressRepository) {
        this.memberAddressRepository = memberAddressRepository;
    }

    @Override
    @Transactional
    public CreateAddressResult create(CreateAddressCommand command) {
        // 第一筆地址天然就是預設，不管呼叫端有沒有勾選；非第一筆才尊重呼叫端的選擇。
        boolean isFirstAddress = !memberAddressRepository.existsByMemberId(command.memberId());
        boolean shouldBeDefault = isFirstAddress || command.isDefault();

        if (shouldBeDefault && !isFirstAddress) {
            memberAddressRepository.clearDefaultForMember(command.memberId());
        }

        MemberAddress address = MemberAddress.create(command.memberId(), command.receiverName(),
                command.receiverPhone(), command.city(), command.district(), command.postalCode(),
                command.street(), shouldBeDefault);
        memberAddressRepository.save(address);
        return new CreateAddressResult(address.getId());
    }
}
