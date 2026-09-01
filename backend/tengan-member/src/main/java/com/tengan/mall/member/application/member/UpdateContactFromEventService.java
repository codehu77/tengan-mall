package com.tengan.mall.member.application.member;

import com.tengan.mall.member.domain.model.Member;
import com.tengan.mall.member.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;

/**
 * 消費 tengan-auth 的 account.contact_changed 事件，更新這裡的唯讀快照。找不到 member row
 * 理論上不會發生（account 一定先於這個事件存在），但比照既有 MQ 消費端的寬容處理，略過不拋錯。
 */
@Service
public class UpdateContactFromEventService implements UpdateContactFromEventUseCase {

    private final MemberRepository memberRepository;

    public UpdateContactFromEventService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void update(UpdateContactFromEventCommand command) {
        memberRepository.findById(command.memberId()).ifPresent(member -> {
            member.updateContact(command.phone(), command.email());
            memberRepository.save(member);
        });
    }
}
