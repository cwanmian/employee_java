package com.ccwme.bugmanager.Service.Employees;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccwme.bugmanager.Bean.Employees;
import com.ccwme.bugmanager.Mapper.EmployeesMapper;
import org.springframework.stereotype.Service;

@Service
public class EmployeesService extends ServiceImpl<EmployeesMapper, Employees> implements EmployeesServiceImp {
}
