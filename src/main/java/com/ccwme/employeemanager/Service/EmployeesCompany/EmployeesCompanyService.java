package com.ccwme.employeemanager.Service.EmployeesCompany;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccwme.employeemanager.Bean.EmployeesCompany;
import com.ccwme.employeemanager.Mapper.EmployeesCompanyMapper;
import org.springframework.stereotype.Service;

@Service
public class EmployeesCompanyService extends ServiceImpl<EmployeesCompanyMapper, EmployeesCompany> implements EmployeesCompanyServiceImp {
}
