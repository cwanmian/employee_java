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
@TableName(value = "employeesedu")
public class EmployeesEdu {
    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    Integer id;
    @ExcelIgnore
    String uid;
    @ExcelProperty(index = 18)
    String starttime;
    @ExcelProperty(index = 18)
    String endtime;
    @ExcelProperty(index = 18)
    String university;
    @ExcelIgnore
    String universitylogo;
    @ExcelProperty(index = 19)
    String major;
    @ExcelProperty(index = 20)
    String degree;
    @ExcelProperty(index = 21)
    String yearsalary;
    @ExcelProperty(index = 22)
    String monthsalary;
    @ExcelProperty(index = 23)
    String months;
    @ExcelProperty(index = 24)
    String bonus;
    @ExcelIgnore
    String isdel;
    @ExcelIgnore
    String adddate;
}
