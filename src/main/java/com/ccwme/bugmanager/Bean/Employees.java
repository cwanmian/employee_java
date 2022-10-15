package com.ccwme.bugmanager.Bean;

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
@TableName(value = "employees")
public class Employees {
    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    Integer id;
    @ExcelIgnore
    String uid;
    @ExcelProperty(index = 1)
    String name;
    @ExcelProperty(index = 0)
    String photo;
    @ExcelProperty(index = 2)
    String gender;
    @ExcelProperty(index = 4)
    String tel;
    @ExcelProperty(index = 7)
    String base;
    @ExcelProperty(index = 25)
    String birth;
    @ExcelProperty(index = 26)
    String email;
    @ExcelIgnore
    String isdel;
    @ExcelIgnore
    String adddate;
}
