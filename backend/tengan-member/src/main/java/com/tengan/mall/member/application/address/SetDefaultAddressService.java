package com.tengan.mall.member.application.address;

import com.tengan.mall.member.domain.exception.MemberAddressNotFoundException;
import com.tengan.mall.member.domain.model.MemberAddress;
import com.tengan.mall.member.domain.repository.MemberAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetDefaultAddressService implements SetDefaultAddressUseCase {

    private final MemberAddressRepository memberAddressRepository;

    public SetDefaultAddressService(MemberAddressRepository memberAddressRepository) {
        this.memberAddressRepository = memberAddressRepository;
    }

    @Override
    @Transactional
    public void setDefault(SetDefaultAddressCommand command) {
        MemberAddress address = memberAddressRepository.findById(command.addressId())
                .filter(a -> a.belongsTo(command.memberId()))
                .orElseThrow(() -> new MemberAddressNotFoundException(command.addressId()));
        memberAddressRepository.clearDefaultForMember(command.memberId());
        address.markAsDefault();
        memberAddressRepository.save(address);
    }
}
