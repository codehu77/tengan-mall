package com.tengan.mall.member.domain.repository;

import com.tengan.mall.member.domain.model.MemberAddress;
import java.util.List;
import java.util.Optional;

public interface MemberAddressRepository {

    MemberAddress save(MemberAddress address);

    Optional<MemberAddress> findById(Long id);

    List<MemberAddress> findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);

    /** 設新預設地址前，先把該會員名下其他地址的 isDefault 清成 false（同一個 transaction 內）。 */
    void clearDefaultForMember(Long memberId);

    void deleteById(Long id);
}
