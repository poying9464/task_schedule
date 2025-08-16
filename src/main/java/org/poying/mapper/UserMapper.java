package org.poying.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.poying.vo.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
}