package com.tengan.mall.admin.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperLogMapper extends BaseMapper<OperLogPO> {
}
