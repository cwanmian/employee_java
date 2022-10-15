package com.ccwme.bugmanager.Service.EmployeesCompany;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccwme.bugmanager.Bean.EmployeesCompany;
import com.ccwme.bugmanager.Mapper.EmployeesCompanyMapper;
import org.springframework.stereotype.Service;

@Service
public class EmployeesCompanyService extends ServiceImpl<EmployeesCompanyMapper, EmployeesCompany> implements EmployeesCompanyServiceImp {
}
