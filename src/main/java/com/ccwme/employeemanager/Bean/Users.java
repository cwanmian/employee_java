package com.ccwme.employeemanager.Bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName(value = "users")
public class Users {
    @TableId(type = IdType.AUTO)
    Integer id;
    String uname;
    String pw;
    String uid;
    String role;
    String tel;
    String state;
}
