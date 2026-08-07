package com.tengan.mall.member.application.address;

import com.tengan.mall.member.domain.exception.MemberAddressNotFoundException;
import com.tengan.mall.member.domain.model.MemberAddress;
import com.tengan.mall.member.domain.repository.MemberAddressRepository;
import org.springframework.stereotype.Service;

/**
 * 刪除地址不會自動把另一筆升格為預設——刪完之後可能暫時沒有預設地址，等使用者自己下次挑一筆
 * 呼叫「設為預設」，這次刻意不做自動升格（YAGNI，沒有被要求，避免猜錯使用者想要哪一筆變預設）。
 */
@Service
public class DeleteAddressService implements DeleteAddressUseCase {

    private final MemberAddressRepository memberAddressRepository;

    public DeleteAddressService(MemberAddressRepository memberAddressRepository) {
        this.memberAddressRepository = memberAddressRepository;
    }

    @Override
    public void delete(DeleteAddressCommand command) {
        MemberAddress address = memberAddressRepository.findById(command.addressId())
                .filter(a -> a.belongsTo(command.memberId()))
                .orElseThrow(() -> new MemberAddressNotFoundException(command.addressId()));
        memberAddressRepository.deleteById(address.getId());
    }
}
