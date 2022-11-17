package com.ccwme.employeemanager.Service.Employees;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccwme.employeemanager.Bean.Employees;
import com.ccwme.employeemanager.Mapper.EmployeesMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeesService extends ServiceImpl<EmployeesMapper, Employees> implements EmployeesServiceImp {

    @Autowired
    EmployeesMapper employeesMapper;
    public Employees selectWithCompanys(Integer id){
        System.out.println(id);
        return employeesMapper.selectWithCompanys(id);
    }
}
