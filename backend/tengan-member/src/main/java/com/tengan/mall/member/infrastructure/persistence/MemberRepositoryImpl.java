package com.tengan.mall.member.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.member.domain.model.Member;
import com.tengan.mall.member.domain.repository.MemberPage;
import com.tengan.mall.member.domain.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberMapper memberMapper;

    public MemberRepositoryImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public Member save(Member member) {
        MemberPO po = toPO(member);
        if (memberMapper.selectById(member.getId()) == null) {
            memberMapper.insert(po);
        } else {
            memberMapper.updateById(po);
        }
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(memberMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return memberMapper.selectById(id) != null;
    }

    @Override
    public MemberPage search(String keyword, int pageNum, int pageSize) {
        LambdaQueryWrapper<MemberPO> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(MemberPO::getUsername, keyword)
                    .or().like(MemberPO::getNickname, keyword)
                    .or().like(MemberPO::getPhone, keyword));
        }
        wrapper.orderByDesc(MemberPO::getCreatedAt);

        IPage<MemberPO> page = memberMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        var items = page.getRecords().stream().map(this::toDomain).toList();
        return new MemberPage(items, page.getTotal());
    }

    @Override
    public long countCreatedToday() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<MemberPO> wrapper = new LambdaQueryWrapper<MemberPO>()
                .ge(MemberPO::getCreatedAt, startOfToday)
                .lt(MemberPO::getCreatedAt, startOfToday.plusDays(1));
        return memberMapper.selectCount(wrapper);
    }

    private MemberPO toPO(Member member) {
        MemberPO po = new MemberPO();
        po.setId(member.getId());
        po.setUsername(member.getUsername());
        po.setPhone(member.getPhone());
        po.setNickname(member.getNickname());
        po.setAvatarUrl(member.getAvatarUrl());
        return po;
    }

    private Member toDomain(MemberPO po) {
        return Member.reconstitute(po.getId(), po.getUsername(), po.getPhone(), po.getNickname(),
                po.getAvatarUrl());
    }
}
