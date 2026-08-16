package com.tengan.mall.wallet.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberTierMapper extends BaseMapper<MemberTierPO> {

    @Insert("INSERT INTO member_tier (member_id, tier, updated_by, updated_reason) "
            + "VALUES (#{memberId}, #{tier}, #{updatedBy}, #{updatedReason}) "
            + "ON DUPLICATE KEY UPDATE tier = #{tier}, updated_by = #{updatedBy}, updated_reason = #{updatedReason}")
    void upsert(@Param("memberId") Long memberId, @Param("tier") int tier, @Param("updatedBy") String updatedBy,
            @Param("updatedReason") String updatedReason);
}
