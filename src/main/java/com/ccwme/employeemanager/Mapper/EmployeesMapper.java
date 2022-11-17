package com.ccwme.employeemanager.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccwme.employeemanager.Bean.Employees;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeesMapper extends BaseMapper<Employees> {
    Employees selectWithCompanys(Integer id);
}
