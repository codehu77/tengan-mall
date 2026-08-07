package com.tengan.mall.member.application.address;

import com.tengan.mall.member.domain.repository.MemberAddressRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListAddressesService implements ListAddressesUseCase {

    private final MemberAddressRepository memberAddressRepository;

    public ListAddressesService(MemberAddressRepository memberAddressRepository) {
        this.memberAddressRepository = memberAddressRepository;
    }

    @Override
    public List<AddressSummary> list(Long memberId) {
        return memberAddressRepository.findByMemberId(memberId).stream()
                .map(a -> new AddressSummary(a.getId(), a.getReceiverName(), a.getReceiverPhone(), a.getAddress(),
                        a.isDefault()))
                .toList();
    }
}
