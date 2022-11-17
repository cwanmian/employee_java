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
@TableName(value = "employeescompany")
public class EmployeesCompany {
    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    Integer id;
    @ExcelIgnore
    String uid;
    @ExcelProperty(index = 5)
    String field;
    @ExcelProperty(index = 6)
    String stage;
    @ExcelProperty(index = 8)
    String starttime;
    @ExcelProperty(index = 10)
    String endtime;
    @ExcelProperty(index = 12)
    String company;
    @ExcelProperty(index = 11)
    String companylogo;
    @ExcelProperty(index = 13)
    String zhiwei;
    @ExcelProperty(index = 16)
    String notes;
    @ExcelIgnore
    String isdel;
    @ExcelIgnore
    String adddate;

}
