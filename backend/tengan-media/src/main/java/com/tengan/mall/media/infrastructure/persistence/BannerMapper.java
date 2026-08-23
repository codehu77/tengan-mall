package com.tengan.mall.media.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BannerMapper extends BaseMapper<BannerPO> {

    @Select("SELECT * FROM banner ORDER BY sort_order ASC, id ASC")
    List<BannerPO> findAllOrdered();

    @Select("SELECT * FROM banner WHERE enabled = 1 ORDER BY sort_order ASC, id ASC")
    List<BannerPO> findAllEnabledOrdered();
}
