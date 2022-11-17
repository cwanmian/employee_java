package com.ccwme.employeemanager.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccwme.employeemanager.Bean.Companylist;
import com.ccwme.employeemanager.Bean.EmployeesCompany;
import com.ccwme.employeemanager.Bean.EmployeesResume;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyListMapper extends BaseMapper<Companylist> {
}
