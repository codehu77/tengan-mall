package com.tengan.mall.member.domain.repository;

import com.tengan.mall.member.domain.model.Member;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);

    /** 供其他服務透過 tengan-admin 批次組裝顯示用（例如付款/訂閱列表要秀會員帳號），不保證回傳順序。 */
    List<Member> findByIds(List<Long> ids);

    boolean existsById(Long id);

    /** 後台會員列表用，keyword 比對 nickname/phone/email。狀態不在這裡篩——member 沒有
     * status 這個概念（見 Member 的 javadoc），要依停權狀態篩選是 tengan-admin 那層的事。 */
    MemberPage search(String keyword, Instant createdFrom, Instant createdTo, int pageNum, int pageSize);

    long countCreatedToday();
}
