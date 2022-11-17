package com.ccwme.employeemanager.Bean;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName(value = "employeescomments")
public class EmployeesComments {
    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    Integer id;
    @ExcelIgnore
    String uid;
    @ExcelProperty(index = 27)
    String comment;
    @ExcelProperty(index = 28)
    String time;
    @ExcelProperty(index = 29)
    String adviser;
    @ExcelIgnore
    String isdel;
    @ExcelIgnore
    String adddate;
}
