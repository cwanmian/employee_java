package com.ccwme.employeemanager.Service.EmployeesResumes;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccwme.employeemanager.Bean.EmployeesEdu;
import com.ccwme.employeemanager.Bean.EmployeesResume;
import com.ccwme.employeemanager.Mapper.EmployeesEduMapper;
import com.ccwme.employeemanager.Mapper.EmployeesResumesMapper;
import org.springframework.stereotype.Service;

@Service
public class EmployeesResumesService extends ServiceImpl<EmployeesResumesMapper, EmployeesResume> implements EmployeesResumesServiceImp {
}
