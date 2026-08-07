package com.tengan.mall.member.application.member;

import com.tengan.mall.member.domain.model.Member;
import com.tengan.mall.member.domain.repository.MemberRepository;
import org.springframework.stereotype.Service;

/**
 * 消費 tengan-auth 的 member.registered 事件建立 profile row。冪等：訊息可能重送（RabbitMQ
 * at-least-once），existsById 已存在就直接略過，不是拋錯或覆蓋既有資料。
 */
@Service
public class RegisterMemberFromEventService implements RegisterMemberFromEventUseCase {

    private final MemberRepository memberRepository;

    public RegisterMemberFromEventService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void register(RegisterMemberFromEventCommand command) {
        if (memberRepository.existsById(command.memberId())) {
            return;
        }
        Member member = Member.create(command.memberId(), command.username(), command.phone());
        memberRepository.save(member);
    }
}
