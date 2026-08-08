package com.tengan.mall.member.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tengan.mall.member.domain.model.MemberAddress;
import com.tengan.mall.member.domain.repository.MemberAddressRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemberAddressRepositoryImpl implements MemberAddressRepository {

    private final MemberAddressMapper memberAddressMapper;

    public MemberAddressRepositoryImpl(MemberAddressMapper memberAddressMapper) {
        this.memberAddressMapper = memberAddressMapper;
    }

    @Override
    public MemberAddress save(MemberAddress address) {
        MemberAddressPO po = toPO(address);
        if (po.getId() == null) {
            memberAddressMapper.insert(po);
            address.assignId(po.getId());
        } else {
            memberAddressMapper.updateById(po);
        }
        return address;
    }

    @Override
    public Optional<MemberAddress> findById(Long id) {
        return Optional.ofNullable(memberAddressMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<MemberAddress> findByMemberId(Long memberId) {
        LambdaQueryWrapper<MemberAddressPO> wrapper = new LambdaQueryWrapper<MemberAddressPO>()
                .eq(MemberAddressPO::getMemberId, memberId)
                .orderByDesc(MemberAddressPO::getIsDefault)
                .orderByDesc(MemberAddressPO::getCreatedAt);
        return memberAddressMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        LambdaQueryWrapper<MemberAddressPO> wrapper = new LambdaQueryWrapper<MemberAddressPO>()
                .eq(MemberAddressPO::getMemberId, memberId);
        return memberAddressMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void clearDefaultForMember(Long memberId) {
        LambdaUpdateWrapper<MemberAddressPO> wrapper = new LambdaUpdateWrapper<MemberAddressPO>()
                .eq(MemberAddressPO::getMemberId, memberId)
                .eq(MemberAddressPO::getIsDefault, true)
                .set(MemberAddressPO::getIsDefault, false);
        memberAddressMapper.update(null, wrapper);
    }

    @Override
    public void deleteById(Long id) {
        memberAddressMapper.deleteById(id);
    }

    private MemberAddressPO toPO(MemberAddress address) {
        MemberAddressPO po = new MemberAddressPO();
        po.setId(address.getId());
        po.setMemberId(address.getMemberId());
        po.setReceiverName(address.getReceiverName());
        po.setReceiverPhone(address.getReceiverPhone());
        po.setCity(address.getCity());
        po.setDistrict(address.getDistrict());
        po.setPostalCode(address.getPostalCode());
        po.setStreet(address.getStreet());
        po.setIsDefault(address.isDefault());
        return po;
    }

    private MemberAddress toDomain(MemberAddressPO po) {
        return MemberAddress.reconstitute(po.getId(), po.getMemberId(), po.getReceiverName(), po.getReceiverPhone(),
                po.getCity(), po.getDistrict(), po.getPostalCode(), po.getStreet(),
                Boolean.TRUE.equals(po.getIsDefault()));
    }
}
