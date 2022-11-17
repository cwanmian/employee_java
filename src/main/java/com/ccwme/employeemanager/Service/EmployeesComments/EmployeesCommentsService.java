package com.ccwme.employeemanager.Service.EmployeesComments;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccwme.employeemanager.Bean.EmployeesComments;
import com.ccwme.employeemanager.Bean.EmployeesEdu;
import com.ccwme.employeemanager.Mapper.EmployeesCommentsMapper;
import com.ccwme.employeemanager.Mapper.EmployeesEduMapper;
import org.springframework.stereotype.Service;

@Service
public class EmployeesCommentsService extends ServiceImpl<EmployeesCommentsMapper, EmployeesComments> implements EmployeesCommentsServiceImp {
}
