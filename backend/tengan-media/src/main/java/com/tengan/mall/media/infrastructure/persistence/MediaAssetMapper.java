package com.tengan.mall.media.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MediaAssetMapper extends BaseMapper<MediaAssetPO> {

    @Select("<script>SELECT * FROM media_asset WHERE url IN "
            + "<foreach collection='urls' item='url' open='(' separator=',' close=')'>#{url}</foreach>"
            + "</script>")
    List<MediaAssetPO> findByUrlIn(@Param("urls") List<String> urls);

    @Select("SELECT * FROM media_asset WHERE owner_type = #{ownerType} AND owner_id = #{ownerId} AND status = 2")
    List<MediaAssetPO> findConfirmedByOwner(@Param("ownerType") String ownerType, @Param("ownerId") Long ownerId);

    @Select("SELECT * FROM media_asset WHERE status = 1 AND updated_at < #{cutoff} "
            + "ORDER BY updated_at ASC LIMIT #{limit}")
    List<MediaAssetPO> findExpiredPending(@Param("cutoff") LocalDateTime cutoff, @Param("limit") int limit);
}
