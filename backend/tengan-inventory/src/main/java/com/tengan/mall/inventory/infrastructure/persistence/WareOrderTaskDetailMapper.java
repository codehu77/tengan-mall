package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WareOrderTaskDetailMapper extends BaseMapper<WareOrderTaskDetailPO> {

    @Select("SELECT * FROM ware_order_task_detail WHERE task_id = #{taskId}")
    List<WareOrderTaskDetailPO> findByTaskId(@Param("taskId") Long taskId);
}
