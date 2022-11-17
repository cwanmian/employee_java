package com.ccwme.employeemanager.Service.Users;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccwme.employeemanager.Bean.Users;
import com.ccwme.employeemanager.Mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, Users> implements UserServiceImp {
}
