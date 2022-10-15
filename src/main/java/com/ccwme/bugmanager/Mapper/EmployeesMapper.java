package com.ccwme.bugmanager.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccwme.bugmanager.Bean.Employees;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeesMapper extends BaseMapper<Employees> {
}
