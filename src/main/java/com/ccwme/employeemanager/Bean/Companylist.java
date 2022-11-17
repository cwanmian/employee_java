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
@TableName(value = "companylist")
public class Companylist {
    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    Integer id;
    @ExcelProperty(index = 1)
    String name;
    @ExcelIgnore
    String number;
    @ExcelProperty(index = 2)
    String enname;
    @ExcelProperty(index = 0)
    String logo;
    @ExcelProperty(index = 3)
    String web;
    @ExcelProperty(index = 4)
    String address;
    @ExcelProperty(index = 5)
    String notes;
    @ExcelProperty(index = 6)
    String type;
    @ExcelProperty(index = 7)
    String stock;
    @ExcelIgnore
    String isdel;
    @ExcelIgnore
    String adddate;
}
