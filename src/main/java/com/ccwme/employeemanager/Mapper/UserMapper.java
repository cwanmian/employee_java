package com.ccwme.employeemanager.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccwme.employeemanager.Bean.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<Users> {
}
