package com.ccwme.employeemanager.Service.CompanyList;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccwme.employeemanager.Bean.Companylist;
import com.ccwme.employeemanager.Mapper.CompanyListMapper;
import org.springframework.stereotype.Service;

@Service
public class CompanyListService extends ServiceImpl<CompanyListMapper, Companylist> implements CompanyListServiceImp {
}
