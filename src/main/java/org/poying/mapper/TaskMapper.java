package org.poying.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.poying.entity.Task;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
}