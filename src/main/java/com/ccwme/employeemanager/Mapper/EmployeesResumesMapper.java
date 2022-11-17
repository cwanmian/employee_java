package com.ccwme.employeemanager.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccwme.employeemanager.Bean.EmployeesEdu;
import com.ccwme.employeemanager.Bean.EmployeesResume;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeesResumesMapper extends BaseMapper<EmployeesResume> {
}
