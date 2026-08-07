package com.tengan.mall.member.domain.repository;

import com.tengan.mall.member.domain.model.Member;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);

    boolean existsById(Long id);

    /** 後台會員列表用，keyword 比對 username/nickname/phone。狀態不在這裡篩——member 沒有
     * status 這個概念（見 Member 的 javadoc），要依停權狀態篩選是 tengan-admin 那層的事。 */
    MemberPage search(String keyword, int pageNum, int pageSize);

    long countCreatedToday();
}
